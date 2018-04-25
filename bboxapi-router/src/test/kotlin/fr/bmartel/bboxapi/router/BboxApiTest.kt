package fr.bmartel.bboxapi.router

import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import fr.bmartel.bboxapi.router.model.*
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.junit.*
import org.skyscreamer.jsonassert.JSONAssert
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.schedule

open class BboxApiTest : TestCase() {

    companion object {
        const val clientSecret = "secret"
        const val clientId = "client"
        private val mockServer = MockWebServer()
        private val bboxApi = BboxApiRouter(clientId = clientId, clientSecret = clientSecret)

        val password = "admin@box"

        val cookie = "2.215.7fffffff.a60bd4fcc583ae1b745b5947d1f04da491b01cefcdf39dd9d2676e1a499ecff0a9a3ec6f269a8a9af5e3e1c7d32f6b65df8754ac7af2854c59f367a29136923b.d2614b3af80b5eb35cd890de5760893330372ab2"
        var attempts = 0
        var macFilterRule: MacFilterRule? = null
        var PINCODE = "123456789"
        var changePassword = 0

        const val code = "B1.4.426b.R0IkGG5QpS5i9fOb.GvLWGP2b0D4fMe2HQePsnE9pJdyTe0aA4zCFmjJSfak"
        const val refreshToken = "r1.BtwazByYlp15S-xf.8s6lNYrTUNBXCSMGcGa_1vehZS6hHmUf1tHUiw3ye_k"
        //https://stackoverflow.com/a/33381385/2614364
        inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

        val btoken = Gson().fromJson<List<Token>>(TestUtils.getResFile(fileName = "token.json"))

        @BeforeClass
        @JvmStatic
        fun initMockServer() {
            if (!runOnNetwork()) {
                mockServer.start()
                mockServer.setDispatcher(MockDispatcher())
                bboxApi.setBasePath(basePath = mockServer.url("").toString().dropLast(n = 1))
            }
        }

        fun runOnNetwork(): Boolean {
            return System.getProperty("testMode") != null && System.getProperty("testMode") == "network"
        }
    }

    @Before
    fun setUp() {
        lock = CountDownLatch(1)
        attempts = 0
        bboxApi.attempts = 0
        bboxApi.authenticated = false
        bboxApi.blockedUntil = Date()
        bboxApi.bboxId = ""
        bboxApi.blocked = false
        macFilterRule = null
        changePassword = 0
        if (!runOnNetwork()) {
            bboxApi.setBasePath(basePath = mockServer.url("").toString().dropLast(n = 1))
        }
    }

    @Test
    fun getSummary() {
        TestUtils.executeAsync(testcase = this, filename = "summary.json", body = bboxApi::getSummary)
    }

    @Test
    fun getSummaryCb() {
        TestUtils.executeAsyncCb(testcase = this, filename = "summary.json", body = bboxApi::getSummary)
    }

    @Test
    fun getSummarySync() {
        TestUtils.executeSync(filename = "summary.json", body = bboxApi::getSummarySync)
    }

    @Test
    fun getXdslInfo() {
        TestUtils.executeAsync(testcase = this, filename = "xdsl_info.json", body = bboxApi::getXdslInfo)
    }

    @Test
    fun getXdslInfoCb() {
        TestUtils.executeAsyncCb(testcase = this, filename = "xdsl_info.json", body = bboxApi::getXdslInfo)
    }

    @Test
    fun getXdslInfoSync() {
        TestUtils.executeSync(filename = "xdsl_info.json", body = bboxApi::getXdslInfoSync)
    }

    @Test
    fun getHosts() {
        TestUtils.executeAsync(testcase = this, filename = "hosts.json", body = bboxApi::getHosts)
    }

    @Test
    fun getHostsCb() {
        TestUtils.executeAsyncCb(testcase = this, filename = "hosts.json", body = bboxApi::getHosts)
    }

    @Test
    fun getHostsSync() {
        TestUtils.executeSync(filename = "hosts.json", body = bboxApi::getHostsSync)
    }

    @Test
    fun getWanIpInfo() {
        TestUtils.executeAsync(testcase = this, filename = "wan_ip.json", body = bboxApi::getWanIpInfo)
    }

    @Test
    fun getWanIpInfoCb() {
        TestUtils.executeAsyncCb(testcase = this, filename = "wan_ip.json", body = bboxApi::getWanIpInfo)
    }

    @Test
    fun getWanIpInfoSync() {
        TestUtils.executeSync(filename = "wan_ip.json", body = bboxApi::getWanIpInfoSync)
    }

    @Test
    fun getDeviceInfo() {
        TestUtils.executeAsync(testcase = this, filename = "device.json", body = bboxApi::getDeviceInfo)
    }

    @Test
    fun getDeviceInfoCb() {
        TestUtils.executeAsyncCb(testcase = this, filename = "device.json", body = bboxApi::getDeviceInfo)
    }

