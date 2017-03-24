/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2017 Bertrand Martel
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
import fr.bmartel.bboxapi.model.recovery.VerifyRecovery;
import fr.bmartel.bboxapi.model.token.BboxDevice;
import fr.bmartel.bboxapi.response.*;
import fr.bmartel.bboxapi.model.summary.ApiSummary;
import fr.bmartel.bboxapi.model.device.BboxDeviceEntry;
import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.model.host.HostItem;
import fr.bmartel.bboxapi.model.voip.CallLogList;
import fr.bmartel.bboxapi.model.voip.VoipEntry;
import fr.bmartel.bboxapi.model.wireless.WirelessItem;
import fr.bmartel.bboxapi.util.RouterApiUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Bbox Router Api client
 *
 * @author Bertrand Martel
 */
public class BboxApi {

    private String mTokenHeader = "";

    private boolean mAuthenticated = false;

    private String mPassword;

    private static int AUTH_MAX_RETRY = 2;

    private int mRetry;

    private final static String BBOX_HOST = "gestionbbox.lan";
    private final static String LOGIN_URI = "http://" + BBOX_HOST + "/api/v1/login";
    private final static String VOIP_URI = "http://" + BBOX_HOST + "/api/v1/voip";
    private final static String DIAL_URI = "http://" + BBOX_HOST + "/api/v1/voip/dial";
    private final static String DISPLAY_STATE_URI = "http://" + BBOX_HOST + "/api/v1/device/display";
    private final static String WIRELESS_URI = "http://" + BBOX_HOST + "/api/v1/wireless";
    private final static String DEVICE_URI = "http://" + BBOX_HOST + "/api/v1/device";
    private final static String SUMMARY_URI = "http://" + BBOX_HOST + "/api/v1/summary";
    private final static String LOGOUT_URI = "http://" + BBOX_HOST + "/api/v1/logout";
    private final static String HOSTS_URI = "http://" + BBOX_HOST + "/api/v1/hosts";
    private final static String CALLLOG_URI = "http://" + BBOX_HOST + "/api/v1/voip/fullcalllog/1";
    private final static String REBOOT_URI = "http://" + BBOX_HOST + "/api/v1/device/reboot";
    private final static String TOKEN_URI = "http://" + BBOX_HOST + "/api/v1/device/token";
    private final static String PASSWORD_RECOV_URI = "http://" + BBOX_HOST + "/api/v1/password-recovery";
    private final static String PASSWORD_RECOV_VERIFY_URI = "http://" + BBOX_HOST + "/api/v1/password-recovery/verify";
    private final static String PINCODE_VERIFY = "http://" + BBOX_HOST + "/api/v1/pincode/verify";
    private final static String RESET_PASSWORD = "http://" + BBOX_HOST + "/api/v1/reset-password";

    private final static String BBOX_COOKIE_NAME = "BBOX_ID";

    private CookieStore mCookieStore = new BasicCookieStore();

    private CloseableHttpClient mHttpClient = HttpClients.custom()
            .setDefaultCookieStore(mCookieStore)
            .build();

    public void setPassword(String pass) {
        mPassword = pass;
    }

    /**
     * Authenticate to Bbox Api
     *
     * @return true if authentication has been initiated successfully
     */
    private AuthResponse authenticate() {

        HttpUriRequest request = new HttpPost(LOGIN_URI + "?password=" + mPassword + "&remember=1");

        CloseableHttpResponse response = null;

        try {
            response = mHttpClient.execute(request);

            StatusLine statusLine = response.getStatusLine();

            try {
                if (response.getStatusLine().getStatusCode() == 200) {

                    String token = storeCookie(response);

                    if (token != null) {
                        mAuthenticated = true;
                        return new AuthResponse(token, HttpStatus.OK, statusLine);
                    } else {
                        return new AuthResponse(null, HttpStatus.NO_COOKIE, statusLine);
                    }

                } else {
                    return new AuthResponse(null,
                            RouterApiUtils.gethttpStatus(response.getStatusLine().getStatusCode()), statusLine);
                }
            } finally {
                response.close();
            }
        } catch (IOException e) {
            //ignored
        }
        return new AuthResponse(null, HttpStatus.UNKNOWN, null);
    }

