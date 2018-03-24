package fr.bmartel.bboxapi

import com.google.gson.Gson
import fr.bmartel.bboxapi.model.Acl
import fr.bmartel.bboxapi.model.BboxException
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.net.URLDecoder
import java.util.regex.Pattern

/**
 * Dispatcher for mockwebserver.
 *
 * @author Bertrand Martel
 */
class MockDispatcher : Dispatcher() {

    /**
     * authenticated response error
     */
    private val authError = BboxException.Model(
            BboxException.ApiException(
                    "v1/voip",
                    "401",
                    listOf(BboxException.ApiError(
                            "", "Operation requires authentication"
                    ))
            )
    )

    /**
     * dispatch all the route
     */
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when {
            request.method == "GET" && request.path == "/summary" -> sendResponse(fileName = "summary.json")
            request.method == "GET" && request.path == "/wan/xdsl" -> sendResponse(fileName = "xdsl_info.json")
            request.method == "GET" && request.path == "/hosts" -> sendResponse(fileName = "hosts.json")
            request.method == "GET" && request.path == "/wan/ip" -> sendResponse(fileName = "wan_ip.json")
            request.method == "GET" && request.path == "/device" -> sendResponse(fileName = "device.json")
            request.method == "GET" && request.path.startsWith("/voip/fullcalllog/") -> processGetFullCallLog(request)
            request.method == "GET" && request.path == "/wireless" -> sendAuthenticatedResponse(request = request, fileName = "wireless.json")
            request.method == "GET" && request.path == "/wireless/acl" -> sendAuthenticatedResponse(request = request, fileName = "acl.json")
            request.method == "DELETE" && request.path.startsWith("/wireless/acl/rules/") -> processDeleteAcl(request)
            request.method == "PUT" && request.path.startsWith("/wireless/acl/rules") -> updateAcl(request)
            request.method == "PUT" && request.path.startsWith("/wireless/acl") -> enableDisableAcl(request)
            request.method == "POST" && request.path.startsWith("/wireless/acl/rules") -> createAcl(request)
            request.method == "POST" && request.path.startsWith("/device/reboot") -> reboot(request)
            request.method == "GET" && request.path == "/device/token" -> sendAuthenticatedResponse(request = request, fileName = "token.json")
            request.method == "PUT" && request.path.startsWith("/wireless?radio.enable=") -> updateWireless(request)
            request.method == "PUT" && request.path.startsWith("/voip/dial") -> voipDial(request)
            request.method == "PUT" && request.path.startsWith("/device/display?luminosity=") -> updateLuminosity(request)
            request.method == "GET" && request.path == "/voip" -> sendAuthenticatedResponse(request = request, fileName = "voip.json")
            request.method == "POST" && request.path == "/login" -> login(request)
            else -> MockResponse().setResponseCode(404)
        }
    }

    /**
     * send 200 data response.
     */
    private fun sendResponse(fileName: String): MockResponse {
        return MockResponse().setResponseCode(200).setBody(TestUtils.getResFile(fileName = fileName))
    }

    /**
     * send 200 authenticated data response
     */
    private fun sendAuthenticatedResponse(request: RecordedRequest, fileName: String): MockResponse {
        return dispatchAuthResponse(request, MockResponse().setResponseCode(200).setBody(TestUtils.getResFile(fileName = fileName)))
    }

    /**
     * send back an authenticated response
     */
    private fun dispatchAuthResponse(request: RecordedRequest, mockResponseSuccess: MockResponse): MockResponse {
        return if (request.headers["Cookie"] == "BBOX_ID=${BboxApiTest.cookie}") {
            mockResponseSuccess
        } else {
            MockResponse().setResponseCode(401).setBody(Gson().toJson(authError))
        }
    }

    /**
     * GET /voip/fullcalllog/${line}
     */
    private fun processGetFullCallLog(request: RecordedRequest): MockResponse {
        val pattern = Pattern.compile("/voip/fullcalllog/(\\d+)")
        val matcher = pattern.matcher(request.path)
        if (matcher.find()) {
            val line = matcher.group(1).toInt()
            if (line == 1 || line == 2) {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(200).setBody(TestUtils.getResFile(fileName = "calllog.json")))
            } else {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(404))
            }
        } else {
            return dispatchAuthResponse(request, MockResponse().setResponseCode(404))
        }
    }

    /**
     * DELETE /wireless/acl/${rule}
     */
    private fun processDeleteAcl(request: RecordedRequest): MockResponse {
        val pattern = Pattern.compile("/wireless/acl/rules/(\\d+)")
        val matcher = pattern.matcher(request.path)
        if (matcher.find()) {
            val rule = matcher.group(1).toInt()
            if (rule == 1) {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(200))
            } else {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(404))
            }
        } else {
            return dispatchAuthResponse(request, MockResponse().setResponseCode(404))
        }
    }

    /**
     * PUT /wireless/acl/${rule}
     */
    private fun updateAcl(request: RecordedRequest): MockResponse {
        val pattern = Pattern.compile("/wireless/acl/rules/(\\d+)")
        val matcher = pattern.matcher(request.path)
        val params = TestUtils.splitQuery(request.body.readUtf8())
        if (matcher.find()) {
            val rule = matcher.group(1).toInt()
            if (rule > 0 &&
                    params.containsKey("macaddress") &&
                    params.containsKey("enable") &&
                    params.containsKey("device")) {
                BboxApiTest.macFilterRule = Acl.MacFilterRule(
                        enable = params["enable"]?.get(0)?.toInt() == 1,
                        macaddress = params["macaddress"]?.get(0) ?: "",
                        ip = params["device"]?.get(0) ?: "")
                return dispatchAuthResponse(request, MockResponse().setResponseCode(200))
            } else {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(404))
            }
        } else {
            return dispatchAuthResponse(request, MockResponse().setResponseCode(404))
        }
    }

    /**
     * PUT /wireless/acl
     */
    private fun enableDisableAcl(request: RecordedRequest): MockResponse {
        if (request.path.indexOf("?") != -1) {
            val query = request.path.substringAfter("?")
            val params = TestUtils.splitQuery(query)
            if (params.containsKey("enable") &&
                    (params.get("enable")?.isNotEmpty() == true) &&
                    (params["enable"]?.get(0).equals("0") || params["enable"]?.get(0).equals("1"))) {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(200))
            } else {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(401))
            }
        } else {
            return dispatchAuthResponse(request, MockResponse().setResponseCode(401))
        }
    }

    /**
     * POST /wireless/acl
     */
    private fun createAcl(request: RecordedRequest): MockResponse {
        if (request.path.indexOf("?") != -1) {
            val query = request.path.substringAfter("?")
            val params = TestUtils.splitQuery(query)
            if (params.containsKey("btoken") &&
                    (params.get("btoken")?.isNotEmpty() == true) &&
                    params["btoken"]?.get(0) == BboxApiTest.btoken[0].device?.token) {

                val formData = TestUtils.splitQuery(request.body.readUtf8())

                if (formData.containsKey("macaddress") &&
                        formData.containsKey("enable") &&
                        formData.containsKey("device")) {
                    BboxApiTest.macFilterRule = Acl.MacFilterRule(
                            enable = formData["enable"]?.get(0)?.toInt() == 1,
                            macaddress = formData["macaddress"]?.get(0) ?: "",
                            ip = formData["device"]?.get(0) ?: "")
                    return dispatchAuthResponse(request, MockResponse().setResponseCode(200))
                } else {
                    return dispatchAuthResponse(request, MockResponse().setResponseCode(401))
                }
            } else {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(401))
            }
        } else {
            return dispatchAuthResponse(request, MockResponse().setResponseCode(401))
        }
    }

    /**
     * POST /device/reboot
     */
    private fun reboot(request: RecordedRequest): MockResponse {
        if (request.path.indexOf("?") != -1) {
            val query = request.path.substringAfter("?")
            val params = TestUtils.splitQuery(query)
            if (params.containsKey("btoken") &&
                    (params.get("btoken")?.isNotEmpty() == true) &&
                    params["btoken"]?.get(0) == BboxApiTest.btoken[0].device?.token) {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(200))
            } else {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(401))
            }
        } else {
            return dispatchAuthResponse(request, MockResponse().setResponseCode(401))
        }
    }

    /**
     * PUT /wireless?radio.enable=1
     */
    private fun updateWireless(request: RecordedRequest): MockResponse {
        val pattern = Pattern.compile("/wireless\\?radio.enable=(\\d+)")
        val matcher = pattern.matcher(request.path)
        val outOfRangeError = BboxException.Model(
                BboxException.ApiException(
                        "v1/wireless",
                        "400",
                        listOf(BboxException.ApiError(
                                "radio.enable", "Parameter out of range"
                        ))
                )
        )
        if (matcher.find()) {
            val status = matcher.group(1).toInt()

            if (status == 0 || status == 1) {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(200))
            } else {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(400).setBody(Gson().toJson(outOfRangeError)))
            }
        } else {
            return dispatchAuthResponse(request, MockResponse().setResponseCode(400).setBody(Gson().toJson(outOfRangeError)))
        }
    }

    /**
     * PUT /voip/dial?line=1&number=0123456789
     */
    private fun voipDial(request: RecordedRequest): MockResponse {
        val pattern = Pattern.compile("/voip/dial\\?line=(\\d+)&number=(\\d+)")
        val matcher = pattern.matcher(request.path)
        if (matcher.find()) {
            val line = matcher.group(1).toInt()
            if (line == 1 || line == 2) {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(200).setBody(TestUtils.getResFile(fileName = "calllog.json")))
            } else {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(404))
            }
        } else {
            return dispatchAuthResponse(request, MockResponse().setResponseCode(404))
        }
    }

    /**
     * PUT /device/display?luminosity=100
     */
    private fun updateLuminosity(request: RecordedRequest): MockResponse {
        val pattern = Pattern.compile("/device/display\\?luminosity=(\\d+)")
        val matcher = pattern.matcher(request.path)
        val outOfRangeError = BboxException.Model(
                BboxException.ApiException(
                        "v1/display",
                        "400",
                        listOf(BboxException.ApiError(
                                "luminosity", "Parameter out of range"
                        ))
                )
        )
        if (matcher.find()) {
            val status = matcher.group(1).toInt()
            if (status in 0..100) {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(200))
            } else {
                return dispatchAuthResponse(request, MockResponse().setResponseCode(400).setBody(Gson().toJson(outOfRangeError)))
            }
        } else {
            return dispatchAuthResponse(request, MockResponse().setResponseCode(400).setBody(Gson().toJson(outOfRangeError)))
        }
    }

    /**
     * POST /login
     */
    private fun login(request: RecordedRequest): MockResponse {
        val params = URLDecoder.decode(request.body.readUtf8().replace("+", "%2B"), "UTF-8").replace("%2B", "+").split("&")
        val password = params.map { it -> it.split("=") }.filter { it[0] == "password" }[0][1]
        if (password == "admin@box") {
            return MockResponse()
                    .setResponseCode(200)
                    .addHeader("Set-Cookie", "BBOX_ID=${BboxApiTest.cookie}; Path=/; Version=1; HttpOnly")
                    .setBody("")
        } else {
            BboxApiTest.attempts++
            if (BboxApiTest.attempts >= 3) {
                val authError = BboxException.Model(
                        BboxException.ApiException(
                                "v1/login",
                                "429",
                                listOf(BboxException.ApiError(
                                        "", "${BboxApiTest.attempts} attempts, retry after " + (120 * (BboxApiTest.attempts - 2)) + " seconds "
                                ))
                        )
                )
                return MockResponse()
                        .setResponseCode(429)
                        .addHeader("Set-Cookie", "BBOX_ID=; expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/; Version=1; HttpOnly")
                        .setBody(Gson().toJson(authError))
            } else {
                return MockResponse()
                        .setResponseCode(401)
                        .addHeader("Set-Cookie", "BBOX_ID=; expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/; Version=1; HttpOnly")
                        .setBody("")
            }
        }
    }
}