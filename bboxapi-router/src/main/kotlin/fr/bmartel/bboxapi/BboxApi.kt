package fr.bmartel.bboxapi

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.gson.gsonDeserializerOf
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMapError
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import fr.bmartel.bboxapi.model.*
import java.net.HttpCookie
import java.util.*
import java.util.regex.Pattern

class BboxApi {

    private var password: String = ""
    var bboxId: String = ""

    var authenticated: Boolean = false

    class BboxAuthException(error: BboxException.Model) : Exception("Bbox authentication failed : ${error.exception.toString()}") {
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

    fun setPassword(password: String) {
        this.password = password
    }

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
                var authError: BboxException.Model? = null
                var exception: Exception?
                if (response.data.isNotEmpty()) {
                    try {
                        authError = Gson().fromJson(String(response.data), BboxException.Model::class.java)
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

    fun getSummary(handler: (Request, Response, Result<List<Summary.Model>, FuelError>) -> Unit) {
        Fuel.get("/summary").responseObject(gsonDeserializerOf(), handler)
    }

    fun getSummary(handler: Handler<List<Summary.Model>>) {
        Fuel.get("/summary").responseObject(gsonDeserializerOf(), handler)
    }

    fun getSummarySync(): Triple<Request, Response, Result<List<Summary.Model>, FuelError>> {
        return Fuel.get("/summary").responseObject(gsonDeserializerOf())
    }

    fun getXdslInfo(handler: (Request, Response, Result<List<Wan.Model>, FuelError>) -> Unit) {
        Fuel.get("/wan/xdsl").responseObject(gsonDeserializerOf(), handler)
    }

    fun getXdslInfo(handler: Handler<List<Wan.Model>>) {
        Fuel.get("/wan/xdsl").responseObject(gsonDeserializerOf(), handler)
    }

    fun getXdslInfoSync(): Triple<Request, Response, Result<List<Wan.Model>, FuelError>> {
        return Fuel.get("/wan/xdsl").responseObject(gsonDeserializerOf())
    }

    fun getHosts(handler: (Request, Response, Result<List<Hosts.Model>, FuelError>) -> Unit) {
        Fuel.get("/hosts").responseObject(gsonDeserializerOf(), handler)
    }

    fun getHosts(handler: Handler<List<Hosts.Model>>) {
        Fuel.get("/hosts").responseObject(gsonDeserializerOf(), handler)
    }

    fun getHostsSync(): Triple<Request, Response, Result<List<Hosts.Model>, FuelError>> {
        return Fuel.get("/hosts").responseObject(gsonDeserializerOf())
    }

    fun getWanIpInfo(handler: (Request, Response, Result<List<Wan.Model>, FuelError>) -> Unit) {
        Fuel.get("/wan/ip").responseObject(gsonDeserializerOf(), handler)
    }

    fun getWanIpInfo(handler: Handler<List<Wan.Model>>) {
        Fuel.get("/wan/ip").responseObject(gsonDeserializerOf(), handler)
    }

    fun getWanIpInfoSync(): Triple<Request, Response, Result<List<Wan.Model>, FuelError>> {
        return Fuel.get("/wan/ip").responseObject(gsonDeserializerOf())
    }

    fun getDeviceInfo(handler: (Request, Response, Result<List<Device.Model>, FuelError>) -> Unit) {
        Fuel.get("/device").responseObject(gsonDeserializerOf(), handler)
    }

    fun getDeviceInfo(handler: Handler<List<Device.Model>>) {
        Fuel.get("/device").responseObject(gsonDeserializerOf(), handler)
    }

    fun getDeviceInfoSync(): Triple<Request, Response, Result<List<Device.Model>, FuelError>> {
        return Fuel.get("/device").responseObject(gsonDeserializerOf())
    }

    fun getVoipInfo(handler: (Request, Response, Result<List<Voip.Model>, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.get("/voip"), handler = handler)
    }

    fun getVoipInfo(handler: Handler<List<Voip.Model>>) {
        processSecureApi(request = Fuel.get("/voip"), handler = handler)
    }

    fun getVoipInfoSync(): Triple<Request, Response, Result<List<Voip.Model>, FuelError>> {
        return processSecureApiSync(request = Fuel.get("/voip"))
    }

    fun getWirelessInfo(handler: (Request, Response, Result<List<Wireless.Model>, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.get("/wireless"), handler = handler)
    }

    fun getWirelessInfo(handler: Handler<List<Wireless.Model>>) {
        processSecureApi(request = Fuel.get("/wireless"), handler = handler)
    }

    fun getWirelessInfoSync(): Triple<Request, Response, Result<List<Wireless.Model>, FuelError>> {
        return processSecureApiSync(request = Fuel.get("/wireless"))
    }

    fun getCallLogs(line: Voip.Line, handler: (Request, Response, Result<List<CallLog.Model>, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.get("/voip/fullcalllog/${if (line == Voip.Line.LINE1) 1 else 2}"), handler = handler)
    }

    fun getCallLogs(line: Voip.Line, handler: Handler<List<CallLog.Model>>) {
        processSecureApi(request = Fuel.get("/voip/fullcalllog/${if (line == Voip.Line.LINE1) 1 else 2}"), handler = handler)
    }

    fun getCallLogsSync(line: Voip.Line): Triple<Request, Response, Result<List<CallLog.Model>, FuelError>> {
        return processSecureApiSync(request = Fuel.get("/voip/fullcalllog/${if (line == Voip.Line.LINE1) 1 else 2}"))
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

    fun voipDial(line: Voip.Line, phoneNumber: String, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(
                request = Fuel.put("/voip/dial?line=${if (line == Voip.Line.LINE1) 1 else 2}&number=$phoneNumber"),
                handler = handler,
                json = false)
    }

    fun voipDial(line: Voip.Line, phoneNumber: String, handler: Handler<String>) {
        processSecureApi(
                request = Fuel.put("/voip/dial?line=${if (line == Voip.Line.LINE1) 1 else 2}&number=$phoneNumber"),
                handler = handler,
                json = false)
    }

    fun voipDialSync(line: Voip.Line, phoneNumber: String): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(
                request = Fuel.put("/voip/dial?line=${if (line == Voip.Line.LINE1) 1 else 2}&number=$phoneNumber"),
                json = false)
    }

    fun getToken(handler: (Request, Response, Result<List<Token.Model>, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.get("/device/token"), handler = handler)
    }

    fun getToken(handler: Handler<List<Token.Model>>) {
        processSecureApi(request = Fuel.get("/device/token"), handler = handler)
    }

    fun getTokenSync(): Triple<Request, Response, Result<List<Token.Model>, FuelError>> {
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

    fun getWifiMacFilter(handler: (Request, Response, Result<List<Acl.Model>, FuelError>) -> Unit) {
        processSecureApi(request = Fuel.get("/wireless/acl"), handler = handler)
    }

    fun getWifiMacFilter(handler: Handler<List<Acl.Model>>) {
        processSecureApi(request = Fuel.get("/wireless/acl"), handler = handler)
    }

    fun getWifiMacFilterSync(): Triple<Request, Response, Result<List<Acl.Model>, FuelError>> {
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

    fun updateMacFilterRule(ruleIndex: Int, rule: Acl.MacFilterRule, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        val data = listOf(
                "enable" to (if (rule.enable) 1 else 0),
                "macaddress" to rule.macaddress,
                "device" to (if (rule.ip == "") -1 else rule.ip)
        )
        processSecureApi(request = Fuel.put("/wireless/acl/rules/$ruleIndex", data), handler = handler, json = false)
    }

    fun updateMacFilterRule(ruleIndex: Int, rule: Acl.MacFilterRule, handler: Handler<String>) {
        val data = listOf(
                "enable" to (if (rule.enable) 1 else 0),
                "macaddress" to rule.macaddress,
                "device" to (if (rule.ip == "") -1 else rule.ip)
        )
        processSecureApi(request = Fuel.put("/wireless/acl/rules/$ruleIndex", data), handler = handler, json = false)
    }

    fun updateMacFilterRuleSync(ruleIndex: Int, rule: Acl.MacFilterRule): Triple<Request, Response, Result<String, FuelError>> {
        val data = listOf(
                "enable" to (if (rule.enable) 1 else 0),
                "macaddress" to rule.macaddress,
                "device" to (if (rule.ip == "") -1 else rule.ip)
        )
        return processSecureApiSync(request = Fuel.put("/wireless/acl/rules/$ruleIndex", data), json = false)
    }

    fun createMacFilterRule(rule: Acl.MacFilterRule, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
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

    fun createMacFilterRule(rule: Acl.MacFilterRule, handler: Handler<String>) {
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

    fun createMacFilterRuleSync(rule: Acl.MacFilterRule): Triple<Request, Response, Result<String, FuelError>> {
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
}