    @Test
    fun getDeviceInfoSync() {
        TestUtils.executeSync(filename = "device.json", body = bboxApi::getDeviceInfoSync)
    }

    @Test
    fun blockClientSync() {
        Assume.assumeFalse(runOnNetwork())
        bboxApi.password = ""
        attempts = 0
        TestUtils.executeSync(
                filename = "device.json",
                body = bboxApi::getVoipInfoSync,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
        Assert.assertEquals(1, attempts)
        TestUtils.executeSync(
                filename = "device.json",
                body = bboxApi::getVoipInfoSync,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
        Assert.assertEquals(2, attempts)
        val exception = BboxApiRouter.BboxAuthException(BboxException(
                ApiException(domain = "v1/login", code = "429", errors = listOf())
        ))
        Assert.assertFalse(bboxApi.blocked)
        Assert.assertEquals(2, bboxApi.attempts)
        TestUtils.executeSync(filename = "device.json", body = bboxApi::getVoipInfoSync, expectedException = exception)
        Assert.assertEquals(3, attempts)
        Assert.assertTrue(bboxApi.blocked)
        Assert.assertEquals(3, bboxApi.attempts)
        Assert.assertTrue(bboxApi.blockedUntil.after(Date()))
        Assert.assertFalse(bboxApi.authenticated)
    }

    @Test
    fun blockClientAsync() {
        Assume.assumeFalse(runOnNetwork())
        bboxApi.password = ""
        attempts = 0
        TestUtils.executeAsync(
                testcase = this,
                filename = "device.json",
                body = bboxApi::getVoipInfo,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
        Assert.assertEquals(1, attempts)
        lock = CountDownLatch(1)
        TestUtils.executeAsync(
                testcase = this,
                filename = "device.json",
                body = bboxApi::getVoipInfo,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
        Assert.assertEquals(2, attempts)
        val error = BboxException(
                ApiException(domain = "v1/login", code = "429", errors = listOf())
        )
        Assert.assertFalse(bboxApi.blocked)
        Assert.assertEquals(2, bboxApi.attempts)
        lock = CountDownLatch(1)
        TestUtils.executeAsync(testcase = this, filename = "device.json", body = bboxApi::getVoipInfo, expectedException = BboxApiRouter.BboxAuthException(error))
        Assert.assertEquals(3, attempts)
        Assert.assertTrue(bboxApi.blocked)
        Assert.assertEquals(3, bboxApi.attempts)
        Assert.assertTrue(bboxApi.blockedUntil.after(Date()))
        Assert.assertFalse(bboxApi.authenticated)
    }

    @Test
    fun authenticateSuccess() {
        bboxApi.password = password
        var request: Request? = null
        var response: Response? = null
        var exception: Exception? = null
        var bboxid: String? = null
        bboxApi.authenticate { authResult ->
            val (req, res, err, id) = authResult
            request = req
            response = res
            exception = err
            bboxid = id
            lock.countDown()
        }
        await()
        Assert.assertThat(request, CoreMatchers.notNullValue())
        Assert.assertThat(response, CoreMatchers.notNullValue())
        Assert.assertThat(exception, CoreMatchers.nullValue())
        Assert.assertThat(bboxid, CoreMatchers.notNullValue())
        Assert.assertEquals(bboxid, cookie)
    }

    @Test
    fun authenticateFailure() {
        bboxApi.password = ""
        var request: Request? = null
        var response: Response? = null
        var exception: Exception? = null
        var bboxid: String? = null
        bboxApi.authenticate { authResult ->
            val (req, res, err, id) = authResult
            request = req
            response = res
            exception = err
            bboxid = id
            lock.countDown()
        }
        await()
        Assert.assertThat(request, CoreMatchers.notNullValue())
        Assert.assertThat(response, CoreMatchers.notNullValue())
        Assert.assertThat(exception, CoreMatchers.notNullValue())
        Assert.assertTrue(exception is HttpException)
        val httpException = exception as HttpException
        Assert.assertEquals(HttpException(httpCode = 401, httpMessage = "Client Error").message, httpException.message)
        Assert.assertThat(bboxid, CoreMatchers.nullValue())
    }

    @Test
    fun authenticateSyncSuccess() {
        bboxApi.password = password
        val result = bboxApi.authenticateSync()
        val (req, res, err, id) = result
        Assert.assertThat(req, CoreMatchers.notNullValue())
        Assert.assertThat(res, CoreMatchers.notNullValue())
        Assert.assertThat(err, CoreMatchers.nullValue())
        Assert.assertThat(id, CoreMatchers.notNullValue())
        Assert.assertEquals(cookie, id)
    }

    @Test
    fun authenticateSyncFailure() {
        bboxApi.password = ""
        val result = bboxApi.authenticateSync()
        val (req, res, err, id) = result
        Assert.assertThat(req, CoreMatchers.notNullValue())
        Assert.assertThat(res, CoreMatchers.notNullValue())
        Assert.assertThat(err, CoreMatchers.notNullValue())
        Assert.assertTrue(err is HttpException)
        val httpException = err as HttpException
        Assert.assertEquals(HttpException(httpCode = 401, httpMessage = "Client Error").message, httpException.message)
        Assert.assertThat(id, CoreMatchers.nullValue())
    }

    @Test
    fun getVoipInfoAuthenticated() {
        bboxApi.password = password
        TestUtils.executeAsync(testcase = this, filename = "voip.json", body = bboxApi::getVoipInfo)
    }

    @Test
    fun getVoipInfoNotAuthenticated() {
        bboxApi.password = ""
        TestUtils.executeAsync(
                testcase = this,
                filename = "voip.json",
                body = bboxApi::getVoipInfo,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun getVoipInfoAlreadyAuthenticatedSuccess() {
        bboxApi.password = password
        bboxApi.authenticated = true
        TestUtils.executeAsync(testcase = this, filename = "voip.json", body = bboxApi::getVoipInfo)
    }

    @Test
    fun getVoipInfoAlreadyAuthenticatedFailure() {
        bboxApi.password = ""
        bboxApi.authenticated = true
        TestUtils.executeAsync(
                testcase = this,
                filename = "voip.json",
                body = bboxApi::getVoipInfo,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun getVoipInfoSyncAuthenticated() {
        bboxApi.password = password
        TestUtils.executeSync(filename = "voip.json", body = bboxApi::getVoipInfoSync)
    }

    @Test
    fun getVoipInfoSyncNotAuthenticated() {
        bboxApi.password = ""
        TestUtils.executeSync(
                filename = "voip.json",
                body = bboxApi::getVoipInfoSync,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun getVoipInfoSyncAlreadyAuthenticatedSuccess() {
        bboxApi.password = password
        bboxApi.authenticated = true
        TestUtils.executeSync(filename = "voip.json", body = bboxApi::getVoipInfoSync)
    }

    @Test
    fun getVoipInfoSyncAlreadyAuthenticatedFailure() {
        bboxApi.password = ""
        bboxApi.authenticated = true
        TestUtils.executeSync(
                filename = "voip.json",
                body = bboxApi::getVoipInfoSync,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }


    @Test
    fun getVoipInfoCbAuthenticated() {
        bboxApi.password = password
        TestUtils.executeAsyncCb(testcase = this, filename = "voip.json", body = bboxApi::getVoipInfo)
    }

    @Test
    fun getVoipInfoCbNotAuthenticated() {
        bboxApi.password = ""
        TestUtils.executeAsyncCb(
                testcase = this,
                filename = "voip.json",
                body = bboxApi::getVoipInfo,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun getVoipInfoCbAlreadyAuthenticatedSuccess() {
        bboxApi.password = password
        bboxApi.authenticated = true
        TestUtils.executeAsyncCb(testcase = this, filename = "voip.json", body = bboxApi::getVoipInfo)
    }

    @Test
    fun getVoipInfoCbAlreadyAuthenticatedFailure() {
        bboxApi.password = ""
        bboxApi.authenticated = true
        TestUtils.executeAsyncCb(
                testcase = this,
                filename = "voip.json",
                body = bboxApi::getVoipInfo,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun getWirelessInfo() {
        bboxApi.password = password
        TestUtils.executeAsync(testcase = this, filename = "wireless.json", body = bboxApi::getWirelessInfo)
    }

    @Test
    fun getWirelessInfoCb() {
        bboxApi.password = password
        TestUtils.executeAsyncCb(testcase = this, filename = "wireless.json", body = bboxApi::getWirelessInfo)
    }

    @Test
    fun getWirelessInfoSync() {
        bboxApi.password = password
        TestUtils.executeSync(filename = "wireless.json", body = bboxApi::getWirelessInfoSync)
    }

    @Test
    fun getCallLogs() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParam(testcase = this, input = Line.LINE1, filename = "calllog.json", body = bboxApi::getCallLogs)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncOneParam(testcase = this, input = Line.LINE2, filename = "calllog.json", body = bboxApi::getCallLogs)
    }

    @Test
    fun getCallLogsCb() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParamCb(testcase = this, input = Line.LINE1, filename = "calllog.json", body = bboxApi::getCallLogs)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncOneParamCb(testcase = this, input = Line.LINE2, filename = "calllog.json", body = bboxApi::getCallLogs)
    }

    @Test
    fun getCallLogsSync() {
        bboxApi.password = password
        TestUtils.executeSyncOneParam(filename = "calllog.json", input = Line.LINE1, body = bboxApi::getCallLogsSync)
        TestUtils.executeSyncOneParam(filename = "calllog.json", input = Line.LINE2, body = bboxApi::getCallLogsSync)
    }

    @Test
    fun noHostUnsecuredAsyncRequest() {
        bboxApi.setBasePath("http://testtesttest")
        TestUtils.executeAsync(testcase = this, filename = "summary.json", body = bboxApi::getSummary, expectedException = UnknownHostException())
    }

    @Test
    fun noHostUnsecuredSyncRequest() {
        bboxApi.setBasePath("http://testtesttest")
        TestUtils.executeSync(filename = "summary.json", body = bboxApi::getSummarySync, expectedException = UnknownHostException())
    }

    @Test
    fun noHostSecuredAsyncRequest() {
        bboxApi.setBasePath("http://testtesttest")
        TestUtils.executeAsync(testcase = this, filename = "voip.json", body = bboxApi::getVoipInfo, expectedException = UnknownHostException())
    }

    @Test
    fun noHostSecuredSyncRequest() {
        bboxApi.setBasePath("http://testtesttest")
        TestUtils.executeSync(filename = "voip.json", body = bboxApi::getVoipInfoSync, expectedException = UnknownHostException())
    }

    @Test
    fun setWifiState() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParam(testcase = this, input = false, filename = null, body = bboxApi::setWifiState)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncOneParam(testcase = this, input = true, filename = null, body = bboxApi::setWifiState)
    }

    @Test
    fun setWifiStateNotAuthenticated() {
        bboxApi.password = ""
        TestUtils.executeAsyncOneParam(
                testcase = this,
                input = false,
                filename = null,
                body = bboxApi::setWifiState,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setWifiStateBadToken() {
        bboxApi.password = ""
        bboxApi.authenticated = true
        TestUtils.executeAsyncOneParam(
                testcase = this,
                input = false,
                filename = null,
                body = bboxApi::setWifiState,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setWifiStateCb() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParamCb(testcase = this, input = false, filename = null, body = bboxApi::setWifiState)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncOneParamCb(testcase = this, input = true, filename = null, body = bboxApi::setWifiState)
    }

    @Test
    fun setWifiStateCbNotAuthenticated() {
        bboxApi.password = ""
        TestUtils.executeAsyncOneParamCb(
                testcase = this,
                input = false,
                filename = null,
                body = bboxApi::setWifiState,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setWifiStateCbBadToken() {
        bboxApi.password = ""
        bboxApi.authenticated = true
        TestUtils.executeAsyncOneParamCb(
                testcase = this,
                input = false,
                filename = null,
                body = bboxApi::setWifiState,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setWifiStateSync() {
        bboxApi.password = password
        TestUtils.executeSyncOneParam(input = false, filename = null, body = bboxApi::setWifiStateSync)
        TestUtils.executeSyncOneParam(input = true, filename = null, body = bboxApi::setWifiStateSync)
    }

    @Test
    fun setWifiStateSyncNotAuthenticated() {
        bboxApi.password = ""
        TestUtils.executeSyncOneParam(
                input = false,
                filename = null,
                body = bboxApi::setWifiStateSync,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setWifiStateSyncBadToken() {
        bboxApi.password = ""
        bboxApi.authenticated = true
        TestUtils.executeSyncOneParam(
                input = false,
                filename = null,
                body = bboxApi::setWifiStateSync,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setDisplayState() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParam(testcase = this, input = false, filename = null, body = bboxApi::setDisplayState)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncOneParam(testcase = this, input = true, filename = null, body = bboxApi::setDisplayState)
    }

    @Test
    fun setDisplayStateCb() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParamCb(testcase = this, input = false, filename = null, body = bboxApi::setDisplayState)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncOneParamCb(testcase = this, input = true, filename = null, body = bboxApi::setDisplayState)
    }

    @Test
    fun setDisplayStateSync() {
        bboxApi.password = password
        TestUtils.executeSyncOneParam(input = false, filename = null, body = bboxApi::setDisplayStateSync)
        TestUtils.executeSyncOneParam(input = true, filename = null, body = bboxApi::setDisplayStateSync)
    }

    @Test
    fun voipDial() {
        bboxApi.password = password
        TestUtils.executeAsyncTwoParam(testcase = this, input1 = Line.LINE1, input2 = "012345689", filename = null, body = bboxApi::voipDial)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncTwoParam(testcase = this, input1 = Line.LINE2, input2 = "012345689", filename = null, body = bboxApi::voipDial)
    }

    @Test
    fun voipDialCb() {
        bboxApi.password = password
        TestUtils.executeAsyncTwoParamCb(testcase = this, input1 = Line.LINE1, input2 = "012345689", filename = null, body = bboxApi::voipDial)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncTwoParamCb(testcase = this, input1 = Line.LINE2, input2 = "012345689", filename = null, body = bboxApi::voipDial)
    }

    @Test
    fun voipDialSync() {
        bboxApi.password = password
        TestUtils.executeSyncTwoParam(input1 = Line.LINE1, input2 = "012345689", filename = null, body = bboxApi::voipDialSync)
        TestUtils.executeSyncTwoParam(input1 = Line.LINE2, input2 = "012345689", filename = null, body = bboxApi::voipDialSync)
    }

    @Test
    fun getBboxToken() {
        bboxApi.password = password
        TestUtils.executeAsync(testcase = this, filename = "token.json", body = bboxApi::getToken)
    }

    @Test
    fun getBboxTokenCb() {
        bboxApi.password = password
        TestUtils.executeAsyncCb(testcase = this, filename = "token.json", body = bboxApi::getToken)
    }

    @Test
    fun getBboxTokenSync() {
        bboxApi.password = password
        TestUtils.executeSync(filename = "token.json", body = bboxApi::getTokenSync)
    }

    @Test
    fun reboot() {
        bboxApi.password = password
        TestUtils.executeAsync(testcase = this, filename = null, body = bboxApi::reboot)
    }

    @Test
    fun rebootCb() {
        bboxApi.password = password
        TestUtils.executeAsyncCb(testcase = this, filename = null, body = bboxApi::reboot)
    }

    @Test
    fun rebootSync() {
        bboxApi.password = password
        TestUtils.executeSync(filename = null, body = bboxApi::rebootSync)
    }

    @Test
    fun getWifiMacFilter() {
        bboxApi.password = password
        TestUtils.executeAsync(testcase = this, filename = "acl.json", body = bboxApi::getWifiMacFilter)
    }

    @Test
    fun getWifiMacFilterCb() {
        bboxApi.password = password
        TestUtils.executeAsyncCb(testcase = this, filename = "acl.json", body = bboxApi::getWifiMacFilter)
    }

    @Test
    fun getWifiMacFilterSync() {
        bboxApi.password = password
        TestUtils.executeSync(filename = "acl.json", body = bboxApi::getWifiMacFilterSync)
    }

    @Test
    fun setWifiMacFilter() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParam(testcase = this, input = false, filename = null, body = bboxApi::setWifiMacFilter)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncOneParam(testcase = this, input = true, filename = null, body = bboxApi::setWifiMacFilter)
    }

    @Test
    fun setWifiMacFilterCb() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParamCb(testcase = this, input = false, filename = null, body = bboxApi::setWifiMacFilter)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncOneParamCb(testcase = this, input = true, filename = null, body = bboxApi::setWifiMacFilter)
    }

    @Test
    fun setWifiMacFilterSync() {
        bboxApi.password = password
        TestUtils.executeSyncOneParam(input = false, filename = null, body = bboxApi::setWifiMacFilterSync)
        TestUtils.executeSyncOneParam(input = true, filename = null, body = bboxApi::setWifiMacFilterSync)
    }

    @Test
    fun deleteWifiMacFilter() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParam(testcase = this, input = 1, filename = null, body = bboxApi::deleteMacFilterRule)
    }

    @Test
    fun deleteWifiMacFilterInvalid() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParam(
                testcase = this,
                input = 2,
                filename = null,
                body = bboxApi::deleteMacFilterRule,
                expectedException = HttpException(404, "Client Error"))
    }

    @Test
    fun deleteWifiMacFilterCb() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParamCb(testcase = this, input = 1, filename = null, body = bboxApi::deleteMacFilterRule)
    }

    @Test
    fun deleteWifiMacFilterCbInvalid() {
        bboxApi.password = password
        TestUtils.executeAsyncOneParamCb(
                testcase = this,
                input = 2,
                filename = null,
                body = bboxApi::deleteMacFilterRule,
                expectedException = HttpException(404, "Client Error"))
    }

    @Test
    fun deleteWifiMacFilterSync() {
        bboxApi.password = password
        TestUtils.executeSyncOneParam(input = 1, filename = null, body = bboxApi::deleteMacFilterRuleSync)
    }

    @Test
    fun deleteWifiMacFilterSyncInvalid() {
        bboxApi.password = password
        TestUtils.executeSyncOneParam(
                input = 2,
                filename = null,
                body = bboxApi::deleteMacFilterRuleSync,
                expectedException = HttpException(404, "Client Error"))
    }

    @Test
    fun updateMacFilterRule() {
        bboxApi.password = password
        val rule = MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeAsyncTwoParam(
                testcase = this,
                input1 = 1,
                input2 = rule,
                filename = null,
                body = bboxApi::updateMacFilterRule
        )
        Assert.assertNotNull(macFilterRule)
        Assert.assertEquals(rule.enable, macFilterRule?.enable)
        Assert.assertEquals(rule.macaddress, macFilterRule?.macaddress)
        Assert.assertEquals(rule.ip, macFilterRule?.ip)
    }

    @Test
    fun updateMacFilterRuleCb() {
        bboxApi.password = password
        val rule = MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeAsyncTwoParamCb(
                testcase = this,
                input1 = 1,
                input2 = rule,
                filename = null,
                body = bboxApi::updateMacFilterRule
        )
        Assert.assertNotNull(macFilterRule)
        Assert.assertEquals(rule.enable, macFilterRule?.enable)
        Assert.assertEquals(rule.macaddress, macFilterRule?.macaddress)
        Assert.assertEquals(rule.ip, macFilterRule?.ip)
    }

    @Test
    fun updateMacFilterRuleSync() {
        bboxApi.password = password
        val rule = MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeSyncTwoParam(
                filename = null,
                input1 = 1,
                input2 = rule,
                body = bboxApi::updateMacFilterRuleSync
        )
        Assert.assertNotNull(macFilterRule)
        Assert.assertEquals(rule.enable, macFilterRule?.enable)
        Assert.assertEquals(rule.macaddress, macFilterRule?.macaddress)
        Assert.assertEquals(rule.ip, macFilterRule?.ip)
    }

    @Test
    fun createWifiMacRule() {
        bboxApi.password = password
        val rule = MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeAsyncOneParam(
                testcase = this,
                input = rule,
                filename = null,
                body = bboxApi::createMacFilterRule
        )
        Assert.assertNotNull(macFilterRule)
        Assert.assertEquals(rule.enable, macFilterRule?.enable)
        Assert.assertEquals(rule.macaddress, macFilterRule?.macaddress)
        Assert.assertEquals(rule.ip, macFilterRule?.ip)
    }

    @Test
    fun createWifiMacRuleCb() {
        bboxApi.password = password
        val rule = MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeAsyncOneParamCb(
                testcase = this,
                input = rule,
                filename = null,
                body = bboxApi::createMacFilterRule
        )
        Assert.assertNotNull(macFilterRule)
        Assert.assertEquals(rule.enable, macFilterRule?.enable)
        Assert.assertEquals(rule.macaddress, macFilterRule?.macaddress)
        Assert.assertEquals(rule.ip, macFilterRule?.ip)
    }

    @Test
    fun createWifiMacRuleSync() {
        bboxApi.password = password
        val rule = MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeSyncOneParam(
                input = rule,
                filename = null,
                body = bboxApi::createMacFilterRuleSync
        )
        Assert.assertNotNull(macFilterRule)
        Assert.assertEquals(rule.enable, macFilterRule?.enable)
        Assert.assertEquals(rule.macaddress, macFilterRule?.macaddress)
        Assert.assertEquals(rule.ip, macFilterRule?.ip)
    }

    @Test
    fun createCustomRequest() {
        TestUtils.checkCustomResponse<List<Summary>>(
                testcase = this,
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/summary"),
                auth = false,
                expectedException = null,
                filename = "summary.json",
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestSecured() {
        bboxApi.password = password
        TestUtils.checkCustomResponse<List<Voip>>(
                testcase = this,
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/voip"),
                auth = true,
                expectedException = null,
                filename = "voip.json",
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestSecuredUnauthorized() {
        bboxApi.password = password
        TestUtils.checkCustomResponse<List<Voip>>(
                testcase = this,
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/voip"),
                auth = false,
                expectedException = HttpException(401, "Client Error"),
                filename = null,
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestCb() {
        TestUtils.checkCustomResponseCb<List<Summary>>(
                testcase = this,
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/summary"),
                auth = false,
                expectedException = null,
                filename = "summary.json",
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestCbSecured() {
        bboxApi.password = password
        TestUtils.checkCustomResponseCb<List<Voip>>(
                testcase = this,
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/voip"),
                auth = true,
                expectedException = null,
                filename = "voip.json",
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestCbSecuredUnauthorized() {
        bboxApi.password = password
        TestUtils.checkCustomResponseCb<List<Voip>>(
                testcase = this,
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/voip"),
                auth = false,
                expectedException = HttpException(401, "Client Error"),
                filename = null,
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestSync() {
        TestUtils.checkCustomResponseSync<List<Summary>>(
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/summary"),
                auth = false,
                expectedException = null,
                filename = "summary.json",
                body = bboxApi::createCustomRequestSync
        )
    }

    @Test
    fun createCustomRequestSecuredSync() {
        bboxApi.password = password
        TestUtils.checkCustomResponseSync<List<Voip>>(
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/voip"),
                auth = true,
                expectedException = null,
                filename = "voip.json",
                body = bboxApi::createCustomRequestSync
        )
    }

    @Test
    fun createCustomRequestSecuredUnauthorizedSync() {
        bboxApi.password = password
        TestUtils.checkCustomResponseSync<List<Voip>>(
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/voip"),
                auth = false,
                expectedException = HttpException(401, "Client Error"),
                filename = null,
                body = bboxApi::createCustomRequestSync
        )
    }

    @Test
    fun createCustomRequestNotAuthenticatedWrongHost() {
        bboxApi.setBasePath("https://google.fr")
        TestUtils.checkCustomResponseSync<List<Summary>>(
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/summary"),
                auth = false,
                expectedException = HttpException(401, "Client Error"),
                filename = "summary.json",
                body = bboxApi::createCustomRequestSync
        )
    }

    @Test
    fun createCustomRequestAuthenticatedWrongHost() {
        bboxApi.setBasePath("https://google.fr")
        TestUtils.checkCustomResponseSync<List<Summary>>(
                inputReq = bboxApi.manager.request(method = Method.GET, path = "/summary"),
                auth = true,
                expectedException = JsonSyntaxException("Error"),
                filename = "summary.json",
                body = bboxApi::createCustomRequestSync
        )
    }

    @Test
    fun getDeviceInfoVersion() {
        val (_, _, result) = bboxApi.getDeviceInfoSync()
        Assert.assertEquals(12, result.component1()?.get(0)?.device?.main?.getMajor())
        Assert.assertEquals(11, result.component1()?.get(0)?.device?.main?.getMinor())
        Assert.assertEquals(0, result.component1()?.get(0)?.device?.main?.getPatch())
    }

    @Test
    fun logout() {
        TestUtils.executeAsync(testcase = this, filename = null, body = bboxApi::logout)
    }

    @Test
    fun logoutSync() {
        TestUtils.executeSync(filename = null, body = bboxApi::logoutSync)
    }

    @Test
    fun logoutCb() {
        TestUtils.executeAsyncCb(testcase = this, filename = null, body = bboxApi::logout)
    }


    @Test
    fun startPasswordRecovery() {
        TestUtils.executeAsync(testcase = this, filename = null, body = bboxApi::startPasswordRecovery)
    }

    @Test
    fun startPasswordRecoverySync() {
        TestUtils.executeSync(filename = null, body = bboxApi::startPasswordRecoverySync)
    }

    @Test
    fun startPasswordRecoveryCb() {
        TestUtils.executeAsyncCb(testcase = this, filename = null, body = bboxApi::startPasswordRecovery)
    }

    @Test
    fun verifyPasswordRecovery() {
        TestUtils.executeAsync(testcase = this, filename = "verify_password.json", body = bboxApi::verifyPasswordRecovery)
    }

    @Test
    fun verifyPasswordRecoverySync() {
        TestUtils.executeSync(filename = "verify_password.json", body = bboxApi::verifyPasswordRecoverySync)
    }

    @Test
    fun verifyPasswordRecoveryCb() {
        TestUtils.executeAsyncCb(testcase = this, filename = "verify_password.json", body = bboxApi::verifyPasswordRecovery)
    }

    @Test
    fun resetPassword() {
        //no button pressed
        TestUtils.executeAsync(testcase = this, filename = "verify_password.json", body = bboxApi::verifyPasswordRecovery)
        changePassword = 1
        //button has been pressed
        lock = CountDownLatch(1)
        TestUtils.executeAsync(testcase = this, filename = null, body = bboxApi::verifyPasswordRecovery)
        //lets reset password now we have the token
        lock = CountDownLatch(1)
        TestUtils.executeAsyncOneParam(input = "123456", testcase = this, filename = null, body = bboxApi::resetPassword)
        Assert.assertEquals("123456", bboxApi.password)
    }

    @Test
    fun resetPasswordCb() {
        //no button pressed
        TestUtils.executeAsyncCb(testcase = this, filename = "verify_password.json", body = bboxApi::verifyPasswordRecovery)
        changePassword = 1
        //button has been pressed
        lock = CountDownLatch(1)
        TestUtils.executeAsyncCb(testcase = this, filename = null, body = bboxApi::verifyPasswordRecovery)
        //lets reset password now we have the token
        lock = CountDownLatch(1)
        TestUtils.executeAsyncOneParamCb(input = "123456", testcase = this, filename = null, body = bboxApi::resetPassword)
        Assert.assertEquals("123456", bboxApi.password)
    }

    @Test
    fun resetPasswordSync() {
        //no button pressed
        TestUtils.executeSync(filename = "verify_password.json", body = bboxApi::verifyPasswordRecoverySync)
        changePassword = 1
        //button has been pressed
        TestUtils.executeSync(filename = null, body = bboxApi::verifyPasswordRecoverySync)
        //lets reset password now we have the token
        TestUtils.executeSyncOneParam(input = "123456", filename = null, body = bboxApi::resetPasswordSync)
        Assert.assertEquals("123456", bboxApi.password)
    }

    @Test
    fun waitForPushButton() {
        var state = bboxApi.waitForPushButton(maxDuration = 2000, pollInterval = 250)
        Assert.assertFalse(state)
        changePassword = 1
        state = bboxApi.waitForPushButton(maxDuration = 2000, pollInterval = 250)
        Assert.assertTrue(state)
        changePassword = 0
    }

    @Test
    fun waitForPushButtonSimuButton() {
        val timer = Timer()
        timer.schedule(delay = 1000) {
            changePassword = 1
        }
        changePassword = 2
        val state = bboxApi.waitForPushButton(maxDuration = 2000, pollInterval = 250)
        Assert.assertTrue(state)
        timer.cancel()
    }

    @Test
    fun authorize() {
        TestUtils.executeAsyncTwoParam(input1 = GrantType.BUTTON, input2 = ResponseType.CODE, testcase = this, filename = "code.json", body = bboxApi::authorize)
    }

    @Test
    fun authorizeSync() {
        TestUtils.executeSyncTwoParam(input1 = GrantType.BUTTON, input2 = ResponseType.CODE, filename = "code.json", body = bboxApi::authorizeSync)
    }

    @Test
    fun authorizeCb() {
        TestUtils.executeAsyncTwoParamCb(input1 = GrantType.BUTTON, input2 = ResponseType.CODE, testcase = this, filename = "code.json", body = bboxApi::authorize)
    }

    @Test
    fun getTokenButton() {
        TestUtils.executeAsyncFourParam(
                input1 = GrantType.BUTTON,
                input2 = code,
                input3 = listOf(Scope.ALL),
                input4 = null,
                testcase = this,
                filename = "oauth_token.json",
                body = bboxApi::getToken)
    }

    @Test
    fun getTokenButtonSync() {
        TestUtils.executeSyncFourParam(
                input1 = GrantType.BUTTON,
                input2 = code,
                input3 = listOf(Scope.ALL),
                input4 = null,
                filename = "oauth_token.json",
                body = bboxApi::getTokenSync)
    }


    @Test
    fun getTokenButtonCb() {
        TestUtils.executeAsyncFourParamCb(
                input1 = GrantType.BUTTON,
                input2 = code,
                input3 = listOf(Scope.ALL),
                input4 = null,
                testcase = this,
                filename = "oauth_token.json",
                body = bboxApi::getToken)
    }

    @Test
    fun getTokenPassword() {
        TestUtils.executeAsyncFourParam(
                input1 = GrantType.PASSWORD,
                input2 = code,
                input3 = listOf(Scope.ALL),
                input4 = password,
                testcase = this,
                filename = "oauth_token.json",
                body = bboxApi::getToken)
    }

    @Test
    fun getTokenPasswordSync() {
        TestUtils.executeSyncFourParam(
                input1 = GrantType.PASSWORD,
                input2 = code,
                input3 = listOf(Scope.ALL),
                input4 = password,
                filename = "oauth_token.json",
                body = bboxApi::getTokenSync)
    }


    @Test
    fun getTokenPasswordCb() {
        TestUtils.executeAsyncFourParamCb(
                input1 = GrantType.PASSWORD,
                input2 = code,
                input3 = listOf(Scope.ALL),
                input4 = password,
                testcase = this,
                filename = "oauth_token.json",
                body = bboxApi::getToken)
    }

    @Test
    fun waitForPushButtonOauth() {
        var triple = bboxApi.authenticateOauthButton(maxDuration = 2000, pollInterval = 250)
        Assert.assertNotNull(triple.third.component2())
        Assert.assertEquals("push button failure", triple.third.component2()?.exception?.message)
        changePassword = 1
        triple = bboxApi.authenticateOauthButton(maxDuration = 2000, pollInterval = 250)
        Assert.assertNull(triple.third.component2())
        Assert.assertEquals(200, triple.second.statusCode)
        JSONAssert.assertEquals(TestUtils.getResFile(fileName = "oauth_token.json"), Gson().toJson(triple.third.get()), false)
        changePassword = 0
    }

    @Test
    fun waitForPushButtonOauthSimuButton() {
        val timer = Timer()
        timer.schedule(delay = 1000) {
            changePassword = 1
        }
        changePassword = 2
        val triple = bboxApi.authenticateOauthButton(maxDuration = 2000, pollInterval = 250)
        Assert.assertNull(triple.third.component2())
        Assert.assertEquals(200, triple.second.statusCode)
        JSONAssert.assertEquals(TestUtils.getResFile(fileName = "oauth_token.json"), Gson().toJson(triple.third.get()), false)
        timer.cancel()
    }

    @Test
    fun refreshToken() {
        TestUtils.executeAsyncTwoParam(
                input1 = refreshToken,
                input2 = listOf(Scope.ALL),
                testcase = this,
                filename = "oauth_token_without_rt.json",
                body = bboxApi::refreshToken)
    }

    @Test
    fun refreshTokenCb() {
        TestUtils.executeAsyncTwoParamCb(
                input1 = refreshToken,
                input2 = listOf(Scope.ALL),
                testcase = this,
                filename = "oauth_token_without_rt.json",
                body = bboxApi::refreshToken)
    }

    @Test
    fun refreshTokenSync() {
        TestUtils.executeSyncTwoParam(
                input1 = refreshToken,
                input2 = listOf(Scope.ALL),
                filename = "oauth_token_without_rt.json",
                body = bboxApi::refreshTokenSync)
    }
}