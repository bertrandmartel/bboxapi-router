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
package fr.bmartel.bboxapi.utils;

import fr.bmartel.bboxapi.model.HttpConnection;

import java.io.IOException;
import java.net.*;
import java.util.Map;

/**
 * Utility functions to use HttpURLConnection.
 *
 * @author Bertrand Martel
 */
public class HttpUtils {

    /**
     * http request utility function.
     *
     * @param method
     * @param uri
     * @throws IOException
     */
    public final static HttpConnection httpRequest(final String method, final String uri) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Length", Integer.toString(0));
        conn.setDoInput(true);
        return new HttpConnection(conn, new byte[]{});
    }

    public final static HttpConnection httpPostFormRequest(final String uri, Map<String, Object> params) throws IOException {
        return sendFormData("POST", uri, params);
    }

    public static HttpConnection httpPutFormRequest(final String uri, Map<String, Object> params) throws IOException {
        return sendFormData("PUT", uri, params);
    }

    /**
     * Post form data : https://stackoverflow.com/a/4206094/2614364 / https://stackoverflow.com/a/21657510/2614364
     *
     * @param uri
     * @param params
     * @throws IOException
     */
    public static HttpConnection sendFormData(final String method, final String uri, Map<String, Object> params) throws IOException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataBytes.length));
        conn.setUseCaches(false);
        return new HttpConnection(conn, postDataBytes);
    }

    public final static void addCookies(HttpConnection conn, CookieManager manager) {
        String cookieVal = "";
        for (HttpCookie cookie : manager.getCookieStore().getCookies()) {
            cookieVal += cookie.toString().replace("\"", "") + ";";
        }
        conn.getConn().setRequestProperty("Cookie", cookieVal);
    }
}
