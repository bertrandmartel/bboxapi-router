package fr.bmartel.bboxapi

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.gson.gsonDeserializerOf
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMapError
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import fr.bmartel.bboxapi.model.*
import java.net.HttpCookie
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.schedule

class BboxApi {

    var password: String = ""
    var bboxId: String = ""

    var authenticated: Boolean = false

    class BboxAuthException(error: BboxException) : Exception("Bbox authentication failed : ${error.exception.toString()}") {
        var error = error
    }

    /**
     * whether or not client is blocked by too many attempts.
     */
    var blocked: Boolean = false
        get() = blockedUntil.after(Date())

    /**
     * date until which client is blocked.
     */
    var blockedUntil: Date = Date()

    /**
     * number of login attempts.
     */
    var attempts: Int = 0

    init {
        FuelManager.instance.basePath = "http://bbox.lan/api/v1"
    }


    fun setBasePath(basePath: String) {
        FuelManager.instance.basePath = basePath
    }

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    private inline fun <reified T : Any> authenticateAndExecute(request: Request, noinline handler: (Request, Response, Result<T, FuelError>) -> Unit, json: Boolean = true) {
        authenticate { authResult ->
            val (req, res, exception, cookie) = authResult
            if (exception != null) {
                handler(req, res, Result.error(Exception("failure")).flatMapError {
                    Result.error(FuelError(exception))
                })
            } else {
                bboxId = cookie ?: ""
                if (json) {
                    request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject(deserializer = gsonDeserializerOf(), handler = handler)
                } else {
                    request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseString(handler = handler as (Request, Response, Result<*, FuelError>) -> Unit)
                }
            }
        }
    }

    private inline fun <reified T : Any> authenticateAndExecute(request: Request, handler: Handler<T>, json: Boolean = true) {
        authenticate { authResult ->
            val (req, res, exception, cookie) = authResult
            if (exception != null) {
                handler.failure(req, res, Result.error(FuelError(exception)).error)
            } else {
                bboxId = cookie ?: ""
                if (json) {
                    request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject(deserializer = gsonDeserializerOf(), handler = handler)
                } else {
                    request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseString(handler = handler as Handler<String>)
                }
            }
        }
    }

    private inline fun <reified T : Any> processSecureApi(request: Request, handler: Handler<T>, json: Boolean = true) {
        if (!authenticated) {
            authenticateAndExecute(request, handler, json = json)
        } else {
            if (json) {
                request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject<T>(deserializer = gsonDeserializerOf()) { req, res, result ->
                    if (res.statusCode == 401) {
                        authenticateAndExecute(request = request, handler = handler, json = json)
                    } else {
                        handler.success(req, res, result.get())
                    }
                }
            } else {
                request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseString { req, res, result ->
                    if (res.statusCode == 401) {
                        authenticateAndExecute(request = request, handler = handler, json = json)
                    } else {
                        handler.success(req, res, result.get() as T)
                    }
                }
            }
        }
    }

    private inline fun <reified T : Any> processSecureApi(request: Request, noinline handler: (Request, Response, Result<T, FuelError>) -> Unit, json: Boolean = true) {
        if (!authenticated) {
            authenticateAndExecute(request, handler, json = json)
        } else {
            if (json) {
                request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject<T>(deserializer = gsonDeserializerOf()) { req, res, result ->
                    if (res.statusCode == 401) {
                        authenticateAndExecute(request = request, handler = handler, json = json)
                    } else {
                        handler(req, res, result)
                    }
                }
            } else {
                request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseString { req, res, result ->
                    if (res.statusCode == 401) {
                        authenticateAndExecute(request = request, handler = handler, json = json)
                    } else {
                        handler(req, res, result as Result<T, FuelError>)
                    }
                }
            }
        }
    }

    private inline fun <reified T : Any> authenticateAndExecuteSync(request: Request, json: Boolean = true): Triple<Request, Response, Result<T, FuelError>> {
        val (req, res, exception, cookie) = authenticateSync()
        if (exception != null) {
            return Triple(req, res, Result.error(Exception("failure")).flatMapError {
                Result.error(ex = FuelError(exception = exception))
            })
        } else {
            bboxId = cookie ?: ""
            if (json) {
                return request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject(deserializer = gsonDeserializerOf())
            } else {
                return request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseString() as Triple<Request, Response, Result<T, FuelError>>
            }
        }
    }

