/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2017-2018 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.bboxapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.bmartel.bboxapi.model.HttpConnection;
import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.model.device.BboxDeviceEntry;
import fr.bmartel.bboxapi.model.host.HostItem;
import fr.bmartel.bboxapi.model.profile.ProfileEntry;
import fr.bmartel.bboxapi.model.profile.RefreshAction;
import fr.bmartel.bboxapi.model.recovery.VerifyRecovery;
import fr.bmartel.bboxapi.model.summary.ApiSummary;
import fr.bmartel.bboxapi.model.token.BboxDevice;
import fr.bmartel.bboxapi.model.voip.CallLogList;
import fr.bmartel.bboxapi.model.voip.VoipEntry;
import fr.bmartel.bboxapi.model.voip.voicemail.VoiceMailEntry;
import fr.bmartel.bboxapi.model.voip.voicemail.VoiceMailItem;
import fr.bmartel.bboxapi.model.wan.WanIp;
import fr.bmartel.bboxapi.model.wan.WanItem;
import fr.bmartel.bboxapi.model.wireless.AclItem;
import fr.bmartel.bboxapi.model.wireless.WirelessItem;
import fr.bmartel.bboxapi.response.*;
import fr.bmartel.bboxapi.utils.HttpUtils;
import fr.bmartel.bboxapi.utils.NetworkUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bbox Router Api client
 *
 * @author Bertrand Martel
 */
public class BboxApi {

    private String mTokenHeader = "";

    private boolean mAuthenticated = false;

    private String mPassword;

    private final static String BBOX_HOST = "bbox.lan";
    private final static String URL_PREFIX = "http://" + BBOX_HOST;
    private final static String LOGIN_URI = URL_PREFIX + "/api/v1/login";
    private final static String VOIP_URI = URL_PREFIX + "/api/v1/voip";
    private final static String DIAL_URI = URL_PREFIX + "/api/v1/voip/dial";
    private final static String DISPLAY_STATE_URI = URL_PREFIX + "/api/v1/device/display";
    private final static String WIRELESS_URI = URL_PREFIX + "/api/v1/wireless";
    private final static String WIRELESS_ACL_URI = URL_PREFIX + "/api/v1/wireless/acl";
    private final static String WIRELESS_ACL_RULES = URL_PREFIX + "/api/v1/wireless/acl/rules";
    private final static String DEVICE_URI = URL_PREFIX + "/api/v1/device";
    private final static String SUMMARY_URI = URL_PREFIX + "/api/v1/summary";
    private final static String LOGOUT_URI = URL_PREFIX + "/api/v1/logout";
    private final static String HOSTS_URI = URL_PREFIX + "/api/v1/hosts";
    private final static String LAST_FIVE_CALLLOG_URI = URL_PREFIX + "/api/v1/voip/fullcalllog";
    private final static String CUSTOMER_CALLOG_URI = URL_PREFIX + "/api/v1/profile/calllog";
    private final static String REBOOT_URI = URL_PREFIX + "/api/v1/device/reboot";
    private final static String TOKEN_URI = URL_PREFIX + "/api/v1/device/token";
    private final static String PASSWORD_RECOV_URI = URL_PREFIX + "/api/v1/password-recovery";
    private final static String PASSWORD_RECOV_VERIFY_URI = URL_PREFIX + "/api/v1/password-recovery/verify";
    private final static String PINCODE_VERIFY = URL_PREFIX + "/api/v1/pincode/verify";
    private final static String RESET_PASSWORD = URL_PREFIX + "/api/v1/reset-password";
    private final static String WAN_XDSL_URI = URL_PREFIX + "/api/v1/wan/xdsl";
    private final static String WAN_IP_URI = URL_PREFIX + "/api/v1/wan/ip";
    private final static String PROFILE_CONSUMPTION_URI = URL_PREFIX + "/api/v1/profile/consumption";
    private final static String PROFILE_REFRESH_URI = URL_PREFIX + "/api/v1/profile/refresh";
    private final static String VOIP_VOICEMAIL_URI = URL_PREFIX + "/api/v1/voip/voicemail";

