/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Bertrand Martel
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import fr.bmartel.protocol.http.ClientSocket;
import fr.bmartel.protocol.http.HttpFrame;
import fr.bmartel.protocol.http.HttpVersion;
import fr.bmartel.protocol.http.IClientSocket;
import fr.bmartel.protocol.http.IHttpClientListener;
import fr.bmartel.protocol.http.inter.IHttpFrame;
import fr.bmartel.protocol.http.states.HttpStates;
import fr.bmartel.protocol.http.utils.ListOfBytes;

/**
 * 
 * Some test case for writing to external host such as google.com (https and
 * http)
 * 
 * @author Bertrand Martel
 *
 */
public class ExternalHostTest {

	public static void main(String[] args) {
		
		System.out.println("Test with : http://www.google.fr");

		ClientSocket clientSocket = new ClientSocket("www.google.fr", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame,
					HttpStates httpStates, IClientSocket clientSocket) {

				printHttpFrameDecodedResult(frame, httpStates);

			}
		});

		HashMap<String, String> headers2  = new HashMap<String, String>();
		headers2.put("Host", "www.google.fr");
		HttpFrame frame2 = new HttpFrame("GET", new HttpVersion(1, 1),
				headers2, "http://www.google.fr/", new ListOfBytes(""));

		clientSocket.write(frame2.toString().getBytes());

		System.out.println("Test with : https://www.google.fr");
		
		clientSocket = new ClientSocket("www.google.fr", 443);

		// set SSL encryption
		clientSocket.setSsl(true);

		// set ssl parameters

		clientSocket
				.setSSLParams(
						"JKS",
						"JKS",
						"",
						"/home/abathur/Bureau/open_source/google-oauth-console-authentication/just2try.keystore",
						"TLS", "", "123456");

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame,
					HttpStates httpStates, IClientSocket clientSocket) {

				printHttpFrameDecodedResult(frame, httpStates);

			}
		});

		headers2 = new HashMap<String, String>();
		headers2.put("Host", "www.google.fr");
		 frame2 = new HttpFrame("GET", new HttpVersion(1, 1), headers2,
				"https://www.google.fr/", new ListOfBytes(""));

		clientSocket.write(frame2.toString().getBytes());
	}

	/**
	 * Print result of http decoding
	 * 
	 * @param frame
	 *            http frame object
	 * @param decodingStates
	 *            final states of http decoding (to catch http decoding error)
	 */
	public static void printHttpFrameDecodedResult(IHttpFrame frame,
			HttpStates decodingStates) {
		if (frame.isHttpRequestFrame()) {
			System.out.println("uri         : " + frame.getUri());
			System.out.println("version     : " + frame.getHttpVersion());
			System.out.println("method      : " + frame.getMethod());
			System.out.println("querystring : " + frame.getQueryString());
			System.out.println("host        : " + frame.getHost());
			System.out.println("chunked     : " + frame.isChunked());
			System.out.println("body        : "
					+ new String(frame.getBody().getBytes()));

			Set<String> keys = frame.getHeaders().keySet();
			Iterator<String> it = keys.iterator();
			int count = 0;
			while (it.hasNext()) {
				Object key = it.next();
				Object value = frame.getHeaders().get(key);
				System.out.println("headers n ° " + count + " : "
						+ key.toString() + " => " + value.toString());
			}
		} else if (frame.isHttpResponseFrame()) {
			System.out
					.println("status code         : " + frame.getStatusCode());
			System.out.println("reason phrase       : "
					+ frame.getReasonPhrase());
			System.out.println("chunked             : " + frame.isChunked());
			System.out.println("body                : "
					+ new String(frame.getBody().getBytes()));

			Set<String> keys = frame.getHeaders().keySet();
			Iterator<String> it = keys.iterator();
			int count = 0;
			while (it.hasNext()) {
				Object key = it.next();
				Object value = frame.getHeaders().get(key);
				System.out.println("headers n ° " + count + " : "
						+ key.toString() + " => " + value.toString());
			}
		} else {
			System.out
					.println("Error, this http frame has not beed decoded correctly. Error code : "
							+ decodingStates.toString());
		}
		System.out.println("##########################################");
	}

}