    private inline fun <reified T : Any> processSecureApiSync(request: Request, json: Boolean = true): Triple<Request, Response, Result<T, FuelError>> {
        if (!authenticated) {
            return authenticateAndExecuteSync(request = request, json = json)
        } else {
            val triple = if (json) {
                request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject<T>(gsonDeserializerOf())
            } else {
                request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseString()
            }

            return if (triple.second.statusCode == 401) {
                authenticateAndExecuteSync(request = request)
            } else {
                triple as Triple<Request, Response, Result<T, FuelError>>
            }
        }
    }

    private fun processAuth(request: Request, response: Response, result: Result<*, FuelError>): AuthResult {
        when (result) {
            is Result.Failure -> {
                authenticated = false
                attempts++
                var authError: BboxException? = null
                var exception: Exception?
                if (response.data.isNotEmpty()) {
                    try {
                        authError = Gson().fromJson(String(response.data), BboxException::class.java)
                        exception = BboxAuthException(authError)
                    } catch (e: JsonSyntaxException) {
                        exception = e
                    }
                } else {
                    exception = result.getException().exception
                }
                if (authError?.exception?.code?.toInt() == 429 &&
                        authError.exception?.errors != null &&
                        authError.exception?.errors?.isNotEmpty()!!) {
                    val pattern = Pattern.compile("(\\d+) attempts, retry after (\\d+) seconds")
                    val matcher = pattern.matcher(authError.exception?.errors?.get(0)?.reason)
                    if (matcher.find()) {
                        val calendar = Calendar.getInstance() // gets a calendar using the default time zone and locale.
                        calendar.add(Calendar.SECOND, matcher.group(2).toInt())
                        blockedUntil = calendar.time
                    }
                }
                return AuthResult(request = request, response = response, exception = exception, bboxid = null)
            }
            is Result.Success -> {
                authenticated = true
                attempts = 0
                blockedUntil = Date()
                response.headers["Set-Cookie"]?.flatMap { HttpCookie.parse(it) }?.find { it.name == "BBOX_ID" }?.let {
                    bboxId = it.value
                }
                return AuthResult(request = request, response = response, exception = null, bboxid = bboxId)
            }
        }
    }

    fun authenticate(handler: (AuthResult) -> Unit) {
        Fuel.post("/login", parameters = listOf("password" to password, "remember" to 1)).response { request, response, result ->
            handler(processAuth(request = request, response = response, result = result))
        }
    }

    fun authenticateSync(): AuthResult {
        val (request, response, result) = Fuel.post(path = "/login", parameters = listOf("password" to password, "remember" to 1)).responseString()
        return processAuth(request = request, response = response, result = result)
    }

    fun getSummary(handler: (Request, Response, Result<List<Summary>, FuelError>) -> Unit) {
        Fuel.get("/summary").responseObject(gsonDeserializerOf(), handler)
    }

    fun getSummary(handler: Handler<List<Summary>>) {
        Fuel.get("/summary").responseObject(gsonDeserializerOf(), handler)
    }

    fun getSummarySync(): Triple<Request, Response, Result<List<Summary>, FuelError>> {
        return Fuel.get("/summary").responseObject(gsonDeserializerOf())
    }

    fun getXdslInfo(handler: (Request, Response, Result<List<Wan>, FuelError>) -> Unit) {
        Fuel.get("/wan/xdsl").responseObject(gsonDeserializerOf(), handler)
    }

    fun getXdslInfo(handler: Handler<List<Wan>>) {
        Fuel.get("/wan/xdsl").responseObject(gsonDeserializerOf(), handler)
    }

    fun getXdslInfoSync(): Triple<Request, Response, Result<List<Wan>, FuelError>> {
        return Fuel.get("/wan/xdsl").responseObject(gsonDeserializerOf())
    }

    fun getHosts(handler: (Request, Response, Result<List<Hosts>, FuelError>) -> Unit) {
        Fuel.get("/hosts").responseObject(gsonDeserializerOf(), handler)
    }

    fun getHosts(handler: Handler<List<Hosts>>) {
        Fuel.get("/hosts").responseObject(gsonDeserializerOf(), handler)
    }