    private final static String BBOX_COOKIE_NAME = "BBOX_ID";

    private CookieManager mCookieManager = new CookieManager();
    private final static String COOKIE_HEADER = "Set-Cookie";

    public void setPassword(String pass) {
        mPassword = pass;
    }

    /**
     * Authenticate to Bbox Api
     *
     * @return true if authentication has been initiated successfully
     */
    private AuthResponse authenticate() throws IOException {

        HttpConnection conn = HttpUtils.httpRequest("POST", LOGIN_URI + "?password=" + mPassword + "&remember=1");

        try {
            conn.getConn().setDoOutput(true);
            conn.write();
            if (conn.getResponseCode() == 200) {

                String token = storeCookie(conn);

                if (token != null) {
                    mAuthenticated = true;
                    return new AuthResponse(token, HttpStatus.gethttpStatus(conn.getResponseCode()));
                } else {
                    return new AuthResponse(null, HttpStatus.gethttpStatus(conn.getResponseCode()));
                }

            } else {
                return new AuthResponse(null, HttpStatus.gethttpStatus(conn.getResponseCode()));
            }
        } finally {
            conn.disconnect();
        }
    }

    private String storeCookie(HttpConnection conn) {
        Map<String, List<String>> headerFields = conn.getConn().getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIE_HEADER);

        if (cookiesHeader != null && cookiesHeader.size() > 0) {
            String cookie = HttpCookie.parse(cookiesHeader.get(0)).get(0).toString().replace("\"", "");
            try {
                mCookieManager.getCookieStore().add(new URI(BBOX_HOST), HttpCookie.parse(cookiesHeader.get(0)).get(0));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return cookie.indexOf(";") != -1 ? cookie.substring(0, cookie.indexOf(";")).split("=")[1] : "";
        }
        return null;
    }

    enum RequestType {
        VOIP,
        DEVICE_INFO,
        SUMMARY,
        GET_HOSTS,
        GET_XDSL_INFO,
        GET_IP_INFO,
        LAST_FIVE_CALL_LOG,
        CUSTOMER_CALLOG_URI,
        BBOX_TOKEN,
        WIRELESS_DATA,
        VERIFY_PASSWORD_RECOVERY,
        GET_WIFI_MAC_FILTER,
        PROFILE_CONSUMPTION,
        VOIP_VOICEMAIL;
    }

    private String isToString(InputStream is) throws IOException {
        int ch;
        StringBuilder sb = new StringBuilder();
        while ((ch = is.read()) != -1)
            sb.append((char) ch);
        return sb.toString();
    }