    private String storeCookie(CloseableHttpResponse response) {

        Header cookieHeader = response.getFirstHeader("set-cookie");

        if (cookieHeader != null) {
            mTokenHeader = cookieHeader.getValue();
            String token = mTokenHeader.substring(8, mTokenHeader.indexOf(';'));
            mCookieStore.clear();

            BasicClientCookie cookie = new BasicClientCookie(BBOX_COOKIE_NAME, token);

            cookie.setDomain(BBOX_HOST);
            mCookieStore.addCookie(cookie);

            return token;
        }
        return null;
    }

    enum RequestType {
        VOIP,
        DEVICE_INFO,
        SUMMARY,
        GET_HOSTS,
        CALL_LOG,
        BBOX_TOKEN,
        WIRELESS_DATA,
        VERIFY_PASSWORD_RECOVERY;
    }

    private HttpResponse executeGetRequest(RequestType type, String uri, boolean skipAuth) {

        if (!skipAuth) {
            if (!mAuthenticated && mPassword != null && !mPassword.equals("")) {
                AuthResponse authResponse = authenticate();
                if (authResponse.getStatus() != HttpStatus.OK) {
                    return getDefaultResponse(type, authResponse.getStatus(), authResponse.getStatusLine());
                }
                mRetry = 0;
            } else {
                mCookieStore.clear();
            }
        }

        HttpGet voipRequest = new HttpGet(uri);

        CloseableHttpResponse response = null;
        try {

            response = mHttpClient.execute(voipRequest);

            StatusLine statusLine = response.getStatusLine();

            try {
                if (response.getStatusLine().getStatusCode() == 200) {

                    String result = EntityUtils.toString(response.getEntity());
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();

                    switch (type) {
                        case VOIP:
                            List<VoipEntry> voipList = gson.fromJson(result,
                                    new TypeToken<List<VoipEntry>>() {
                                    }.getType());

                            return new VoipResponse(voipList, HttpStatus.OK, statusLine);
                        case DEVICE_INFO:
                            List<BboxDeviceEntry> deviceInfoList = gson.fromJson(result,
                                    new TypeToken<List<BboxDeviceEntry>>() {
                                    }.getType());

                            return new DeviceInfoResponse(deviceInfoList, HttpStatus.OK, statusLine);
                        case SUMMARY:
                            List<ApiSummary> summary = gson.fromJson(result,
                                    new TypeToken<List<ApiSummary>>() {
                                    }.getType());

                            return new SummaryResponse(summary, HttpStatus.OK, statusLine);
                        case GET_HOSTS:
                            List<HostItem> hosts = gson.fromJson(result,
                                    new TypeToken<List<HostItem>>() {
                                    }.getType());

                            return new HostsResponse(hosts, HttpStatus.OK, statusLine);
                        case CALL_LOG:
                            List<CallLogList> callLog = gson.fromJson(result,
                                    new TypeToken<List<CallLogList>>() {
                                    }.getType());

                            return new CallLogResponse(callLog, HttpStatus.OK, statusLine);
                        case WIRELESS_DATA:
                            List<WirelessItem> wirelessList = gson.fromJson(result,
                                    new TypeToken<List<WirelessItem>>() {
                                    }.getType());

                            return new WirelessResponse(wirelessList, HttpStatus.OK, statusLine);
                        case VERIFY_PASSWORD_RECOVERY:
                            List<VerifyRecovery> verifyList = gson.fromJson(result,
                                    new TypeToken<List<VerifyRecovery>>() {
                                    }.getType());
                            return new VerifyRecoveryResponse(verifyList, HttpStatus.OK, statusLine);
                        case BBOX_TOKEN:
                            List<BboxDevice> deviceList = gson.fromJson(result,
                                    new TypeToken<List<BboxDevice>>() {
                                    }.getType());

                            return new BboxTokenResponse(deviceList, HttpStatus.OK, statusLine);
                    }

                } else if (response.getStatusLine().getStatusCode() == 401) {
                    // authenticate & retry
                    mAuthenticated = false;
                    if (mRetry < (AUTH_MAX_RETRY + 1)) {
                        mRetry++;
                        return executeGetRequest(type, uri, false);
                    }
                    mRetry = 0;
                } else {
                    return getDefaultResponse(type, RouterApiUtils.gethttpStatus(response.getStatusLine()
                            .getStatusCode()), statusLine);
                }
            } finally {
                response.close();
            }
        } catch (IOException e) {
            //ignored
        }
        return getDefaultResponse(type, HttpStatus.UNKNOWN, null);
    }