    fun getHostsSync(): Triple<Request, Response, Result<List<Hosts>, FuelError>> {
        return Fuel.get("/hosts").responseObject(gsonDeserializerOf())
    }

    fun getWanIpInfo(handler: (Request, Response, Result<List<Wan>, FuelError>) -> Unit) {
        Fuel.get("/wan/ip").responseObject(gsonDeserializerOf(), handler)
    }

    fun getWanIpInfo(handler: Handler<List<Wan>>) {
        Fuel.get("/wan/ip").responseObject(gsonDeserializerOf(), handler)
    }

    fun getWanIpInfoSync(): Triple<Request, Response, Result<List<Wan>, FuelError>> {
        return Fuel.get("/wan/ip").responseObject(gsonDeserializerOf())
    }

    fun getDeviceInfo(handler: (Request, Response, Result<List<Device>, FuelError>) -> Unit) {
        Fuel.get("/device").responseObject(gsonDeserializerOf(), handler)
    }

    fun getDeviceInfo(handler: Handler<List<Device>>) {
        Fuel.get("/device").responseObject(gsonDeserializerOf(), handler)
    }

    fun getDeviceInfoSync(): Triple<Request, Response, Result<List<Device>, FuelError>> {
        return Fuel.get("/device").responseObject(gsonDeserializerOf())
    }

    fun getVoipInfo(handler: (Request, Response, Result<List<Voip>, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.get("/voip"), handler = handler)
    }

    fun getVoipInfo(handler: Handler<List<Voip>>) {
        processSecureApi(request = Fuel.get("/voip"), handler = handler)
    }

    fun getVoipInfoSync(): Triple<Request, Response, Result<List<Voip>, FuelError>> {
        return processSecureApiSync(request = Fuel.get("/voip"))
    }

    fun getWirelessInfo(handler: (Request, Response, Result<List<Wireless>, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.get("/wireless"), handler = handler)
    }

    fun getWirelessInfo(handler: Handler<List<Wireless>>) {
        processSecureApi(request = Fuel.get("/wireless"), handler = handler)
    }

    fun getWirelessInfoSync(): Triple<Request, Response, Result<List<Wireless>, FuelError>> {
        return processSecureApiSync(request = Fuel.get("/wireless"))
    }

    fun getCallLogs(line: Line, handler: (Request, Response, Result<List<CallLog>, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.get("/voip/fullcalllog/${if (line == Line.LINE1) 1 else 2}"), handler = handler)
    }

    fun getCallLogs(line: Line, handler: Handler<List<CallLog>>) {
        processSecureApi(request = Fuel.get("/voip/fullcalllog/${if (line == Line.LINE1) 1 else 2}"), handler = handler)
    }

    fun getCallLogsSync(line: Line): Triple<Request, Response, Result<List<CallLog>, FuelError>> {
        return processSecureApiSync(request = Fuel.get("/voip/fullcalllog/${if (line == Line.LINE1) 1 else 2}"))
    }

