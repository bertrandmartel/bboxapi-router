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
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
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

        HttpPost authenticatePost = new HttpPost(
                LOGIN_URI + "?password=" + mPassword + "&remember=1");

        CloseableHttpResponse response = null;

        try {
            response = mHttpClient.execute(authenticatePost);

            StatusLine statusLine = response.getStatusLine();

            try {
                if (response.getStatusLine().getStatusCode() == 200) {

                    Header cookieHeader = response.getFirstHeader("set-cookie");

                    if (cookieHeader != null) {
                        mTokenHeader = cookieHeader.getValue();
                        String token = mTokenHeader.substring(8, mTokenHeader.indexOf(';'));
                        mAuthenticated = true;

                        mCookieStore.clear();

                        BasicClientCookie cookie = new BasicClientCookie(BBOX_COOKIE_NAME, token);

                        cookie.setDomain(BBOX_HOST);
                        mCookieStore.addCookie(cookie);

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

    enum RequestType {
        VOIP,
        DEVICE_INFO,
        SUMMARY,
        GET_HOSTS,
        CALL_LOG,
        WIRELESS_DATA;
    }

    private HttpResponse executeGetRequest(RequestType type, String uri, boolean needAuth) {

        if (!mAuthenticated && needAuth) {
            AuthResponse authResponse = authenticate();
            if (authResponse.getStatus() != HttpStatus.OK) {
                return getDefaultResponse(type, authResponse.getStatus(), authResponse.getStatusLine());
            }
            mRetry = 0;
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
                    }

                } else if (response.getStatusLine().getStatusCode() == 401 && needAuth) {
                    // authenticate & retry
                    mAuthenticated = false;
                    if (mRetry < (AUTH_MAX_RETRY + 1)) {
                        mRetry++;
                        return executeGetRequest(type, uri, needAuth);
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

    private HttpStatus executeRequest(HttpEntityEnclosingRequestBase request) {

        if (!mAuthenticated) {
            AuthResponse authResponse = authenticate();
            if (authResponse.getStatus() != HttpStatus.OK) {
                return HttpStatus.UNAUTHORIZED;
            }
            mRetry = 0;
        }

        CloseableHttpResponse response = null;
        try {
            response = mHttpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 401) {
                // authenticate & retry
                mAuthenticated = false;
                if (mRetry < (AUTH_MAX_RETRY + 1)) {
                    mRetry++;
                    return executeRequest(request);
                }
                mRetry = 0;
            } else {
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
     * Voip dial a phone number
     *
     * @return true if request has been successfully initiated
     */
    public HttpStatus voipDial(int lineNumber, String phoneNumber) {

        HttpPut dialRequest = new HttpPut(DIAL_URI + "?line=" + lineNumber + "&number=" + phoneNumber);

        return executeRequest(dialRequest);
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

        return executeRequest(displayStateRequest);
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

        return executeRequest(wifiRequest);
    }

    /**
     * VoipItem data
     *
     * @return true if request has been successfully initiated
     */
    public VoipResponse getVoipData() {
        return (VoipResponse) executeGetRequest(RequestType.VOIP, VOIP_URI, true);
    }

    /**
     * Bbox device api
     *
     * @return true if request has been successfully initiated
     */
    public DeviceInfoResponse getDeviceInfo() {
        return (DeviceInfoResponse) executeGetRequest(RequestType.DEVICE_INFO, DEVICE_URI, true);
    }

    /**
     * Retrieve summary api result
     *
     * @return true if request has been successfully initiated
     */
    public SummaryResponse getDeviceSummary() {
        return (SummaryResponse) executeGetRequest(RequestType.SUMMARY, SUMMARY_URI, false);
    }

    /**
     * Retrieve all hosts
     *
     * @return true if request has been successfully initiated
     */
    public HostsResponse getHosts() {
        return (HostsResponse) executeGetRequest(RequestType.GET_HOSTS, HOSTS_URI, true);
    }


    /**
     * Retrieve full call log
     *
     * @return true if request has been successfully initiated
     */
    public CallLogResponse getFullCallLog() {
        return (CallLogResponse) executeGetRequest(RequestType.CALL_LOG, CALLLOG_URI, true);
    }


    public WirelessResponse getWirelessData() {
        return (WirelessResponse) executeGetRequest(RequestType.WIRELESS_DATA, WIRELESS_URI, true);
    }

    /**
     * Logout
     *
     * @return true if logout has been initiated successfully
     */
    public HttpStatus logout() {

        HttpPost logoutRequest = new HttpPost(LOGOUT_URI);

        mTokenHeader = "";
        mAuthenticated = false;
        mCookieStore.clear();

        return executeRequest(logoutRequest);
    }
}
