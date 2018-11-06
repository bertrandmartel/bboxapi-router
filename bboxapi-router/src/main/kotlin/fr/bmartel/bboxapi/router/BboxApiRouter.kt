package fr.bmartel.bboxapi.router

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMapError
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import fr.bmartel.bboxapi.router.model.*
import java.net.HttpCookie
import java.net.UnknownHostException
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.schedule

class BboxApiRouter(val clientId: String? = null, val clientSecret: String? = null) {

    var password: String = ""
    var bboxId: String = ""

    var authenticated: Boolean = false

    class BboxAuthException(error: BboxException) : Exception("Bbox authentication failed : ${error.exception.toString()}") {
        var error = error
    }

    companion object {
        fun getPasswordStrength(input: String, strength: PasswordStrength): Boolean {
            when (strength) {
                PasswordStrength.MEDIUM -> {
                    return checkStrength(
                            input = input,
                            minimumCharCount = 6,
                            minimumLowercaseCount = 1,
                            minimumUppercaseCount = 1,
                            minimumDigitCount = 1,
                            minimumSpecialCharCount = 1)
                }
                PasswordStrength.STRONG -> {
                    return checkStrength(
                            input = input,
                            minimumCharCount = 10,
                            minimumLowercaseCount = 2,
                            minimumUppercaseCount = 1,
                            minimumDigitCount = 2,
                            minimumSpecialCharCount = 1)
                }
            }
        }

        private fun checkStrength(input: String,
                                  minimumCharCount: Int,
                                  minimumLowercaseCount: Int,
                                  minimumUppercaseCount: Int,
                                  minimumDigitCount: Int,
                                  minimumSpecialCharCount: Int): Boolean {
            return (Regex(".{$minimumCharCount,}").find(input)?.value != null &&
                    Regex("([^a-z]*[a-z]){$minimumLowercaseCount,}").find(input)?.value != null &&
                    Regex("([^A-Z]*[A-Z]){$minimumUppercaseCount,}").find(input)?.value != null &&
                    Regex("([^\\d]*\\d){$minimumDigitCount,}").find(input)?.value != null &&
                    Regex("([^\\!\\\"\\#\\\$\\%\\&\\'\\(\\)\\*\\+\\,\\-\\.\\/]*[\\!\\\"\\#\\\$\\%\\&\\'\\(\\)\\*\\+\\,\\-\\.\\/]){$minimumSpecialCharCount,}").find(input)?.value != null // at least 1 special character
                    )
        }
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

    /**
     * use Oauth whether a clientId & clientSecret have been defined
     */
    var useOauth: Boolean = false
        get() = (clientId != null && clientSecret != null)

    val manager = FuelManager()

    init {
        manager.basePath = "https://mabbox.bytel.fr/api/v1"
    }

    fun setBasePath(basePath: String) {
        manager.basePath = basePath
    }

    fun init() {
        val (_, _, result) = buildSummaryRequest().responseString()
        if (result is Result.Failure && result.getException().exception is UnknownHostException) {
            //switching to gestionbbox.lan
            manager.basePath = "http://gestionbbox.lan/api/v1"
        }
    }

    private fun buildLoginRequest(): Request {
        return manager.request(method = Method.POST, path = "/login", param = listOf("password" to password, "remember" to 1))
    }

    private fun buildSummaryRequest(): Request {
        return manager.request(method = Method.GET, path = "/summary")
    }

    private fun buildXdslRequest(): Request {
        return manager.request(method = Method.GET, path = "/wan/xdsl")
    }

    private fun buildHostRequest(): Request {
        return manager.request(method = Method.GET, path = "/hosts")
    }

    private fun buildWanIpInfoRequest(): Request {
        return manager.request(method = Method.GET, path = "/wan/ip")
    }

    private fun buildDeviceInfoRequest(): Request {
        return manager.request(method = Method.GET, path = "/device")
    }

    private fun buildVoipRequest(): Request {
        return manager.request(method = Method.GET, path = "/voip")
    }

    private fun buildWirelessRequest(): Request {
        return manager.request(method = Method.GET, path = "/wireless")
    }

    private fun buildCallLogsRequest(line: Line): Request {
        return manager.request(method = Method.GET, path = "/voip/fullcalllog/${if (line == Line.LINE1) 1 else 2}")
    }

    private fun buildWifiStateRequest(state: Boolean): Request {
        return manager.request(method = Method.PUT, path = "/wireless?radio.enable=${if (state) 1 else 0}")
    }

    private fun buildDisplayStateRequest(state: Boolean): Request {
        return manager.request(method = Method.PUT, path = "/device/display?luminosity=${if (state) 100 else 0}")
    }

    private fun buildVoipDialRequest(line: Line, phoneNumber: String): Request {
        return manager.request(method = Method.PUT, path = "/voip/dial?line=${if (line == Line.LINE1) 1 else 2}&number=$phoneNumber")
    }

    private fun buildTokenRequest(): Request {
        return manager.request(method = Method.GET, path = "/device/token")
    }

    private fun buildRebootRequest(btoken: String?): Request {
        return manager.request(method = Method.POST, path = "/device/reboot?btoken=$btoken")
    }

    private fun buildGetAclRequest(): Request {
        return manager.request(method = Method.GET, path = "/wireless/acl")
    }

    private fun buildSetWifiMacFilterRequest(state: Boolean): Request {
        return manager.request(method = Method.PUT, path = "/wireless/acl?enable=${if (state) 1 else 0}")
    }

    private fun buildDeleteAclRequest(ruleIndex: Int): Request {
        return manager.request(method = Method.DELETE, path = "/wireless/acl/rules/$ruleIndex")
    }

    private fun buildUpdateAclRequest(ruleIndex: Int, rule: MacFilterRule): Request {
        val data = listOf(
                "enable" to (if (rule.enable) 1 else 0),
                "macaddress" to rule.macaddress,
                "device" to (if (rule.ip == "") -1 else rule.ip)
        )
        return manager.request(method = Method.PUT, path = "/wireless/acl/rules/$ruleIndex", param = data)
    }

    private fun buildCreateAclRequest(btoken: String?, rule: MacFilterRule): Request {
        val data = listOf(
                "enable" to (if (rule.enable) 1 else 0),
                "macaddress" to rule.macaddress,
                "device" to (if (rule.ip == "") -1 else rule.ip)
        )
        return manager.request(method = Method.POST, path = "/wireless/acl/rules?btoken=$btoken", param = data)
    }

    private fun buildLogoutRequest(): Request {
        return manager.request(method = Method.POST, path = "/logout")
    }

    private fun buildStartRecoveryRequest(): Request {
        return manager.request(method = Method.POST, path = "/password-recovery")
    }

    private fun buildVerifyRecoveryRequest(): Request {
        return manager.request(method = Method.GET, path = "/password-recovery/verify")
    }

    private fun buildResetPasswordRequest(btoken: String?, password: String): Request {
        val data = listOf(
                "password" to password
        )
        return manager.request(method = Method.POST, path = "/reset-password?btoken=$btoken", param = data)
    }

    private fun buildOauthAuthorizeRequest(grantType: GrantType, responseType: ResponseType): Request {
        val data = mutableListOf(
                "grant_type" to grantType.field,
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "response_type" to responseType.field
        )
        return manager.request(method = Method.POST, path = "/oauth/authorize", param = data)
    }

    private fun buildGetTokenRequest(grantType: GrantType, code: String, scope: List<Scope>, password: String?): Request {
        var scopeStr = ""
        scope.map { scopeStr += "${it.field} " }
        val data = mutableListOf(
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "grant_type" to grantType.field,
                "scope" to "*",
                "code" to code
        )
        if (password != null) {
            data.add("username" to "admin")
            data.add("password" to password)
        }
        return manager.request(method = Method.POST, path = "/oauth/token", param = data)
    }

    private fun buildRefreshTokenRequest(refreshToken: String, scope: List<Scope>): Request {
        var scopeStr = ""
        scope.map { scopeStr += "${it.field} " }
        val data = mutableListOf(
                "grant_type" to GrantType.REFRESH_TOKEN.field,
                "scope" to "*",
                "refresh_token" to refreshToken
        )
        return manager.request(method = Method.POST, path = "/oauth/token", param = data)
    }

    private fun buildServicesRequest(): Request {
        return manager.request(method = Method.GET, path = "/services")
    }

    private fun buildRemoteAccessRequest(enable: Boolean): Request {
        return manager.request(method = Method.PUT, path = "/remote/admin?enable=${if (enable) "1" else "0"}")
    }

    private fun onAuthenticationSuccess(response: Response) {
        response.headers["Set-Cookie"]?.flatMap { HttpCookie.parse(it) }?.find { it.name == "BBOX_ID" }?.let {
            bboxId = it.value
            authenticated = true
            attempts = 0
            blockedUntil = Date()
        }
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
                    request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject(deserializer = object : ResponseDeserializable<T> {
                        override fun deserialize(content: String) = BboxApiUtils.fromJson<T>(content)
                    }, handler = handler)
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
                    request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject(deserializer = object : ResponseDeserializable<T> {
                        override fun deserialize(content: String) = BboxApiUtils.fromJson<T>(content)
                    }, handler = handler)
                } else {
                    request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseString(handler = handler as Handler<String>)
                }
            }
        }
    }

    private inline fun <reified T : Any> processSecureApi(request: Request, handler: Handler<T>, json: Boolean = true) {
        //if (!useOauth) {
        if (!authenticated) {
            authenticateAndExecute(request, handler, json = json)
        } else {
            if (json) {
                request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject<T>(deserializer = object : ResponseDeserializable<T> {
                    override fun deserialize(content: String) = BboxApiUtils.fromJson<T>(content)
                }) { req, res, result ->
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
        /*
        } else {
            //TODO: implement oauth
        }
        */
    }

    private inline fun <reified T : Any> processSecureApi(request: Request, noinline handler: (Request, Response, Result<T, FuelError>) -> Unit, json: Boolean = true) {
        if (!authenticated) {
            authenticateAndExecute(request, handler, json = json)
        } else {
            if (json) {
                request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject<T>(deserializer = object : ResponseDeserializable<T> {
                    override fun deserialize(content: String) = BboxApiUtils.fromJson<T>(content)
                }) { req, res, result ->
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
                return request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject(deserializer = object : ResponseDeserializable<T> {
                    override fun deserialize(content: String) = BboxApiUtils.fromJson<T>(content)
                })
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
                request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseObject<T>(object : ResponseDeserializable<T> {
                    override fun deserialize(content: String) = BboxApiUtils.fromJson<T>(content)
                })
            } else {
                request.header(pairs = *arrayOf("Cookie" to "BBOX_ID=$bboxId")).responseString()
            }

            return if (triple.second.statusCode == 401) {
                authenticateAndExecuteSync(request = request, json = json)
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
                onAuthenticationSuccess(response)
                return AuthResult(request = request, response = response, exception = null, bboxid = bboxId)
            }
        }
    }

    fun authenticate(handler: (AuthResult) -> Unit) {
        buildLoginRequest().response { request, response, result ->
            handler(processAuth(request = request, response = response, result = result))
        }
    }

    fun authenticateSync(): AuthResult {
        val (request, response, result) = buildLoginRequest().responseString()
        return processAuth(request = request, response = response, result = result)
    }

    fun getSummary(handler: (Request, Response, Result<List<Summary>, FuelError>) -> Unit) {
        buildSummaryRequest().responseObject(Summary.Deserializer(), handler)
    }

    fun getSummary(handler: Handler<List<Summary>>) {
        buildSummaryRequest().responseObject(Summary.Deserializer(), handler)
    }

    fun getSummarySync(): Triple<Request, Response, Result<List<Summary>, FuelError>> {
        return buildSummaryRequest().responseObject(Summary.Deserializer())
    }

    fun getXdslInfo(handler: (Request, Response, Result<List<Wan>, FuelError>) -> Unit) {
        buildXdslRequest().responseObject(Wan.Deserializer(), handler)
    }

    fun getXdslInfo(handler: Handler<List<Wan>>) {
        buildXdslRequest().responseObject(Wan.Deserializer(), handler)
    }

    fun getXdslInfoSync(): Triple<Request, Response, Result<List<Wan>, FuelError>> {
        return buildXdslRequest().responseObject(Wan.Deserializer())
    }

    fun getHosts(handler: (Request, Response, Result<List<Hosts>, FuelError>) -> Unit) {
        processSecureApi(request = buildHostRequest(), handler = handler)
    }

    fun getHosts(handler: Handler<List<Hosts>>) {
        processSecureApi(request = buildHostRequest(), handler = handler)
    }

    fun getHostsSync(): Triple<Request, Response, Result<List<Hosts>, FuelError>> {
        return processSecureApiSync(request = buildHostRequest())
    }

    fun getWanIpInfo(handler: (Request, Response, Result<List<Wan>, FuelError>) -> Unit) {
        buildWanIpInfoRequest().responseObject(Wan.Deserializer(), handler)
    }

    fun getWanIpInfo(handler: Handler<List<Wan>>) {
        buildWanIpInfoRequest().responseObject(Wan.Deserializer(), handler)
    }

    fun getWanIpInfoSync(): Triple<Request, Response, Result<List<Wan>, FuelError>> {
        return buildWanIpInfoRequest().responseObject(Wan.Deserializer())
    }

    fun getDeviceInfo(handler: (Request, Response, Result<List<Device>, FuelError>) -> Unit) {
        buildDeviceInfoRequest().responseObject(Device.Deserializer(), handler)
    }

    fun getDeviceInfo(handler: Handler<List<Device>>) {
        buildDeviceInfoRequest().responseObject(Device.Deserializer(), handler)
    }

    fun getDeviceInfoSync(): Triple<Request, Response, Result<List<Device>, FuelError>> {
        return buildDeviceInfoRequest().responseObject(Device.Deserializer())
    }

    fun getVoipInfo(handler: (Request, Response, Result<List<Voip>, FuelError>) -> Unit) {
        processSecureApi(request = buildVoipRequest(), handler = handler)
    }

    fun getVoipInfo(handler: Handler<List<Voip>>) {
        processSecureApi(request = buildVoipRequest(), handler = handler)
    }

    fun getVoipInfoSync(): Triple<Request, Response, Result<List<Voip>, FuelError>> {
        return processSecureApiSync(request = buildVoipRequest())
    }

    fun getWirelessInfo(handler: (Request, Response, Result<List<Wireless>, FuelError>) -> Unit) {
        processSecureApi(request = buildWirelessRequest(), handler = handler)
    }

    fun getWirelessInfo(handler: Handler<List<Wireless>>) {
        processSecureApi(request = buildWirelessRequest(), handler = handler)
    }

    fun getWirelessInfoSync(): Triple<Request, Response, Result<List<Wireless>, FuelError>> {
        return processSecureApiSync(request = buildWirelessRequest())
    }

    fun getCallLogs(line: Line, handler: (Request, Response, Result<List<CallLog>, FuelError>) -> Unit) {
        processSecureApi(request = buildCallLogsRequest(line), handler = handler)
    }

    fun getCallLogs(line: Line, handler: Handler<List<CallLog>>) {
        processSecureApi(request = buildCallLogsRequest(line), handler = handler)
    }

    fun getCallLogsSync(line: Line): Triple<Request, Response, Result<List<CallLog>, FuelError>> {
        return processSecureApiSync(request = buildCallLogsRequest(line))
    }

    fun setWifiState(state: Boolean, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(request = buildWifiStateRequest(state), handler = handler, json = false)
    }

    fun setWifiState(state: Boolean, handler: Handler<String>) {
        processSecureApi(request = buildWifiStateRequest(state), handler = handler, json = false)
    }

    fun setWifiStateSync(state: Boolean): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(request = buildWifiStateRequest(state), json = false)
    }

    fun setDisplayState(state: Boolean, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(request = buildDisplayStateRequest(state), handler = handler, json = false)
    }

    fun setDisplayState(state: Boolean, handler: Handler<String>) {
        processSecureApi(request = buildDisplayStateRequest(state), handler = handler, json = false)
    }

    fun setDisplayStateSync(state: Boolean): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(request = buildDisplayStateRequest(state), json = false)
    }

    fun voipDial(line: Line, phoneNumber: String, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(
                request = buildVoipDialRequest(line, phoneNumber),
                handler = handler,
                json = false)
    }

    fun voipDial(line: Line, phoneNumber: String, handler: Handler<String>) {
        processSecureApi(
                request = buildVoipDialRequest(line, phoneNumber),
                handler = handler,
                json = false)
    }

    fun voipDialSync(line: Line, phoneNumber: String): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(
                request = buildVoipDialRequest(line, phoneNumber),
                json = false)
    }

    fun getBboxToken(handler: (Request, Response, Result<List<Token>, FuelError>) -> Unit) {
        processSecureApi(request = buildTokenRequest(), handler = handler)
    }

    fun getBboxToken(handler: Handler<List<Token>>) {
        processSecureApi(request = buildTokenRequest(), handler = handler)
    }

    fun getBboxTokenSync(): Triple<Request, Response, Result<List<Token>, FuelError>> {
        return processSecureApiSync(request = buildTokenRequest())
    }

    fun reboot(handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        getBboxToken { _, _, result ->
            processSecureApi(
                    request = buildRebootRequest(btoken = result.get()[0].device?.token),
                    handler = handler,
                    json = false)
        }
    }

    fun reboot(handler: Handler<String>) {
        getBboxToken { _, _, result ->
            processSecureApi(
                    request = buildRebootRequest(btoken = result.get()[0].device?.token),
                    handler = handler,
                    json = false)
        }
    }

    fun rebootSync(): Triple<Request, Response, Result<String, FuelError>> {
        val (_, _, result) = getBboxTokenSync()
        return processSecureApiSync(request = buildRebootRequest(btoken = result.get()[0].device?.token), json = false)
    }

    fun getWifiMacFilter(handler: (Request, Response, Result<List<Acl>, FuelError>) -> Unit) {
        processSecureApi(request = buildGetAclRequest(), handler = handler)
    }

    fun getWifiMacFilter(handler: Handler<List<Acl>>) {
        processSecureApi(request = buildGetAclRequest(), handler = handler)
    }

    fun getWifiMacFilterSync(): Triple<Request, Response, Result<List<Acl>, FuelError>> {
        return processSecureApiSync(request = buildGetAclRequest())
    }

    fun setWifiMacFilter(state: Boolean, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(request = buildSetWifiMacFilterRequest(state), handler = handler, json = false)
    }

    fun setWifiMacFilter(state: Boolean, handler: Handler<String>) {
        processSecureApi(request = buildSetWifiMacFilterRequest(state), handler = handler, json = false)
    }

    fun setWifiMacFilterSync(state: Boolean): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(request = buildSetWifiMacFilterRequest(state), json = false)
    }

    fun deleteMacFilterRule(ruleIndex: Int, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(request = buildDeleteAclRequest(ruleIndex), handler = handler, json = false)
    }

    fun deleteMacFilterRule(ruleIndex: Int, handler: Handler<String>) {
        processSecureApi(request = buildDeleteAclRequest(ruleIndex), handler = handler, json = false)
    }

    fun deleteMacFilterRuleSync(ruleIndex: Int): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(request = buildDeleteAclRequest(ruleIndex), json = false)
    }

    fun updateMacFilterRule(ruleIndex: Int, rule: MacFilterRule, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        processSecureApi(request = buildUpdateAclRequest(ruleIndex, rule), handler = handler, json = false)
    }

    fun updateMacFilterRule(ruleIndex: Int, rule: MacFilterRule, handler: Handler<String>) {
        processSecureApi(request = buildUpdateAclRequest(ruleIndex, rule), handler = handler, json = false)
    }

    fun updateMacFilterRuleSync(ruleIndex: Int, rule: MacFilterRule): Triple<Request, Response, Result<String, FuelError>> {
        return processSecureApiSync(request = buildUpdateAclRequest(ruleIndex, rule), json = false)
    }

    fun createMacFilterRule(rule: MacFilterRule, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        getBboxToken { _, _, result ->
            processSecureApi(
                    request = buildCreateAclRequest(btoken = result.get()[0].device?.token, rule = rule),
                    handler = handler,
                    json = false)
        }
    }

    fun createMacFilterRule(rule: MacFilterRule, handler: Handler<String>) {
        getBboxToken { _, _, result ->
            processSecureApi(
                    request = buildCreateAclRequest(btoken = result.get()[0].device?.token, rule = rule),
                    handler = handler,
                    json = false)
        }
    }

    fun createMacFilterRuleSync(rule: MacFilterRule): Triple<Request, Response, Result<String, FuelError>> {
        val (_, _, result) = getBboxTokenSync()
        return processSecureApiSync(
                request = buildCreateAclRequest(btoken = result.get()[0].device?.token, rule = rule),
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
        return if (auth && !password.isEmpty()) {
            processSecureApiSync(request = request, json = false)
        } else {
            request.responseString()
        }
    }

    fun logout(handler: (Request, Response, Result<ByteArray, FuelError>) -> Unit) {
        authenticated = false
        buildLogoutRequest().response(handler)
    }

    fun logout(handler: Handler<ByteArray>) {
        authenticated = false
        buildLogoutRequest().response(handler)
    }

    fun logoutSync(): Triple<Request, Response, Result<ByteArray, FuelError>> {
        authenticated = false
        return buildLogoutRequest().response()
    }

    fun startPasswordRecovery(handler: (Request, Response, Result<ByteArray, FuelError>) -> Unit) {
        buildStartRecoveryRequest().response(handler)
    }

    fun startPasswordRecovery(handler: Handler<ByteArray>) {
        buildStartRecoveryRequest().response(handler)
    }

    fun startPasswordRecoverySync(): Triple<Request, Response, Result<ByteArray, FuelError>> {
        return buildStartRecoveryRequest().response()
    }

    fun verifyPasswordRecovery(handler: (Request, Response, Result<List<RecoveryVerify>, Exception>?) -> Unit) {
        buildVerifyRecoveryRequest().responseString { req, res, result ->
            when (result) {
                is Result.Failure -> {
                    handler(req, res, null)
                }
                is Result.Success -> {
                    if (result.get().isEmpty()) {
                        onAuthenticationSuccess(res)
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
        buildVerifyRecoveryRequest().responseString { req, res, result ->
            when (result) {
                is Result.Failure -> {
                    handler.failure(req, res, result.error)
                }
                is Result.Success -> {
                    if (result.get().isEmpty()) {
                        onAuthenticationSuccess(res)
                        handler.success(req, res, null)
                    } else {
                        handler.success(req, res, Gson().fromJson<List<RecoveryVerify>>(result.get()))
                    }
                }
            }
        }
    }

    fun verifyPasswordRecoverySync(): Triple<Request, Response, Result<List<RecoveryVerify>, Exception>?> {
        val (req, res, result) = buildVerifyRecoveryRequest().responseString()
        if (result.component2() != null) {
            return Triple(req, res, null)
        }
        return if (result.get().isEmpty()) {
            onAuthenticationSuccess(res)
            Triple(req, res, null)
        } else {
            Triple(req, res, Result.of(Gson().fromJson<List<RecoveryVerify>>(result.get())))
        }
    }

    fun resetPassword(password: String, handler: (Request, Response, Result<String, FuelError>) -> Unit) {
        getBboxToken { _, _, result ->
            processSecureApi(
                    request = buildResetPasswordRequest(btoken = result.get()[0].device?.token, password = password),
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
        getBboxToken { _, _, result ->
            processSecureApi(
                    request = buildResetPasswordRequest(btoken = result.get()[0].device?.token, password = password),
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
        val (_, _, result) = getBboxTokenSync()
        val resetResult: Triple<Request, Response, Result<String, FuelError>> = processSecureApiSync(
                request = buildResetPasswordRequest(btoken = result.get()[0].device?.token, password = password),
                json = false)
        if (resetResult.second.statusCode == 200) {
            this.password = password
        }
        return resetResult
    }

    fun waitForPushButton(maxDuration: Long, pollInterval: Long = 1000): Boolean {
        val (_, response, _) = startPasswordRecoverySync()
        if (response.statusCode == 200) {
            return waitForPush(maxDuration, pollInterval)
        }
        return false
    }

    fun authorize(grantType: GrantType, responseType: ResponseType, handler: (Request, Response, Result<CodeResponse, FuelError>) -> Unit) {
        buildOauthAuthorizeRequest(grantType = grantType, responseType = responseType).responseObject(CodeResponse.Deserializer(), handler)
    }

    fun authorize(grantType: GrantType, responseType: ResponseType, handler: Handler<CodeResponse>) {
        buildOauthAuthorizeRequest(grantType = grantType, responseType = responseType).responseObject(CodeResponse.Deserializer(), handler)
    }

    fun authorizeSync(grantType: GrantType, responseType: ResponseType): Triple<Request, Response, Result<CodeResponse, FuelError>> {
        return buildOauthAuthorizeRequest(grantType = grantType, responseType = responseType).responseObject(CodeResponse.Deserializer())
    }

    fun getToken(grantType: GrantType, code: String, scope: List<Scope>, password: String? = null,
                 handler: (Request, Response, Result<TokenResponse, FuelError>) -> Unit) {
        buildGetTokenRequest(grantType = grantType, code = code, scope = scope, password = password).responseObject(TokenResponse.Deserializer(), handler)
    }

    fun getToken(grantType: GrantType, code: String, scope: List<Scope>, password: String? = null, handler: Handler<TokenResponse>) {
        buildGetTokenRequest(grantType = grantType, code = code, scope = scope, password = password).responseObject(TokenResponse.Deserializer(), handler)
    }

    fun getTokenSync(grantType: GrantType, code: String, scope: List<Scope>, password: String? = null): Triple<Request, Response, Result<TokenResponse, FuelError>> {
        return buildGetTokenRequest(grantType = grantType, code = code, scope = scope, password = password).responseObject(TokenResponse.Deserializer())
    }

    fun refreshToken(refreshToken: String,
                     scope: List<Scope>,
                     handler: (Request, Response, Result<TokenResponse, FuelError>) -> Unit) {
        buildRefreshTokenRequest(
                refreshToken = refreshToken,
                scope = scope).responseObject(TokenResponse.Deserializer(), handler)
    }

    fun refreshToken(refreshToken: String,
                     scope: List<Scope>,
                     handler: Handler<TokenResponse>) {
        buildRefreshTokenRequest(
                refreshToken = refreshToken,
                scope = scope).responseObject(TokenResponse.Deserializer(), handler)
    }

    fun refreshTokenSync(refreshToken: String,
                         scope: List<Scope>): Triple<Request, Response, Result<TokenResponse, FuelError>> {
        return buildRefreshTokenRequest(
                refreshToken = refreshToken,
                scope = scope).responseObject(TokenResponse.Deserializer())
    }

    private fun waitForPush(maxDuration: Long, pollInterval: Long = 1000): Boolean {
        var listenTimer: Timer? = null
        val (_, response, result) = verifyPasswordRecoverySync()
        if (response.statusCode == 200 && result?.get() == null) {
            listenTimer?.cancel()
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
                    listenTimer?.cancel()
                    return true
                } else {
                    expire = verify?.get()?.get(0)?.expires ?: 0
                }
                Thread.sleep(pollInterval)
            }
        }
        listenTimer?.cancel()
        return false
    }

    /**
     * authentication using Oauth with code + button
     */
    fun authenticateOauthButton(maxDuration: Long,
                                pollInterval: Long = 1000,
                                scope: List<Scope> = listOf(Scope.ALL)): Triple<Request, Response, Result<*, FuelError>> {
        val authorizeTriple = authorizeSync(grantType = GrantType.BUTTON, responseType = ResponseType.CODE)
        if (authorizeTriple.component2().statusCode == 200) {
            if (waitForPush(maxDuration, pollInterval)) {
                return getTokenSync(
                        grantType = GrantType.BUTTON,
                        code = authorizeTriple.component3().get().code,
                        scope = scope)
            }
            //send custom error failure for pushing button
            return Triple(
                    authorizeTriple.component1(),
                    authorizeTriple.component2(),
                    Result.error(Exception("failure")).flatMapError {
                        Result.error(FuelError(Exception("push button failure")))
                    })
        }
        return authorizeTriple
    }

    /**
     * authentication using Oauth with code + password
     */
    fun authenticateOauthPassword(scope: List<Scope> = listOf(Scope.ALL)): Triple<Request, Response, Result<*, FuelError>> {
        val authorizeTriple = authorizeSync(grantType = GrantType.PASSWORD, responseType = ResponseType.CODE)
        if (authorizeTriple.component2().statusCode == 200) {
            return getTokenSync(
                    grantType = GrantType.PASSWORD,
                    code = authorizeTriple.component3().get().code,
                    password = password,
                    scope = scope)
        }
        return authorizeTriple
    }

    fun getServices(handler: (Request, Response, Result<List<ServiceObject>, FuelError>) -> Unit) {
        buildServicesRequest().responseObject(ServiceObject.Deserializer(), handler)
    }

    fun getServices(handler: Handler<List<ServiceObject>>) {
        buildServicesRequest().responseObject(ServiceObject.Deserializer(), handler)
    }

    fun getServicesSync(): Triple<Request, Response, Result<List<ServiceObject>, FuelError>> {
        return buildServicesRequest().responseObject(ServiceObject.Deserializer())
    }

    fun isRemoteActivable(): Boolean {
        val (_, _, result) = getServicesSync()
        if (result is Result.Success) {
            return result.get()[0].services.remote.admin.activable == 1
        }
        return false
    }

    fun configureRemoteAccess(state: Boolean): Triple<Request, Response, Result<String, FuelError>>? {
        if (!state) {
            return processSecureApiSync(request = buildRemoteAccessRequest(enable = state), json = false)
        } else if (isRemoteActivable()) {
            return processSecureApiSync(request = buildRemoteAccessRequest(enable = state), json = false)
        }
        return null
    }
}