    fun setWifiState(state: Boolean, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.put("/wireless?radio.enable=${if (state) 1 else 0}"), handler = handler, json = false)
    }

    fun setWifiState(state: Boolean, handler: Handler<String>) {
        processSecureApi(request = Fuel.put("/wireless?radio.enable=${if (state) 1 else 0}"), handler = handler, json = false)
    }

    fun setWifiStateSync(state: Boolean): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(request = Fuel.put("/wireless?radio.enable=${if (state) 1 else 0}"), json = false)
    }

    fun setDisplayState(state: Boolean, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.put("/device/display?luminosity=${if (state) 100 else 0}"), handler = handler, json = false)
    }

    fun setDisplayState(state: Boolean, handler: Handler<String>) {
        processSecureApi(request = Fuel.put("/device/display?luminosity=${if (state) 100 else 0}"), handler = handler, json = false)
    }

    fun setDisplayStateSync(state: Boolean): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(request = Fuel.put("/device/display?luminosity=${if (state) 100 else 0}"), json = false)
    }

    fun voipDial(line: Line, phoneNumber: String, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(
                request = Fuel.put("/voip/dial?line=${if (line == Line.LINE1) 1 else 2}&number=$phoneNumber"),
                handler = handler,
                json = false)
    }

    fun voipDial(line: Line, phoneNumber: String, handler: Handler<String>) {
        processSecureApi(
                request = Fuel.put("/voip/dial?line=${if (line == Line.LINE1) 1 else 2}&number=$phoneNumber"),
                handler = handler,
                json = false)
    }

    fun voipDialSync(line: Line, phoneNumber: String): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(
                request = Fuel.put("/voip/dial?line=${if (line == Line.LINE1) 1 else 2}&number=$phoneNumber"),
                json = false)
    }

    fun getToken(handler: (Request, Response, Result<List<Token>, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.get("/device/token"), handler = handler)
    }

    fun getToken(handler: Handler<List<Token>>) {
        processSecureApi(request = Fuel.get("/device/token"), handler = handler)
    }

    fun getTokenSync(): Triple<Request, Response, Result<List<Token>, FuelError>> {
        return processSecureApiSync(request = Fuel.get("/device/token"))
    }

    fun reboot(handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        getToken { _, _, result ->
            processSecureApi(
                    request = Fuel.post("/device/reboot?btoken=${result.get()[0].device?.token}"),
                    handler = handler,
                    json = false)
        }
    }

    fun reboot(handler: Handler<String>) {
        getToken { _, _, result ->
            processSecureApi(
                    request = Fuel.post("/device/reboot?btoken=${result.get()[0].device?.token}"),
                    handler = handler,
                    json = false)
        }
    }

    fun rebootSync(): Triple<Request, Response, Result<String, FuelError>> {
        val (_, _, result) = getTokenSync()
        return processSecureApiSync(request = Fuel.post("/device/reboot?btoken=${result.get()[0].device?.token}"), json = false)
    }

    fun getWifiMacFilter(handler: (Request, Response, Result<List<Acl>, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.get("/wireless/acl"), handler = handler)
    }

    fun getWifiMacFilter(handler: Handler<List<Acl>>) {
        processSecureApi(request = Fuel.get("/wireless/acl"), handler = handler)
    }

    fun getWifiMacFilterSync(): Triple<Request, Response, Result<List<Acl>, FuelError>> {
        return processSecureApiSync(request = Fuel.get("/wireless/acl"))
    }

    fun setWifiMacFilter(state: Boolean, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.put("/wireless/acl?enable=${if (state) 1 else 0}"), handler = handler, json = false)
    }

    fun setWifiMacFilter(state: Boolean, handler: Handler<String>) {
        processSecureApi(request = Fuel.put("/wireless/acl?enable=${if (state) 1 else 0}"), handler = handler, json = false)
    }

    fun setWifiMacFilterSync(state: Boolean): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(request = Fuel.put("/wireless/acl?enable=${if (state) 1 else 0}"), json = false)
    }

    fun deleteMacFilterRule(ruleIndex: Int, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.delete("/wireless/acl/rules/$ruleIndex"), handler = handler, json = false)
    }

    fun deleteMacFilterRule(ruleIndex: Int, handler: Handler<String>) {
        processSecureApi(request = Fuel.delete("/wireless/acl/rules/$ruleIndex"), handler = handler, json = false)
    }

    fun deleteMacFilterRuleSync(ruleIndex: Int): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(request = Fuel.delete("/wireless/acl/rules/$ruleIndex"), json = false)
    }

    fun updateMacFilterRule(ruleIndex: Int, rule: MacFilterRule, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        val data = listOf(
                "enable" to (if (rule.enable) 1 else 0),
                "macaddress" to rule.macaddress,
                "device" to (if (rule.ip == "") -1 else rule.ip)
        )
        processSecureApi(request = Fuel.put("/wireless/acl/rules/$ruleIndex", data), handler = handler, json = false)
    }

    fun updateMacFilterRule(ruleIndex: Int, rule: MacFilterRule, handler: Handler<String>) {
        val data = listOf(
                "enable" to (if (rule.enable) 1 else 0),
                "macaddress" to rule.macaddress,
                "device" to (if (rule.ip == "") -1 else rule.ip)
        )
        processSecureApi(request = Fuel.put("/wireless/acl/rules/$ruleIndex", data), handler = handler, json = false)
    }

    fun updateMacFilterRuleSync(ruleIndex: Int, rule: MacFilterRule): Triple<Request, Response, Result<String, FuelError>> {
        val data = listOf(
                "enable" to (if (rule.enable) 1 else 0),
                "macaddress" to rule.macaddress,
                "device" to (if (rule.ip == "") -1 else rule.ip)
        )
        return processSecureApiSync(request = Fuel.put("/wireless/acl/rules/$ruleIndex", data), json = false)
    }

    fun createMacFilterRule(rule: MacFilterRule, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        getToken { _, _, result ->
            val data = listOf(
                    "enable" to (if (rule.enable) 1 else 0),
                    "macaddress" to rule.macaddress,
                    "device" to (if (rule.ip == "") -1 else rule.ip)
            )
            processSecureApi(
                    request = Fuel.post("/wireless/acl/rules?btoken=${result.get()[0].device?.token}", parameters = data),
                    handler = handler,
                    json = false)
        }
    }

    fun createMacFilterRule(rule: MacFilterRule, handler: Handler<String>) {
        getToken { _, _, result ->
            val data = listOf(
                    "enable" to (if (rule.enable) 1 else 0),
                    "macaddress" to rule.macaddress,
                    "device" to (if (rule.ip == "") -1 else rule.ip)
            )
            processSecureApi(
                    request = Fuel.post("/wireless/acl/rules?btoken=${result.get()[0].device?.token}", parameters = data),
                    handler = handler,
                    json = false)
        }
    }

    fun createMacFilterRuleSync(rule: MacFilterRule): Triple<Request, Response, Result<String, FuelError>> {
        val (_, _, result) = getTokenSync()
        val data = listOf(
                "enable" to (if (rule.enable) 1 else 0),
                "macaddress" to rule.macaddress,
                "device" to (if (rule.ip == "") -1 else rule.ip)
        )
        return processSecureApiSync(
                request = Fuel.post("/wireless/acl/rules?btoken=${result.get()[0].device?.token}", parameters = data),
                json = false)
    }

    fun createCustomRequest(request: Request, auth: Boolean, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        if (auth) {
            processSecureApi(request = request, handler = handler, json = false)
        } else {
            request.responseString(handler = handler)
        }
    }

    fun createCustomRequest(request: Request, auth: Boolean, handler: Handler<String>) {
        if (auth) {
            processSecureApi(request = request, handler = handler, json = false)
        } else {
            request.responseString(handler = handler)
        }
    }

    fun createCustomRequestSync(request: Request, auth: Boolean): Triple<Request, Response, Result<String, FuelError>> {
        if (auth) {
            return processSecureApiSync(request = request, json = false)
        } else {
            return request.responseString()
        }
    }

    fun logout(handler: (Request, Response, Result<ByteArray, FuelError>) -> Unit) {
        authenticated = false
        Fuel.post("/logout").response(handler)
    }

    fun logout(handler: Handler<ByteArray>) {
        authenticated = false
        Fuel.post("/logout").response(handler)
    }

    fun logoutSync(): Triple<Request, Response, Result<ByteArray, FuelError>> {
        authenticated = false
        return Fuel.post("/logout").response()
    }

    fun startPasswordRecovery(handler: (Request, Response, Result<ByteArray, FuelError>) -> Unit) {
        Fuel.post("/password-recovery").response(handler)
    }

    fun startPasswordRecovery(handler: Handler<ByteArray>) {
        Fuel.post("/password-recovery").response(handler)
    }

    fun startPasswordRecoverySync(): Triple<Request, Response, Result<ByteArray, FuelError>> {
        return Fuel.post("/password-recovery").response()
    }

    fun verifyPasswordRecovery(handler: (Request, Response, Result<List<RecoveryVerify>, Exception>?) -> Unit) {
        Fuel.get("/password-recovery/verify").responseString { req, res, result ->
            when (result) {
                is Result.Failure -> {
                    handler(req, res, null)
                }
                is Result.Success -> {
                    if (result.get().isEmpty()) {
                        res.headers["Set-Cookie"]?.flatMap { HttpCookie.parse(it) }?.find { it.name == "BBOX_ID" }?.let {
                            bboxId = it.value
                            authenticated = true
                        }
                        handler(req, res, null)
                    } else {
                        val data = Result.of(Gson().fromJson<List<RecoveryVerify>>(result.get()))
                        handler(req, res, data)
                    }
                }
            }
        }
    }

    fun verifyPasswordRecovery(handler: Handler<List<RecoveryVerify>?>) {
        Fuel.get("/password-recovery/verify").responseString { req, res, result ->
            when (result) {
                is Result.Failure -> {
                    handler.failure(req, res, result.error)
                }
                is Result.Success -> {
                    if (result.get().isEmpty()) {
                        res.headers["Set-Cookie"]?.flatMap { HttpCookie.parse(it) }?.find { it.name == "BBOX_ID" }?.let {
                            bboxId = it.value
                            authenticated = true
                        }
                        handler.success(req, res, null)
                    } else {
                        handler.success(req, res, Gson().fromJson<List<RecoveryVerify>>(result.get()))
                    }
                }
            }
        }
    }

    fun verifyPasswordRecoverySync(): Triple<Request, Response, Result<List<RecoveryVerify>, Exception>?> {
        val (req, res, result) = Fuel.get("/password-recovery/verify").responseString()
        if (result.component2() != null) {
            return Triple(req, res, null)
        }
        return if (result.get().isEmpty()) {
            res.headers["Set-Cookie"]?.flatMap { HttpCookie.parse(it) }?.find { it.name == "BBOX_ID" }?.let {
                bboxId = it.value
                authenticated = true
            }
            Triple(req, res, null)
        } else {
            Triple(req, res, Result.of(Gson().fromJson<List<RecoveryVerify>>(result.get())))
        }
    }

    fun resetPassword(password: String, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        getToken { _, _, result ->
            val data = listOf(
                    "password" to password
            )
            processSecureApi(
                    request = Fuel.post("/reset-password?btoken=${result.get()[0].device?.token}", parameters = data),
                    handler = { req: Request, res: Response, resetResult: Result<String, FuelError> ->
                        if (res.statusCode == 200) {
                            this.password = password
                        }
                        handler(req, res, resetResult)
                    },
                    json = false)
        }
    }

    fun resetPassword(password: String, handler: Handler<String>) {
        getToken { _, _, result ->
            val data = listOf(
                    "password" to password
            )
            processSecureApi(
                    request = Fuel.post("/reset-password?btoken=${result.get()[0].device?.token}", parameters = data),
                    handler = { req: Request, res: Response, resetResult: Result<String, FuelError> ->
                        if (res.statusCode == 200) {
                            this.password = password
                        }
                        when (resetResult) {
                            is Result.Failure -> {
                                handler.failure(req, res, resetResult.error)
                            }
                            is Result.Success -> {
                                handler.success(req, res, resetResult.get())
                            }
                        }
                    },
                    json = false)
        }
    }

    fun resetPasswordSync(password: String): Triple<Request, Response, Result<String, FuelError>> {
        val (_, _, result) = getTokenSync()
        val data = listOf(
                "password" to password
        )
        val resetResult: Triple<Request, Response, Result<String, FuelError>> = processSecureApiSync(
                request = Fuel.post("/reset-password?btoken=${result.get()[0].device?.token}", parameters = data),
                json = false)
        if (resetResult.second.statusCode == 200) {
            this.password = password
        }
        return resetResult
    }

    fun waitForPushButton(maxDuration: Long, pollInterval: Long = 1000): Boolean {
        val (_, response, _) = startPasswordRecoverySync()
        var listenTimer: Timer? = null
        if (response.statusCode == 200) {
            val (_, response, result) = verifyPasswordRecoverySync()
            if (response.statusCode == 200 && result?.get() == null) {
                return true
            }
            var expire: Int = result?.get()?.get(0)?.expires ?: 0
            if (expire > 0) {
                var stop = false
                listenTimer = Timer()
                listenTimer.schedule(delay = maxDuration) {
                    stop = true
                }
                while (expire > 0 && !stop) {
                    val (_, res, verify) = verifyPasswordRecoverySync()
                    if (res.statusCode == 200 && verify?.get() == null) {
                        return true
                    } else {
                        expire = verify?.get()?.get(0)?.expires ?: 0
                    }
                    Thread.sleep(pollInterval)
                }
            }
        }
        listenTimer?.cancel()
        return false
    }
}