    private HttpResponse getDefaultResponse(RequestType type, HttpStatus status, StatusLine statusLine) {

        switch (type) {
            case VOIP:
                return new VoipResponse(null, status, statusLine);
            case DEVICE_INFO:
                return new DeviceInfoResponse(null, status, statusLine);
            case SUMMARY:
                return new SummaryResponse(null, status, statusLine);
            case GET_HOSTS:
                return new HostsResponse(null, status, statusLine);
            case CALL_LOG:
                return new CallLogResponse(null, status, statusLine);
            case WIRELESS_DATA:
                return new WirelessResponse(null, status, statusLine);
        }
        return new VoipResponse(null, HttpStatus.UNKNOWN, null);
    }

    private HttpStatus executeRequest(HttpEntityEnclosingRequestBase request, boolean auth, boolean skipAuth) {

        if (!skipAuth) {
            if (!mAuthenticated && auth) {
                AuthResponse authResponse = authenticate();
                if (authResponse.getStatus() != HttpStatus.OK) {
                    return HttpStatus.UNAUTHORIZED;
                }
                mRetry = 0;
            } else if (!auth) {
                mCookieStore.clear();
            }
        }

        CloseableHttpResponse response = null;
        try {
            response = mHttpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 401 && auth) {
                // authenticate & retry
                mAuthenticated = false;
                if (mRetry < (AUTH_MAX_RETRY + 1)) {
                    mRetry++;
                    return executeRequest(request, auth, skipAuth);
                }
                mRetry = 0;
            } else {
                storeCookie(response);
                try {
                    return RouterApiUtils.gethttpStatus(response.getStatusLine().getStatusCode());
                } finally {
                    response.close();
                }
            }
        } catch (IOException e) {
            //ignored
        }
        return HttpStatus.UNKNOWN;
    }

    /**
     * Set Bbox display state (luminosity of Bbox device)
     *
     * @param state ON/OFF
     * @return true if request has been successfully initiated
     */
    public HttpStatus setBboxDisplayState(boolean state) {

        int luminosity = state ? 100 : 0;

        HttpPut displayStateRequest = new HttpPut(DISPLAY_STATE_URI + "?luminosity=" + luminosity);

        return executeRequest(displayStateRequest, true, false);
    }


    /**
     * Set SummaryWifi state
     *
     * @param state wifi state ON/OFF
     * @return true if request has been successfully initiated
     */
    public HttpStatus setWifiState(boolean state) {

        int status = state ? 1 : 0;

        HttpPut wifiRequest = new HttpPut(WIRELESS_URI + "?radio.enable=" + status);

        return executeRequest(wifiRequest, true, false);
    }

    /**
     * Reboot bbox
     *
     * @return
     */
    public HttpStatus reboot() {

        BboxTokenResponse response = (BboxTokenResponse) executeGetRequest(RequestType.BBOX_TOKEN, TOKEN_URI, false);

        if (response.getStatus() == HttpStatus.OK) {

            if (response.getDeviceList().size() > 0 && response.getDeviceList().get(0).getBboxToken().getToken() !=
                    null) {
                HttpPost rebootRequest = new HttpPost(REBOOT_URI + "?btoken=" + response.getDeviceList().get(0)
                        .getBboxToken().getToken());

                return executeRequest(rebootRequest, true, false);
            }
        }
        return response.getStatus();
    }

    /**
     * Reboot bbox
     *
     * @return
     */
    public HttpStatus startPasswordRecovery() {
        HttpPost rebootRequest = new HttpPost(PASSWORD_RECOV_URI);
        return executeRequest(rebootRequest, false, false);
    }

    /**
     * Reboot bbox
     *
     * @return
     */
    public HttpStatus sendPincodeVerify(String pincode) {
        HttpPost pincodeRequest = new HttpPost(PINCODE_VERIFY + "?pincode=" + pincode);
        return executeRequest(pincodeRequest, false, false);
    }

    /**
     * reset password
     *
     * @param password
     * @return
     */
    public HttpStatus resetPassword(String password) {

        BboxTokenResponse response = (BboxTokenResponse) executeGetRequest(RequestType.BBOX_TOKEN, TOKEN_URI, true);

        if (response.getStatus() == HttpStatus.OK) {

            if (response.getDeviceList().size() > 0 && response.getDeviceList().get(0).getBboxToken().getToken() !=
                    null) {
                HttpPost resetRequest = new HttpPost(RESET_PASSWORD + "?btoken=" + response.getDeviceList().get(0)
                        .getBboxToken().getToken());

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("password", password));

                try {
                    resetRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return executeRequest(resetRequest, true, true);
            }
        }
        return response.getStatus();
    }


    /**
     * login api
     *
     * @return true if request has been successfully initiated
     */
    public HttpStatus voipDial(int lineNumber, String phoneNumber) {

        HttpPut dialRequest = new HttpPut(DIAL_URI + "?line=" + lineNumber + "&number=" + phoneNumber);

        return executeRequest(dialRequest, true, false);
    }


    /**
     * Verify password recovery status.
     */
    public VerifyRecoveryResponse verifyPasswordRecovery() {
        return (VerifyRecoveryResponse) executeGetRequest(RequestType.VERIFY_PASSWORD_RECOVERY,
                PASSWORD_RECOV_VERIFY_URI, false);
    }

    /**
     * VoipItem data
     */
    public VoipResponse getVoipData() {
        return (VoipResponse) executeGetRequest(RequestType.VOIP, VOIP_URI, false);
    }

    /**
     * Bbox device api
     */
    public DeviceInfoResponse getDeviceInfo(boolean useAuth) {
        return (DeviceInfoResponse) executeGetRequest(RequestType.DEVICE_INFO, DEVICE_URI, !useAuth);
    }

    /**
     * Retrieve summary api result
     */
    public SummaryResponse getDeviceSummary(boolean useAuth) {
        return (SummaryResponse) executeGetRequest(RequestType.SUMMARY, SUMMARY_URI, !useAuth);
    }

    /**
     * Retrieve all hosts
     */
    public HostsResponse getHosts() {
        return (HostsResponse) executeGetRequest(RequestType.GET_HOSTS, HOSTS_URI, false);
    }


    /**
     * Retrieve full call log
     */
    public CallLogResponse getFullCallLog() {
        return (CallLogResponse) executeGetRequest(RequestType.CALL_LOG, CALLLOG_URI, false);
    }


    public WirelessResponse getWirelessData() {
        return (WirelessResponse) executeGetRequest(RequestType.WIRELESS_DATA, WIRELESS_URI, false);
    }

    /**
     * Logout
     */
    public HttpStatus logout() {

        HttpPost logoutRequest = new HttpPost(LOGOUT_URI);

        mTokenHeader = "";
        mAuthenticated = false;
        mCookieStore.clear();

        return executeRequest(logoutRequest, false, false);
    }
}
