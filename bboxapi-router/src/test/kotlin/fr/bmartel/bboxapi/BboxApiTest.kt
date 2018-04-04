package fr.bmartel.bboxapi

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import fr.bmartel.bboxapi.model.*
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.junit.*
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.CountDownLatch

open class BboxApiTest : TestCase() {

    companion object {

        private val mockServer = MockWebServer()
        private val bboxApi = BboxApi()

        private val password = "admin@box"

        val cookie = "2.215.7fffffff.a60bd4fcc583ae1b745b5947d1f04da491b01cefcdf39dd9d2676e1a499ecff0a9a3ec6f269a8a9af5e3e1c7d32f6b65df8754ac7af2854c59f367a29136923b.d2614b3af80b5eb35cd890de5760893330372ab2"
        var attempts = 0
        var macFilterRule: Acl.MacFilterRule? = null

        //https://stackoverflow.com/a/33381385/2614364
        inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

        val btoken = Gson().fromJson<List<Token.Model>>(TestUtils.getResFile(fileName = "token.json"))

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
        bboxApi.setPassword("")
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
        val exception = BboxApi.BboxAuthException(BboxException.Model(
                BboxException.ApiException(domain = "v1/login", code = "429", errors = listOf())
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
        bboxApi.setPassword("")
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
        val error = BboxException.Model(
                BboxException.ApiException(domain = "v1/login", code = "429", errors = listOf())
        )
        Assert.assertFalse(bboxApi.blocked)
        Assert.assertEquals(2, bboxApi.attempts)
        lock = CountDownLatch(1)
        TestUtils.executeAsync(testcase = this, filename = "device.json", body = bboxApi::getVoipInfo, expectedException = BboxApi.BboxAuthException(error))
        Assert.assertEquals(3, attempts)
        Assert.assertTrue(bboxApi.blocked)
        Assert.assertEquals(3, bboxApi.attempts)
        Assert.assertTrue(bboxApi.blockedUntil.after(Date()))
        Assert.assertFalse(bboxApi.authenticated)
    }

    @Test
    fun authenticateSuccess() {
        bboxApi.setPassword(password)
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
        bboxApi.setPassword("")
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
        bboxApi.setPassword(password)
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
        bboxApi.setPassword("")
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
        bboxApi.setPassword(password)
        TestUtils.executeAsync(testcase = this, filename = "voip.json", body = bboxApi::getVoipInfo)
    }

    @Test
    fun getVoipInfoNotAuthenticated() {
        bboxApi.setPassword("")
        TestUtils.executeAsync(
                testcase = this,
                filename = "voip.json",
                body = bboxApi::getVoipInfo,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun getVoipInfoAlreadyAuthenticatedSuccess() {
        bboxApi.setPassword(password)
        bboxApi.authenticated = true
        TestUtils.executeAsync(testcase = this, filename = "voip.json", body = bboxApi::getVoipInfo)
    }

    @Test
    fun getVoipInfoAlreadyAuthenticatedFailure() {
        bboxApi.setPassword("")
        bboxApi.authenticated = true
        TestUtils.executeAsync(
                testcase = this,
                filename = "voip.json",
                body = bboxApi::getVoipInfo,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun getVoipInfoSyncAuthenticated() {
        bboxApi.setPassword(password)
        TestUtils.executeSync(filename = "voip.json", body = bboxApi::getVoipInfoSync)
    }

    @Test
    fun getVoipInfoSyncNotAuthenticated() {
        bboxApi.setPassword("")
        TestUtils.executeSync(
                filename = "voip.json",
                body = bboxApi::getVoipInfoSync,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun getVoipInfoSyncAlreadyAuthenticatedSuccess() {
        bboxApi.setPassword(password)
        bboxApi.authenticated = true
        TestUtils.executeSync(filename = "voip.json", body = bboxApi::getVoipInfoSync)
    }

    @Test
    fun getVoipInfoSyncAlreadyAuthenticatedFailure() {
        bboxApi.setPassword("")
        bboxApi.authenticated = true
        TestUtils.executeSync(
                filename = "voip.json",
                body = bboxApi::getVoipInfoSync,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }


    @Test
    fun getVoipInfoCbAuthenticated() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncCb(testcase = this, filename = "voip.json", body = bboxApi::getVoipInfo)
    }

    @Test
    fun getVoipInfoCbNotAuthenticated() {
        bboxApi.setPassword("")
        TestUtils.executeAsyncCb(
                testcase = this,
                filename = "voip.json",
                body = bboxApi::getVoipInfo,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun getVoipInfoCbAlreadyAuthenticatedSuccess() {
        bboxApi.setPassword(password)
        bboxApi.authenticated = true
        TestUtils.executeAsyncCb(testcase = this, filename = "voip.json", body = bboxApi::getVoipInfo)
    }

    @Test
    fun getVoipInfoCbAlreadyAuthenticatedFailure() {
        bboxApi.setPassword("")
        bboxApi.authenticated = true
        TestUtils.executeAsyncCb(
                testcase = this,
                filename = "voip.json",
                body = bboxApi::getVoipInfo,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun getWirelessInfo() {
        bboxApi.setPassword(password)
        TestUtils.executeAsync(testcase = this, filename = "wireless.json", body = bboxApi::getWirelessInfo)
    }

    @Test
    fun getWirelessInfoCb() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncCb(testcase = this, filename = "wireless.json", body = bboxApi::getWirelessInfo)
    }

    @Test
    fun getWirelessInfoSync() {
        bboxApi.setPassword(password)
        TestUtils.executeSync(filename = "wireless.json", body = bboxApi::getWirelessInfoSync)
    }

    @Test
    fun getCallLogs() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncLine(testcase = this, line = Voip.Line.LINE1, filename = "calllog.json", body = bboxApi::getCallLogs)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncLine(testcase = this, line = Voip.Line.LINE2, filename = "calllog.json", body = bboxApi::getCallLogs)
    }

    @Test
    fun getCallLogsCb() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncLineCb(testcase = this, line = Voip.Line.LINE1, filename = "calllog.json", body = bboxApi::getCallLogs)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncLineCb(testcase = this, line = Voip.Line.LINE2, filename = "calllog.json", body = bboxApi::getCallLogs)
    }

    @Test
    fun getCallLogsSync() {
        bboxApi.setPassword(password)
        TestUtils.executeSyncLine(filename = "calllog.json", line = Voip.Line.LINE1, body = bboxApi::getCallLogsSync)
        TestUtils.executeSyncLine(filename = "calllog.json", line = Voip.Line.LINE2, body = bboxApi::getCallLogsSync)
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
        bboxApi.setPassword(password)
        TestUtils.executeAsyncBool(testcase = this, input = false, filename = null, body = bboxApi::setWifiState)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncBool(testcase = this, input = true, filename = null, body = bboxApi::setWifiState)
    }

    @Test
    fun setWifiStateNotAuthenticated() {
        bboxApi.setPassword("")
        TestUtils.executeAsyncBool(
                testcase = this,
                input = false,
                filename = null,
                body = bboxApi::setWifiState,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setWifiStateBadToken() {
        bboxApi.setPassword("")
        bboxApi.authenticated = true
        TestUtils.executeAsyncBool(
                testcase = this,
                input = false,
                filename = null,
                body = bboxApi::setWifiState,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setWifiStateCb() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncBoolCb(testcase = this, input = false, filename = null, body = bboxApi::setWifiState)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncBoolCb(testcase = this, input = true, filename = null, body = bboxApi::setWifiState)
    }

    @Test
    fun setWifiStateCbNotAuthenticated() {
        bboxApi.setPassword("")
        TestUtils.executeAsyncBoolCb(
                testcase = this,
                input = false,
                filename = null,
                body = bboxApi::setWifiState,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setWifiStateCbBadToken() {
        bboxApi.setPassword("")
        bboxApi.authenticated = true
        TestUtils.executeAsyncBoolCb(
                testcase = this,
                input = false,
                filename = null,
                body = bboxApi::setWifiState,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setWifiStateSync() {
        bboxApi.setPassword(password)
        TestUtils.executeSyncBool(input = false, filename = null, body = bboxApi::setWifiStateSync)
        TestUtils.executeSyncBool(input = true, filename = null, body = bboxApi::setWifiStateSync)
    }

    @Test
    fun setWifiStateSyncNotAuthenticated() {
        bboxApi.setPassword("")
        TestUtils.executeSyncBool(
                input = false,
                filename = null,
                body = bboxApi::setWifiStateSync,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setWifiStateSyncBadToken() {
        bboxApi.setPassword("")
        bboxApi.authenticated = true
        TestUtils.executeSyncBool(
                input = false,
                filename = null,
                body = bboxApi::setWifiStateSync,
                expectedException = HttpException(httpCode = 401, httpMessage = "Client Error"))
    }

    @Test
    fun setDisplayState() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncBool(testcase = this, input = false, filename = null, body = bboxApi::setDisplayState)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncBool(testcase = this, input = true, filename = null, body = bboxApi::setDisplayState)
    }

    @Test
    fun setDisplayStateCb() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncBoolCb(testcase = this, input = false, filename = null, body = bboxApi::setDisplayState)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncBoolCb(testcase = this, input = true, filename = null, body = bboxApi::setDisplayState)
    }

    @Test
    fun setDisplayStateSync() {
        bboxApi.setPassword(password)
        TestUtils.executeSyncBool(input = false, filename = null, body = bboxApi::setDisplayStateSync)
        TestUtils.executeSyncBool(input = true, filename = null, body = bboxApi::setDisplayStateSync)
    }

    @Test
    fun voipDial() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncLineString(testcase = this, line = Voip.Line.LINE1, input2 = "012345689", filename = null, body = bboxApi::voipDial)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncLineString(testcase = this, line = Voip.Line.LINE2, input2 = "012345689", filename = null, body = bboxApi::voipDial)
    }

    @Test
    fun voipDialCb() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncLineStringCb(testcase = this, line = Voip.Line.LINE1, input2 = "012345689", filename = null, body = bboxApi::voipDial)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncLineStringCb(testcase = this, line = Voip.Line.LINE2, input2 = "012345689", filename = null, body = bboxApi::voipDial)
    }

    @Test
    fun voipDialSync() {
        bboxApi.setPassword(password)
        TestUtils.executeSyncLineString(line = Voip.Line.LINE1, input2 = "012345689", filename = null, body = bboxApi::voipDialSync)
        TestUtils.executeSyncLineString(line = Voip.Line.LINE2, input2 = "012345689", filename = null, body = bboxApi::voipDialSync)
    }

    @Test
    fun getBboxToken() {
        bboxApi.setPassword(password)
        TestUtils.executeAsync(testcase = this, filename = "token.json", body = bboxApi::getToken)
    }

    @Test
    fun getBboxTokenCb() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncCb(testcase = this, filename = "token.json", body = bboxApi::getToken)
    }

    @Test
    fun getBboxTokenSync() {
        bboxApi.setPassword(password)
        TestUtils.executeSync(filename = "token.json", body = bboxApi::getTokenSync)
    }

    @Test
    fun reboot() {
        bboxApi.setPassword(password)
        TestUtils.executeAsync(testcase = this, filename = null, body = bboxApi::reboot)
    }

    @Test
    fun rebootCb() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncCb(testcase = this, filename = null, body = bboxApi::reboot)
    }

    @Test
    fun rebootSync() {
        bboxApi.setPassword(password)
        TestUtils.executeSync(filename = null, body = bboxApi::rebootSync)
    }

    @Test
    fun getWifiMacFilter() {
        bboxApi.setPassword(password)
        TestUtils.executeAsync(testcase = this, filename = "acl.json", body = bboxApi::getWifiMacFilter)
    }

    @Test
    fun getWifiMacFilterCb() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncCb(testcase = this, filename = "acl.json", body = bboxApi::getWifiMacFilter)
    }

    @Test
    fun getWifiMacFilterSync() {
        bboxApi.setPassword(password)
        TestUtils.executeSync(filename = "acl.json", body = bboxApi::getWifiMacFilterSync)
    }

    @Test
    fun setWifiMacFilter() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncBool(testcase = this, input = false, filename = null, body = bboxApi::setWifiMacFilter)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncBool(testcase = this, input = true, filename = null, body = bboxApi::setWifiMacFilter)
    }

    @Test
    fun setWifiMacFilterCb() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncBoolCb(testcase = this, input = false, filename = null, body = bboxApi::setWifiMacFilter)
        lock = CountDownLatch(1)
        TestUtils.executeAsyncBoolCb(testcase = this, input = true, filename = null, body = bboxApi::setWifiMacFilter)
    }

    @Test
    fun setWifiMacFilterSync() {
        bboxApi.setPassword(password)
        TestUtils.executeSyncBool(input = false, filename = null, body = bboxApi::setWifiMacFilterSync)
        TestUtils.executeSyncBool(input = true, filename = null, body = bboxApi::setWifiMacFilterSync)
    }

    @Test
    fun deleteWifiMacFilter() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncInt(testcase = this, input = 1, filename = null, body = bboxApi::deleteMacFilterRule)
    }

    @Test
    fun deleteWifiMacFilterInvalid() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncInt(
                testcase = this,
                input = 2,
                filename = null,
                body = bboxApi::deleteMacFilterRule,
                expectedException = HttpException(404, "Client Error"))
    }

    @Test
    fun deleteWifiMacFilterCb() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncIntCb(testcase = this, input = 1, filename = null, body = bboxApi::deleteMacFilterRule)
    }

    @Test
    fun deleteWifiMacFilterCbInvalid() {
        bboxApi.setPassword(password)
        TestUtils.executeAsyncIntCb(
                testcase = this,
                input = 2,
                filename = null,
                body = bboxApi::deleteMacFilterRule,
                expectedException = HttpException(404, "Client Error"))
    }

    @Test
    fun deleteWifiMacFilterSync() {
        bboxApi.setPassword(password)
        TestUtils.executeSyncInt(input = 1, filename = null, body = bboxApi::deleteMacFilterRuleSync)
    }

    @Test
    fun deleteWifiMacFilterSyncInvalid() {
        bboxApi.setPassword(password)
        TestUtils.executeSyncInt(
                input = 2,
                filename = null,
                body = bboxApi::deleteMacFilterRuleSync,
                expectedException = HttpException(404, "Client Error"))
    }

    @Test
    fun updateMacFilterRule() {
        bboxApi.setPassword(password)
        val rule = Acl.MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeAsyncRuleInt(
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
        bboxApi.setPassword(password)
        val rule = Acl.MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeAsyncRuleIntCb(
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
        bboxApi.setPassword(password)
        val rule = Acl.MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeSyncRuleInt(
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
        bboxApi.setPassword(password)
        val rule = Acl.MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeAsyncRule(
                testcase = this,
                input1 = rule,
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
        bboxApi.setPassword(password)
        val rule = Acl.MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeAsyncRuleCb(
                testcase = this,
                input1 = rule,
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
        bboxApi.setPassword(password)
        val rule = Acl.MacFilterRule(enable = true, macaddress = "01:23:45:67:89", ip = "192.168.2.4")
        TestUtils.executeSyncRule(
                input1 = rule,
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
        TestUtils.checkCustomResponse<List<Summary.Model>>(
                testcase = this,
                inputReq = Fuel.get("/summary"),
                auth = false,
                expectedException = null,
                filename = "summary.json",
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestSecured() {
        bboxApi.setPassword(password)
        TestUtils.checkCustomResponse<List<Voip.Model>>(
                testcase = this,
                inputReq = Fuel.get("/voip"),
                auth = true,
                expectedException = null,
                filename = "voip.json",
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestSecuredUnauthorized() {
        bboxApi.setPassword(password)
        TestUtils.checkCustomResponse<List<Voip.Model>>(
                testcase = this,
                inputReq = Fuel.get("/voip"),
                auth = false,
                expectedException = HttpException(401, "Client Error"),
                filename = null,
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestCb() {
        TestUtils.checkCustomResponseCb<List<Summary.Model>>(
                testcase = this,
                inputReq = Fuel.get("/summary"),
                auth = false,
                expectedException = null,
                filename = "summary.json",
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestCbSecured() {
        bboxApi.setPassword(password)
        TestUtils.checkCustomResponseCb<List<Voip.Model>>(
                testcase = this,
                inputReq = Fuel.get("/voip"),
                auth = true,
                expectedException = null,
                filename = "voip.json",
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestCbSecuredUnauthorized() {
        bboxApi.setPassword(password)
        TestUtils.checkCustomResponseCb<List<Voip.Model>>(
                testcase = this,
                inputReq = Fuel.get("/voip"),
                auth = false,
                expectedException = HttpException(401, "Client Error"),
                filename = null,
                body = bboxApi::createCustomRequest
        )
    }

    @Test
    fun createCustomRequestSync() {
        TestUtils.checkCustomResponseSync<List<Summary.Model>>(
                inputReq = Fuel.get("/summary"),
                auth = false,
                expectedException = null,
                filename = "summary.json",
                body = bboxApi::createCustomRequestSync
        )
    }

    @Test
    fun createCustomRequestSecuredSync() {
        bboxApi.setPassword(password)
        TestUtils.checkCustomResponseSync<List<Voip.Model>>(
                inputReq = Fuel.get("/voip"),
                auth = true,
                expectedException = null,
                filename = "voip.json",
                body = bboxApi::createCustomRequestSync
        )
    }

    @Test
    fun createCustomRequestSecuredUnauthorizedSync() {
        bboxApi.setPassword(password)
        TestUtils.checkCustomResponseSync<List<Voip.Model>>(
                inputReq = Fuel.get("/voip"),
                auth = false,
                expectedException = HttpException(401, "Client Error"),
                filename = null,
                body = bboxApi::createCustomRequestSync
        )
    }

    @Test
    fun createCustomRequestNotAuthenticatedWrongHost() {
        bboxApi.setBasePath("https://google.fr")
        TestUtils.checkCustomResponseSync<List<Summary.Model>>(
                inputReq = Fuel.get("/summary"),
                auth = false,
                expectedException = HttpException(401, "Client Error"),
                filename = "summary.json",
                body = bboxApi::createCustomRequestSync
        )
    }

    @Test
    fun createCustomRequestAuthenticatedWrongHost() {
        bboxApi.setBasePath("https://google.fr")
        TestUtils.checkCustomResponseSync<List<Summary.Model>>(
                inputReq = Fuel.get("/summary"),
                auth = true,
                expectedException = JsonSyntaxException("Error"),
                filename = "summary.json",
                body = bboxApi::createCustomRequestSync
        )
    }
}