    private HttpResponse executeGetRequest(RequestType type, String uri, boolean skipAuth) throws IOException {

        if (!skipAuth) {
            if (!mAuthenticated && mPassword != null && !mPassword.equals("")) {
                AuthResponse authResponse = authenticate();
                if (authResponse.getStatus() != HttpStatus.OK) {
                    return getDefaultResponse(type, authResponse.getStatus());
                }
            }
        }

        HttpConnection conn = HttpUtils.httpRequest("GET", uri);

        try {
            if (!skipAuth && mCookieManager.getCookieStore().getCookies().size() > 0) {
                HttpUtils.addCookies(conn, mCookieManager);
            }

            if (conn.getResponseCode() == 200) {

                InputStream in = new BufferedInputStream(conn.getConn().getInputStream());
                String result = isToString(in);
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                switch (type) {
                    case VOIP:
                        List<VoipEntry> voipList = gson.fromJson(result,
                                new TypeToken<List<VoipEntry>>() {
                                }.getType());

                        return new VoipResponse(voipList, HttpStatus.OK);
                    case PROFILE_CONSUMPTION:
                        List<ProfileEntry> consumptionList = gson.fromJson(result,
                                new TypeToken<List<ProfileEntry>>() {
                                }.getType());

                        return new ConsumptionResponse(consumptionList, HttpStatus.OK);
                    case VOIP_VOICEMAIL:
                        List<VoiceMailEntry> voiceMailList = gson.fromJson(result,
                                new TypeToken<List<VoiceMailEntry>>() {
                                }.getType());

                        return new VoiceMailResponse(voiceMailList, HttpStatus.OK);
                    case DEVICE_INFO:
                        List<BboxDeviceEntry> deviceInfoList = gson.fromJson(result,
                                new TypeToken<List<BboxDeviceEntry>>() {
                                }.getType());

                        return new DeviceInfoResponse(deviceInfoList, HttpStatus.OK);
                    case SUMMARY:
                        List<ApiSummary> summary = gson.fromJson(result,
                                new TypeToken<List<ApiSummary>>() {
                                }.getType());

                        return new SummaryResponse(summary, HttpStatus.OK);
                    case GET_HOSTS:
                        List<HostItem> hosts = gson.fromJson(result,
                                new TypeToken<List<HostItem>>() {
                                }.getType());

                        return new HostsResponse(hosts, HttpStatus.OK);
                    case GET_XDSL_INFO:
                        List<WanItem> xdslInfo = gson.fromJson(result,
                                new TypeToken<List<WanItem>>() {
                                }.getType());

                        return new WanXdslResponse(xdslInfo, HttpStatus.OK);
                    case GET_IP_INFO:
                        List<WanIp> ipInfo = gson.fromJson(result,
                                new TypeToken<List<WanIp>>() {
                                }.getType());

                        return new WanIpResponse(ipInfo, HttpStatus.OK);
                    case LAST_FIVE_CALL_LOG:
                        List<CallLogList> last5CallLog = gson.fromJson(result,
                                new TypeToken<List<CallLogList>>() {
                                }.getType());

                        return new CallLogVoipResponse(last5CallLog, HttpStatus.OK);
                    case CUSTOMER_CALLOG_URI:
                        System.out.println(result);
                        List<fr.bmartel.bboxapi.model.profile.CallLogList> customerCallLog = gson.fromJson(result,
                                new TypeToken<List<fr.bmartel.bboxapi.model.profile.CallLogList>>() {
                                }.getType());

                        return new CallLogCustomerResponse(customerCallLog, HttpStatus.OK);
                    case WIRELESS_DATA:
                        List<WirelessItem> wirelessList = gson.fromJson(result,
                                new TypeToken<List<WirelessItem>>() {
                                }.getType());

                        return new WirelessResponse(wirelessList, HttpStatus.OK);
                    case GET_WIFI_MAC_FILTER:
                        List<AclItem> aclList = gson.fromJson(result,
                                new TypeToken<List<AclItem>>() {
                                }.getType());

                        return new WirelessAclResponse(aclList, HttpStatus.OK);
                    case VERIFY_PASSWORD_RECOVERY:
                        storeCookie(conn);
                        List<VerifyRecovery> verifyList = gson.fromJson(result,
                                new TypeToken<List<VerifyRecovery>>() {
                                }.getType());
                        return new VerifyRecoveryResponse(verifyList, HttpStatus.OK);
                    case BBOX_TOKEN:
                        List<BboxDevice> deviceList = gson.fromJson(result,
                                new TypeToken<List<BboxDevice>>() {
                                }.getType());

                        return new BboxTokenResponse(deviceList, HttpStatus.OK);
                }

            } else if (conn.getResponseCode() == 401) {
                // authenticate & retry
                mAuthenticated = false;
            } else {
                return getDefaultResponse(type, HttpStatus.gethttpStatus(conn.getResponseCode()));
            }
        } finally {
            conn.disconnect();
        }
        return getDefaultResponse(type, HttpStatus.UNKNOWN);
    }

    /*
    private HttpStatus executeDeleteRequest(RequestType type, String uri, boolean skipAuth) throws IOException {

        if (!skipAuth) {
            if (!mAuthenticated && mPassword != null && !mPassword.equals("")) {
                AuthResponse authResponse = authenticate();
                if (authResponse.getStatus() != HttpStatus.OK) {
                    return getDefaultResponse(type, authResponse.getStatus());
                }
            }
        }

        HttpConnection conn = HttpUtils.httpRequest("DELETE", uri);

        try {
            if (!skipAuth && mCookieManager.getCookieStore().getCookies().size() > 0) {
                HttpUtils.addCookies(conn, mCookieManager);
            }

            if (conn.getData().length > 0) {
                conn.getConn().setDoOutput(true);
                conn.write();
            }

            if (conn.getResponseCode() == 200) {
                return getDefaultResponse(type, HttpStatus.OK);
            } else if (conn.getResponseCode() == 401) {
                // authenticate & retry
                mAuthenticated = false;
            } else {
                return getDefaultResponse(type, HttpStatus.gethttpStatus(conn.getResponseCode()));
            }
        } finally {
            conn.disconnect();
        }
        return getDefaultResponse(type, HttpStatus.UNKNOWN);
    }
    */

    private HttpResponse getDefaultResponse(RequestType type, HttpStatus status) {

        switch (type) {
            case VOIP:
                return new VoipResponse(null, status);
            case PROFILE_CONSUMPTION:
                return new ConsumptionResponse(null, status);
            case VOIP_VOICEMAIL:
                return new VoiceMailResponse(null, status);
            case DEVICE_INFO:
                return new DeviceInfoResponse(null, status);
            case SUMMARY:
                return new SummaryResponse(null, status);
            case GET_HOSTS:
                return new HostsResponse(null, status);
            case LAST_FIVE_CALL_LOG:
                return new CallLogVoipResponse(null, status);
            case WIRELESS_DATA:
                return new WirelessResponse(null, status);
            case GET_XDSL_INFO:
                return new WanXdslResponse(null, status);
            case GET_IP_INFO:
                return new WanIpResponse(null, status);
        }
        return new VoipResponse(null, HttpStatus.UNKNOWN);
    }

    private HttpStatus executeRequest(HttpConnection conn, boolean auth, boolean skipAuth) throws IOException {
        try {
            if (!skipAuth) {
                if (!mAuthenticated && auth) {
                    AuthResponse authResponse = authenticate();
                    if (authResponse.getStatus() != HttpStatus.OK) {
                        return HttpStatus.UNAUTHORIZED;
                    }
                } else if (!auth) {
                    mCookieManager.getCookieStore().removeAll();
                }
            }

            if (!skipAuth && mCookieManager.getCookieStore().getCookies().size() > 0) {
                HttpUtils.addCookies(conn, mCookieManager);
            }

            if (conn.getData().length > 0 || conn.getConn().getRequestMethod().equals("POST")) {
                conn.getConn().setDoOutput(true);
                conn.write();
            }

            if (conn.getResponseCode() == 401 && auth) {
                // authenticate & retry
                mAuthenticated = false;
            } else {
                storeCookie(conn);
                return HttpStatus.gethttpStatus(conn.getResponseCode());
            }
        } finally {
            conn.disconnect();
        }
        return HttpStatus.UNKNOWN;
    }

    /**
     * Set Bbox display state (luminosity of Bbox device)
     *
     * @param state ON/OFF
     * @return true if request has been successfully initiated
     */
    public HttpStatus setBboxDisplayState(boolean state) throws IOException {
        int luminosity = state ? 100 : 0;
        HttpConnection conn = HttpUtils.httpRequest("PUT", DISPLAY_STATE_URI + "?luminosity=" + luminosity);
        return executeRequest(conn, true, false);
    }


    /**
     * Set SummaryWifi state
     *
     * @param state wifi state ON/OFF
     * @return true if request has been successfully initiated
     */
    public HttpStatus setWifiState(boolean state) throws IOException {
        int status = state ? 1 : 0;
        HttpConnection conn = HttpUtils.httpRequest("PUT", WIRELESS_URI + "?radio.enable=" + status);
        return executeRequest(conn, true, false);
    }

    /**
     * Enable/Disable Wifi Mac filtering.
     *
     * @param state state of Mac filtering
     * @return request status
     */
    public HttpStatus setWifiMacFilter(boolean state) throws IOException {
        int status = state ? 1 : 0;
        HttpConnection conn = HttpUtils.httpRequest("PUT", WIRELESS_ACL_URI + "?enable=" + status);
        return executeRequest(conn, true, false);
    }

    /**
     * Delete ACL rule.
     *
     * @param ruleIndex number of rule
     * @return request status
     */
    public HttpStatus deleteMacFilterRule(int ruleIndex) throws IOException {
        HttpConnection conn = HttpUtils.httpRequest("DELETE", WIRELESS_ACL_RULES + "/" + ruleIndex);
        return executeRequest(conn, true, false);
    }

    /**
     * Update Wifi Mac filtering Rule.
     *
     * @param state state of Mac filtering
     * @return request status
     */
    public HttpStatus updateWifiMacFilterRule(int ruleIndex, boolean state, String macAddress, String ip) throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("enable", String.valueOf(state ? 1 : 0));
        params.put("macaddress", macAddress);
        params.put("device", ip);
        HttpConnection conn = HttpUtils.httpPutFormRequest(WIRELESS_ACL_RULES + "/" + ruleIndex, params);
        return executeRequest(conn, true, false);
    }

    /**
     * Create Wifi Mac filtering rules.
     *
     * @param enable     enable rule
     * @param macAddress mac address to filter
     * @return request status
     */
    public HttpStatus createWifiMacFilterRule(boolean enable, String macAddress, String ip) throws IOException {

        BboxTokenResponse response = (BboxTokenResponse) executeGetRequest(RequestType.BBOX_TOKEN, TOKEN_URI, false);

        if (response.getStatus() == HttpStatus.OK) {

            if (response.getDeviceList().size() > 0 && response.getDeviceList().get(0).getBboxToken().getToken() !=
                    null) {

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("enable", String.valueOf(enable ? 1 : 0));
                params.put("macaddress", macAddress);
                params.put("device", ip);

                HttpConnection conn = HttpUtils.httpPostFormRequest(WIRELESS_ACL_RULES + "?btoken=" + response.getDeviceList()
                        .get(0)
                        .getBboxToken().getToken(), params);

                return executeRequest(conn, true, false);
            }
        }
        return response.getStatus();
    }

    /**
     * Reboot bbox
     */
    public HttpStatus reboot() throws IOException {
        BboxTokenResponse response = (BboxTokenResponse) executeGetRequest(RequestType.BBOX_TOKEN, TOKEN_URI, false);

        if (response.getStatus() == HttpStatus.OK) {
            if (response.getDeviceList().size() > 0 &&
                    response.getDeviceList().get(0).getBboxToken().getToken() != null) {
                HttpConnection conn = HttpUtils.httpRequest("POST", REBOOT_URI + "?btoken=" + response.getDeviceList().get(0).getBboxToken().getToken());
                return executeRequest(conn, true, false);
            }
        }
        return response.getStatus();
    }

    /**
     * Reboot bbox
     */
    public HttpStatus startPasswordRecovery() throws IOException {
        HttpConnection conn = HttpUtils.httpRequest("POST", PASSWORD_RECOV_URI);
        return executeRequest(conn, false, false);
    }

    /**
     * Reboot bbox
     */
    public HttpStatus sendPincodeVerify(String pincode) throws IOException {
        HttpConnection conn = HttpUtils.httpRequest("POST", PINCODE_VERIFY + "?pincode=" + pincode);
        return executeRequest(conn, false, false);
    }

    /**
     * reset password
     *
     * @param password
     */
    public HttpStatus resetPassword(String password) throws IOException {

        BboxTokenResponse response = (BboxTokenResponse) executeGetRequest(RequestType.BBOX_TOKEN, TOKEN_URI, true);

        if (response.getStatus() == HttpStatus.OK) {

            if (response.getDeviceList().size() > 0 && response.getDeviceList().get(0).getBboxToken().getToken() !=
                    null) {

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("password", password);

                HttpConnection conn = HttpUtils.httpPostFormRequest(RESET_PASSWORD +
                        "?btoken=" + response.getDeviceList().get(0).getBboxToken().getToken(), params);

                return executeRequest(conn, true, true);
            }
        }
        return response.getStatus();
    }


    /**
     * login api
     *
     * @return true if request has been successfully initiated
     */
    public HttpStatus voipDial(int lineNumber, String phoneNumber) throws IOException {
        HttpConnection conn = HttpUtils.httpRequest("PUT", DIAL_URI + "?line=" + lineNumber + "&number=" +
                URLEncoder.encode(phoneNumber.replaceAll("\\s+", "").replaceAll("\\+", "00"), "UTF-8"));
        return executeRequest(conn, true, false);
    }


    /**
     * Verify password recovery status.
     */
    public VerifyRecoveryResponse verifyPasswordRecovery() throws IOException {
        return (VerifyRecoveryResponse) executeGetRequest(RequestType.VERIFY_PASSWORD_RECOVERY,
                PASSWORD_RECOV_VERIFY_URI, false);
    }

    /**
     * VoipItem data
     */
    public VoipResponse getVoipData() throws IOException {
        return (VoipResponse) executeGetRequest(RequestType.VOIP, VOIP_URI, false);
    }


    /**
     * Profile Consumption.
     */
    public ConsumptionResponse getConsumptionData() throws IOException {

        return (ConsumptionResponse) executeGetRequest(
                RequestType.PROFILE_CONSUMPTION, PROFILE_CONSUMPTION_URI, false);
    }

    /**
     * Get Voice mail.
     */
    public VoiceMailResponse getVoiceMailData() throws IOException {

        // call voip/voicemail
        VoiceMailResponse voiceMailResponse = (VoiceMailResponse) executeGetRequest(RequestType.VOIP_VOICEMAIL,
                VOIP_VOICEMAIL_URI, false);

        List<VoiceMailItem> voiceMailList = voiceMailResponse.getVoiceMailList().get(0).getVoiceMailItems();

        if (voiceMailList.size() > 0) {

            //rebuild url in case id_session is missing
            if (!voiceMailList.get(0).getLinkMsg().contains("id_session")) {

                ConsumptionResponse consumptionResponse = getConsumptionData();

                for (VoiceMailItem item : voiceMailList) {

                    try {
                        URL url = new URL(item.getLinkMsg());

                        Map<String, String> params = NetworkUtils.getQueryMap(url.getQuery());

                        item.setLinkMsg("http://www.espaceclient.bbox.bouyguestelecom.fr/api/api_suivibbox.phtml?" +
                                "idmsg=" + params.get("idmsg") + "&" +
                                "uid=" + params.get("uid") + "&" +
                                "idbal=" + params.get("idbal") + "&" +
                                "rang_tel=" + params.get("rang_tel") + "&" +
                                "pg=play_msg&" +
                                "id_session=" + consumptionResponse.getProfileList().get(0).getProfile()
                                .getSession());

                    } catch (MalformedURLException e) {
                    }
                }
            }

            // check the first URL, set to empty string if not valid
            HttpConnection conn = HttpUtils.httpRequest("HEAD", voiceMailList.get(0).getLinkMsg());

            try {
                if (conn.getResponseCode() != 200 || conn.getConn().getHeaderField("Content-Type").equals("text/html")) {
                    for (VoiceMailItem item : voiceMailList) {
                        item.setLinkMsg("");
                    }
                } else {
                    voiceMailResponse.setValidSession(true);
                }
            } finally {
                conn.disconnect();
            }
        }
        return voiceMailResponse;
    }

    /**
     * delete voice mail
     *
     * @param id voicemail id
     * @return http response
     */
    public HttpStatus deleteVoiceMail(final int id) throws IOException {
        HttpConnection conn = HttpUtils.httpRequest("DELETE", VOIP_VOICEMAIL_URI + "/1/" + id);
        return executeRequest(conn, true, false);
    }

    /**
     * mark voice mail as read.
     *
     * @param id voicemail id
     * @return http response
     */
    public HttpStatus readVoiceMail(final int id) throws IOException {
        HttpConnection conn = HttpUtils.httpRequest("PUT", VOIP_VOICEMAIL_URI + "/1/" + id);
        return executeRequest(conn, true, false);
    }

    /**
     * Refresh profile.
     *
     * @param action type of data to refresh
     */
    public HttpStatus refreshProfile(RefreshAction action) throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("action", action.getAction());
        HttpConnection conn = HttpUtils.httpPutFormRequest(PROFILE_REFRESH_URI, params);
        return executeRequest(conn, true, false);
    }

    /**
     * Bbox device api
     */
    public DeviceInfoResponse getDeviceInfo(boolean useAuth) throws IOException {
        return (DeviceInfoResponse) executeGetRequest(RequestType.DEVICE_INFO, DEVICE_URI, !useAuth);
    }

    /**
     * Get wifi mac filter information.
     *
     * @return request status
     */
    public WirelessAclResponse getWifiMacFilterInfo() throws IOException {
        return (WirelessAclResponse) executeGetRequest(RequestType.GET_WIFI_MAC_FILTER, WIRELESS_ACL_URI, false);
    }

    /**
     * Retrieve summary api result
     */
    public SummaryResponse getDeviceSummary(boolean useAuth) throws IOException {
        return (SummaryResponse) executeGetRequest(RequestType.SUMMARY, SUMMARY_URI, !useAuth);
    }

    /**
     * Retrieve all hosts
     */
    public HostsResponse getHosts() throws IOException {
        return (HostsResponse) executeGetRequest(RequestType.GET_HOSTS, HOSTS_URI, true);
    }

    /**
     * Get XDSL information.
     */
    public WanXdslResponse getXdslInfo() throws IOException {
        return (WanXdslResponse) executeGetRequest(RequestType.GET_XDSL_INFO, WAN_XDSL_URI, true);
    }

    /**
     * Get IP information.
     */
    public WanIpResponse getIpInfo() throws IOException {
        return (WanIpResponse) executeGetRequest(RequestType.GET_IP_INFO, WAN_IP_URI, true);
    }

    /**
     * Retrieve last five call log entries
     */
    public CallLogVoipResponse getLastCallLog(final int lineNumber) throws IOException {
        return (CallLogVoipResponse) executeGetRequest(RequestType.LAST_FIVE_CALL_LOG, LAST_FIVE_CALLLOG_URI + "/" + lineNumber, false);
    }

    /**
     * Retrieve callLog from customer space.
     */
    public CallLogCustomerResponse getCallLog(final int lineNumber) throws IOException {
        return (CallLogCustomerResponse) executeGetRequest(RequestType.CUSTOMER_CALLOG_URI, CUSTOMER_CALLOG_URI + "/" + lineNumber, false);
    }

    public WirelessResponse getWirelessData() throws IOException {
        return (WirelessResponse) executeGetRequest(RequestType.WIRELESS_DATA, WIRELESS_URI, false);
    }

    /**
     * Logout
     */
    public HttpStatus logout() throws IOException {
        HttpConnection conn = HttpUtils.httpRequest("POST", LOGOUT_URI);

        mTokenHeader = "";
        mAuthenticated = false;
        mCookieManager.getCookieStore().removeAll();

        return executeRequest(conn, false, false);
    }
}
