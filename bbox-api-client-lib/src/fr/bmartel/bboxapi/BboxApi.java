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
package fr.bmartel.bboxapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import fr.bmartel.bboxapi.listeners.IApiSummaryListener;
import fr.bmartel.bboxapi.listeners.IAuthenticationListener;
import fr.bmartel.bboxapi.listeners.IBboxDeviceListener;
import fr.bmartel.bboxapi.listeners.IFullCallLogListener;
import fr.bmartel.bboxapi.listeners.IHostsListener;
import fr.bmartel.bboxapi.listeners.ILogoutListener;
import fr.bmartel.bboxapi.listeners.IRequestStatusListener;
import fr.bmartel.bboxapi.listeners.IVoipDataListener;
import fr.bmartel.bboxapi.listeners.IWirelessListener;
import fr.bmartel.bboxapi.model.ApiSummary;
import fr.bmartel.bboxapi.model.BBoxDevice;
import fr.bmartel.bboxapi.model.Host;
import fr.bmartel.bboxapi.model.Voip;
import fr.bmartel.bboxapi.voip.CallLog;
import fr.bmartel.bboxapi.voip.CallState;
import fr.bmartel.bboxapi.voip.CallType;
import fr.bmartel.bboxapi.wireless.RadioObject;
import fr.bmartel.bboxapi.wireless.SsidObject;
import fr.bmartel.bboxapi.wireless.WirelessCapability;
import fr.bmartel.bboxapi.wireless.WirelessData;
import fr.bmartel.protocol.http.ClientSocket;
import fr.bmartel.protocol.http.HttpFrame;
import fr.bmartel.protocol.http.HttpVersion;
import fr.bmartel.protocol.http.IClientSocket;
import fr.bmartel.protocol.http.IHttpClientListener;
import fr.bmartel.protocol.http.constants.HttpMethod;
import fr.bmartel.protocol.http.states.HttpStates;
import fr.bmartel.protocol.http.utils.ListOfBytes;

/**
 * Bbox Api implementation
 * 
 * @author Bertrand Martel
 *
 */
public class BboxApi implements IBboxApi {

	private String token_header = "";

	private boolean authenticated = false;

	public BboxApi() {
	}

	/**
	 * Authenticate to Bbox Api
	 * 
	 * @param password
	 *            BBox management interface password
	 * @param authenticationListener
	 *            listener called when authentication result has been received
	 * @return true if authentication has been initiated successfully
	 */
	@Override
	public boolean authenticate(String password, final IAuthenticationListener authenticationListener) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						if (frame.getHeaders().containsKey("set-cookie")) {

							token_header = frame.getHeaders().get("set-cookie");

							String token = token_header.substring(8, token_header.indexOf(';'));

							authenticated = true;

							authenticationListener.onAuthenticationSuccess(token);
						} else {
							authenticationListener.onAuthenticationError();
						}
					} else {
						authenticationListener.onAuthenticationError();
					}
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
				
				authenticationListener.onAuthenticationError();
				
			}
		});

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		HttpFrame frameRequest = new HttpFrame(HttpMethod.POST_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/login", new ListOfBytes("password=" + password
				+ "&remember=1"));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}

	/**
	 * Voip data
	 * 
	 * @param voipDataListener
	 *            listener called when voip data result has been received
	 * @return true if request has been successfully initiated
	 */
	@Override
	public boolean voipData(final IVoipDataListener voipDataListener) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						String data = new String(frame.getBody().getBytes());

						JSONArray obj = (JSONArray) JSONValue.parse(data);

						if (obj.size() > 0) {

							JSONObject item = (JSONObject) obj.get(0);
							if (item.containsKey("voip")) {

								JSONArray sub_item = (JSONArray) item.get("voip");
								if (sub_item.size() > 0) {

									JSONObject sub_item_first_element = (JSONObject) sub_item.get(0);

									if (sub_item_first_element.containsKey("id") && sub_item_first_element.containsKey("status")
											&& sub_item_first_element.containsKey("callstate") && sub_item_first_element.containsKey("uri")
											&& sub_item_first_element.containsKey("blockstate") && sub_item_first_element.containsKey("anoncallstate")
											&& sub_item_first_element.containsKey("mwi") && sub_item_first_element.containsKey("message_count")
											&& sub_item_first_element.containsKey("notanswered")) {

										int id = Integer.parseInt(sub_item_first_element.get("id").toString());
										String status = sub_item_first_element.get("status").toString();
										String callState = sub_item_first_element.get("callstate").toString();
										String uri = sub_item_first_element.get("uri").toString();
										int blockState = Integer.parseInt(sub_item_first_element.get("blockstate").toString());
										int anoncallState = Integer.parseInt(sub_item_first_element.get("anoncallstate").toString());
										int mwi = Integer.parseInt(sub_item_first_element.get("mwi").toString());
										int messageCount = Integer.parseInt(sub_item_first_element.get("message_count").toString());
										int notanswered = Integer.parseInt(sub_item_first_element.get("notanswered").toString());

										Voip voip = new Voip(id, status, CallState.getValue(callState), uri, blockState, anoncallState, mwi, messageCount,
												notanswered);

										voipDataListener.onVoipDataReceived(voip);
										clientSocket.closeSocket();
										return;
									}
								}
							}
						}
					}
					voipDataListener.onVoipDataFailure();
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
				
				voipDataListener.onVoipDataFailure();
			}
		});

		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		headers.put("Cookie", token_header);
		HttpFrame frameRequest = new HttpFrame(HttpMethod.GET_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/voip", new ListOfBytes(""));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}

	/**
	 * Voip dial a phone number
	 * 
	 * @param requestStatus
	 *            listener for status of request
	 * @return true if request has been successfully initiated
	 */
	@Override
	public boolean voipDial(int lineNumber, String phoneNumber, final IRequestStatusListener requestStatus) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						requestStatus.onSuccess();
					} else {
						requestStatus.onFailure();
					}
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
			
				requestStatus.onFailure();
				
			}
		});

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		headers.put("Cookie", token_header);
		HttpFrame frameRequest = new HttpFrame(HttpMethod.PUT_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/voip/dial", new ListOfBytes("line="
				+ lineNumber + "&number=" + phoneNumber));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}

	/**
	 * Bbox device api
	 * 
	 * @param deviceListener
	 *            bbox device listener
	 * @return true if request has been successfully initiated
	 */
	@Override
	public boolean bboxDevice(final IBboxDeviceListener deviceListener) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						String data = new String(frame.getBody().getBytes());

						JSONArray obj = (JSONArray) JSONValue.parse(data);

						if (obj.size() > 0) {

							JSONObject item = (JSONObject) obj.get(0);
							if (item.containsKey("device")) {

								JSONObject sub_item = (JSONObject) item.get("device");

								if (sub_item.containsKey("now") && sub_item.containsKey("status") && sub_item.containsKey("numberofboots")
										&& sub_item.containsKey("modelname") && sub_item.containsKey("user_configured") && sub_item.containsKey("display")
										&& sub_item.containsKey("firstusedate") && sub_item.containsKey("uptime")) {

									// String now =
									// sub_item.get("now").toString();
									int status = Integer.parseInt(sub_item.get("status").toString());
									int bootNumber = Integer.parseInt(sub_item.get("numberofboots").toString());
									String modelname = sub_item.get("modelname").toString();

									int user_configured = Integer.parseInt(sub_item.get("user_configured").toString());
									boolean userConfig = (user_configured == 0) ? false : true;

									JSONObject display = (JSONObject) sub_item.get("display");
									int displayLuminosity = Integer.parseInt(display.get("luminosity").toString());
									boolean displayState = (displayLuminosity == 100) ? true : false;

									String firstusedate = sub_item.get("firstusedate").toString();

									BBoxDevice device = new BBoxDevice(status, bootNumber, modelname, userConfig, displayState, firstusedate);

									if (sub_item.containsKey("serialnumber")) {
										device.setSerialNumber(sub_item.get("serialnumber").toString());
									}

									deviceListener.onBboxDeviceReceived(device);
									clientSocket.closeSocket();
									return;
								}
							}
						}
					}
					deviceListener.onBboxDeviceFailure();
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
				
				deviceListener.onBboxDeviceFailure();
				
			}
		});

		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		headers.put("Cookie", token_header);
		HttpFrame frameRequest = new HttpFrame(HttpMethod.GET_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/device", new ListOfBytes(""));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}

	/**
	 * Set Bbox display state (luminosity of Bbox device)
	 * 
	 * @param state
	 *            ON/OFF
	 * @param requestStatus
	 *            listener for request status
	 * @return true if request has been successfully initiated
	 */
	@Override
	public boolean setBboxDisplayState(boolean state, final IRequestStatusListener requestStatus) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						requestStatus.onSuccess();
					} else {
						requestStatus.onFailure();
					}
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
				
				requestStatus.onFailure();
				
			}
		});

		byte luminosity = 0;

		if (state)
			luminosity = 100;

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		headers.put("Cookie", token_header);
		HttpFrame frameRequest = new HttpFrame(HttpMethod.PUT_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/device/display", new ListOfBytes("luminosity="
				+ luminosity));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}

	/**
	 * 
	 * Set Wifi state
	 * 
	 * @param state
	 *            wifi state ON/OFF
	 * @param requestStatus
	 *            listener for request status
	 * @return true if request has been successfully initiated
	 */
	@Override
	public boolean setWifiState(boolean state, final IRequestStatusListener requestStatus) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						requestStatus.onSuccess();
					} else {
						requestStatus.onFailure();
					}
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
				
				requestStatus.onFailure();
				
			}
		});

		int stateInt = 0;

		if (state)
			stateInt = 1;

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		headers.put("Cookie", token_header);
		HttpFrame frameRequest = new HttpFrame(HttpMethod.PUT_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/wireless", new ListOfBytes("radio.enable="
				+ stateInt));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}

	/**
	 * 
	 * Retrieve full call log
	 * 
	 * @param listener
	 *            listener for request result / failure
	 * 
	 * @return true if request has been successfully initiated
	 */
	@Override
	public boolean getFullCallLog(final IFullCallLogListener listener) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						String data = new String(frame.getBody().getBytes());

						JSONArray obj = (JSONArray) JSONValue.parse(data);

						if (obj.size() > 0) {

							JSONObject item = (JSONObject) obj.get(0);
							if (item.containsKey("calllog")) {

								JSONArray sub_item = (JSONArray) item.get("calllog");

								List<CallLog> logList = new ArrayList<CallLog>();

								for (int i = 0; i < sub_item.size(); i++) {

									JSONObject callItem = (JSONObject) sub_item.get(i);

									if (callItem.containsKey("id") && callItem.containsKey("number") && callItem.containsKey("date")
											&& callItem.containsKey("type") && callItem.containsKey("answered") && callItem.containsKey("duree")) {

										int id = Integer.parseInt(callItem.get("id").toString());
										String number = callItem.get("number").toString();
										long date = Long.parseLong(callItem.get("date").toString());
										String type = callItem.get("type").toString();
										int answered = Integer.parseInt(callItem.get("answered").toString());
										int duree = Integer.parseInt(callItem.get("duree").toString());
										boolean isAnswered = false;

										if (answered == 1)
											isAnswered = true;

										logList.add(new CallLog(id, number, date, CallType.getValue(type), isAnswered, duree));
									}
								}

								listener.onFullCallLogReceived(logList);
								clientSocket.closeSocket();

								return;
							}
						}

					}
					listener.onFullCallLogFailure();
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
				
				listener.onFullCallLogFailure();
				
			}
		});

		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		headers.put("Cookie", token_header);
		HttpFrame frameRequest = new HttpFrame(HttpMethod.GET_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/voip/fullcalllog/1", new ListOfBytes(""));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}

	/**
	 * 
	 * Retrieve all hosts
	 * 
	 * @param listener
	 *            listener for request result / failure
	 * 
	 * @return true if request has been successfully initiated
	 */
	@Override
	public boolean getHosts(final IHostsListener listener) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						String data = new String(frame.getBody().getBytes());

						JSONArray obj = (JSONArray) JSONValue.parse(data);

						if (obj.size() > 0) {

							JSONObject item = (JSONObject) obj.get(0);
							if (item.containsKey("hosts")) {

								JSONObject sub_item = (JSONObject) item.get("hosts");

								if (sub_item.containsKey("list")) {

									JSONArray hostList = (JSONArray) sub_item.get("list");

									List<Host> hostObjectList = new ArrayList<Host>();

									for (int i = 0; i < hostList.size(); i++) {

										JSONObject hostEntry = (JSONObject) hostList.get(i);

										if (hostEntry.containsKey("id") && hostEntry.containsKey("hostname") && hostEntry.containsKey("macaddress")
												&& hostEntry.containsKey("ipaddress") && hostEntry.containsKey("type") && hostEntry.containsKey("link")
												&& hostEntry.containsKey("devicetype") && hostEntry.containsKey("firstseen")
												&& hostEntry.containsKey("lastseen") && hostEntry.containsKey("lease") && hostEntry.containsKey("active")) {

											int id = Integer.parseInt(hostEntry.get("id").toString());
											String hostname = hostEntry.get("hostname").toString();
											String macaddress = hostEntry.get("macaddress").toString();
											String ipaddress = hostEntry.get("ipaddress").toString();
											String type = hostEntry.get("type").toString();
											String link = hostEntry.get("link").toString();
											String devicetype = hostEntry.get("devicetype").toString();
											String firstseen = hostEntry.get("firstseen").toString();
											String lastseen = hostEntry.get("lastseen").toString();
											int lease = Integer.parseInt(hostEntry.get("lease").toString());

											boolean active = (Integer.parseInt(hostEntry.get("active").toString()) == 0) ? false : true;

											hostObjectList.add(new Host(id, hostname, macaddress, ipaddress, type, link, devicetype, firstseen, lastseen,
													lease, active));
										}
									}
									listener.onHostsReceived(hostObjectList);
									clientSocket.closeSocket();
									return;
								}
							}
						}

					}
					listener.onHostsFailure();
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
				
				listener.onHostsFailure();
				
			}
		});

		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		headers.put("Cookie", token_header);
		HttpFrame frameRequest = new HttpFrame(HttpMethod.GET_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/hosts", new ListOfBytes(""));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}

	/**
	 * 
	 * Retrieve summary api result
	 * 
	 * @param listener
	 *            listener for request result / failure
	 * 
	 * @return true if request has been successfully initiated
	 */
	@Override
	public boolean getSummary(final IApiSummaryListener listener) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						String data = new String(frame.getBody().getBytes());

						JSONArray obj = (JSONArray) JSONValue.parse(data);

						if (obj.size() > 0) {

							JSONObject item = (JSONObject) obj.get(0);

							if (item.containsKey("authenticated") && item.containsKey("display") && item.containsKey("internet") && item.containsKey("voip")
									&& item.containsKey("iptv") && item.containsKey("hosts") && item.containsKey("wan")) {

								int authenticated = Integer.parseInt(item.get("authenticated").toString());
								boolean displayState = false;
								int luminosity = 0;

								JSONObject displayObj = (JSONObject) item.get("display");

								if (displayObj.containsKey("state") && displayObj.containsKey("luminosity")) {

									displayState = true;
									luminosity = Integer.parseInt(displayObj.get("luminosity").toString());
									if (luminosity == 0)
										displayState = false;
								}

								int internetState = 0;

								JSONObject internetObj = (JSONObject) item.get("internet");

								if (internetObj.containsKey("state")) {

									internetState = Integer.parseInt(internetObj.get("state").toString());
								}

								JSONArray voipArr = (JSONArray) item.get("voip");

								String voipStatus = "";
								CallState callState = CallState.UNKNOWN;
								int message = 0;
								int notanswered = 0;

								if (voipArr.size() > 0) {

									JSONObject voipObject = (JSONObject) voipArr.get(0);

									if (voipObject.containsKey("status") && voipObject.containsKey("callstate") && voipObject.containsKey("message")
											&& voipObject.containsKey("notanswered")) {

										voipStatus = voipObject.get("status").toString();
										callState = CallState.getValue(voipObject.get("callstate").toString());
										message = Integer.parseInt(voipObject.get("message").toString());
										notanswered = Integer.parseInt(voipObject.get("notanswered").toString());
									}
								}

								JSONArray iptvArr = (JSONArray) item.get("iptv");

								String iptvAddr = "";
								String iptvIpAddr = "";
								int iptvReceipt = 0;
								int iptvNumber = 0;

								if (iptvArr.size() > 0) {

									JSONObject iptvObject = (JSONObject) iptvArr.get(0);

									if (iptvObject.containsKey("address") && iptvObject.containsKey("ipaddress") && iptvObject.containsKey("receipt")
											&& iptvObject.containsKey("number")) {

										iptvAddr = iptvObject.get("address").toString();
										iptvIpAddr = iptvObject.get("ipaddress").toString();
										iptvReceipt = Integer.parseInt(iptvObject.get("receipt").toString());
										iptvNumber = Integer.parseInt(iptvObject.get("number").toString());
									}
								}

								JSONArray hostArr = (JSONArray) item.get("hosts");

								List<Host> hostList = new ArrayList<Host>();

								for (int i = 0; i < hostArr.size(); i++) {

									JSONObject hostItem = (JSONObject) hostArr.get(i);
									if (hostItem.containsKey("hostname") && hostItem.containsKey("ipaddress")) {

										hostList.add(new Host(0, hostItem.get("hostname").toString(), "", hostItem.get("ipaddress").toString(), "", "", "", "",
												"", 0, false));
									}
								}

								JSONObject wanObject = (JSONObject) item.get("wan");

								int rxOccupation = 0;
								int txOccupation = 0;

								if (wanObject.containsKey("ip")) {

									JSONObject ipObject = (JSONObject) wanObject.get("ip");

									if (ipObject.containsKey("stats")) {

										JSONObject statsObject = (JSONObject) ipObject.get("stats");

										if (statsObject.containsKey("rx") && statsObject.containsKey("tx")) {

											JSONObject rxObject = (JSONObject) statsObject.get("rx");
											JSONObject txObject = (JSONObject) statsObject.get("tx");

											if (rxObject.containsKey("occupation") && txObject.containsKey("occupation")) {

												rxOccupation = Integer.parseInt(rxObject.get("occupation").toString());
												txOccupation = Integer.parseInt(txObject.get("occupation").toString());

											}
										}
									}
								}

								ApiSummary summary = new ApiSummary(rxOccupation, txOccupation, hostList, iptvAddr, iptvIpAddr, iptvReceipt, iptvNumber,
										voipStatus, callState, message, notanswered, internetState, authenticated, displayState);

								listener.onApiSummaryReceived(summary);
								clientSocket.closeSocket();
								return;
							}
						}
					}
					listener.onApiSummaryFailure();
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
				
				listener.onApiSummaryFailure();
				
			}
		});

		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		headers.put("Cookie", token_header);
		HttpFrame frameRequest = new HttpFrame(HttpMethod.GET_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/summary", new ListOfBytes(""));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * Logout
	 * 
	 * @param logoutListener
	 *            listener called when logout result has been received
	 * @return true if logout has been initiated successfully
	 */
	@Override
	public boolean logout(final ILogoutListener logoutListener) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						token_header = "";
						authenticated = false;
						logoutListener.onLogoutSuccess();

					} else {
						logoutListener.onLogoutError();
					}
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
				
				logoutListener.onLogoutError();
				
			}
		});

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		HttpFrame frameRequest = new HttpFrame(HttpMethod.POST_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/logout", new ListOfBytes(""));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}

	@Override
	public boolean getWirelessData(final IWirelessListener wirelessListener) {

		ClientSocket clientSocket = new ClientSocket("gestionbbox.lan", 80);

		clientSocket.addClientSocketEventListener(new IHttpClientListener() {

			@Override
			public void onIncomingHttpFrame(HttpFrame frame, HttpStates httpStates, IClientSocket clientSocket) {

				if (httpStates == HttpStates.HTTP_FRAME_OK && frame.isHttpResponseFrame()) {

					// this is data coming from the server
					if (frame.getStatusCode() == 200) {

						String data = new String(frame.getBody().getBytes());

						JSONArray obj = (JSONArray) JSONValue.parse(data);

						if (obj.size() > 0) {

							JSONObject item = (JSONObject) obj.get(0);
							if (item.containsKey("wireless")) {

								JSONObject sub_item_first_element = (JSONObject) item.get("wireless");

								if (sub_item_first_element.containsKey("status") && sub_item_first_element.containsKey("radio")
										&& sub_item_first_element.containsKey("ssid") && sub_item_first_element.containsKey("capabilities")
										&& sub_item_first_element.containsKey("standard")) {

									String status = sub_item_first_element.get("status").toString();

									JSONObject radioItem = (JSONObject) sub_item_first_element.get("radio");

									Iterator it = radioItem.entrySet().iterator();

									HashMap<Integer, RadioObject> radioList = new HashMap<Integer, RadioObject>();
									HashMap<Integer, SsidObject> ssidList = new HashMap<Integer, SsidObject>();

									while (it.hasNext()) {

										Map.Entry<String, Object> pair = (Map.Entry) it.next();

										JSONObject subItem = (JSONObject) pair.getValue();

										boolean enable = false;
										String standard = "";
										int state = 0;
										int channel = 0;
										int currentChannel = 0;
										boolean dfs = false;
										boolean ht40 = false;

										if (subItem.containsKey("enable"))
											enable = Integer.parseInt(subItem.get("enable").toString()) == 1 ? true : false;
										if (subItem.containsKey("standard"))
											standard = subItem.get("standard").toString();
										if (subItem.containsKey("state"))
											state = Integer.parseInt(subItem.get("state").toString());
										if (subItem.containsKey("channel"))
											channel = Integer.parseInt(subItem.get("channel").toString());
										if (subItem.containsKey("current_channel"))
											currentChannel = Integer.parseInt(subItem.get("current_channel").toString());
										if (subItem.containsKey("dfs"))
											dfs = Integer.parseInt(subItem.get("dfs").toString()) == 1 ? true : false;
										if (subItem.containsKey("ht40")) {
											JSONObject ht40Object = (JSONObject) subItem.get("ht40");
											if (ht40Object.containsKey("enable"))
												ht40 = Integer.parseInt(subItem.get("enable").toString()) == 1 ? true : false;
										}

										radioList.put(Integer.parseInt(pair.getKey()), new RadioObject(enable, standard, state, channel, currentChannel, dfs,
												ht40));
									}

									JSONObject ssidItem = (JSONObject) sub_item_first_element.get("ssid");

									Iterator it2 = ssidItem.entrySet().iterator();

									while (it2.hasNext()) {

										Map.Entry<String, Object> pair = (Map.Entry) it2.next();

										JSONObject subItem = (JSONObject) pair.getValue();

										String id = "";
										boolean enabled = false;
										boolean hidden = false;
										String bssid = "";
										boolean wmmenable = false;
										boolean wpsenabled = false;
										String wpsstatus = "";
										boolean securityDefault = false;
										String securityProtocol = "";
										String securityEncryption = "";
										String securityPassphrase = "";

										if (subItem.containsKey("id"))
											id = subItem.get("id").toString();
										if (subItem.containsKey("enable"))
											enabled = Integer.parseInt(subItem.get("enable").toString()) == 1 ? true : false;
										if (subItem.containsKey("hidden"))
											hidden = Integer.parseInt(subItem.get("hidden").toString()) == 1 ? true : false;
										if (subItem.containsKey("bssid"))
											bssid = subItem.get("bssid").toString();
										if (subItem.containsKey("wmmenable"))
											wmmenable = Integer.parseInt(subItem.get("wmmenable").toString()) == 1 ? true : false;
										if (subItem.containsKey("wps")) {

											JSONObject wpsObject = (JSONObject) subItem.get("wps");

											if (wpsObject.containsKey("enable"))
												wpsenabled = Integer.parseInt(wpsObject.get("enable").toString()) == 1 ? true : false;
											if (wpsObject.containsKey("status"))
												wpsstatus = wpsObject.get("status").toString();
										}
										if (subItem.containsKey("security")) {

											JSONObject securityObject = (JSONObject) subItem.get("security");

											if (securityObject.containsKey("isdefault"))
												securityDefault = Integer.parseInt(securityObject.get("isdefault").toString()) == 1 ? true : false;
											if (securityObject.containsKey("protocol"))
												securityProtocol = securityObject.get("protocol").toString();
											if (securityObject.containsKey("encryption"))
												securityEncryption = securityObject.get("encryption").toString();
											if (securityObject.containsKey("passphrase"))
												securityPassphrase = securityObject.get("passphrase").toString();

										}

										ssidList.put(Integer.parseInt(pair.getKey()), new SsidObject(id, enabled, hidden, bssid, wmmenable, wpsenabled,
												wpsstatus, securityDefault, securityProtocol, securityEncryption, securityPassphrase));
									}

									JSONObject capabilityItems = (JSONObject) sub_item_first_element.get("capabilities");

									Iterator it3 = capabilityItems.entrySet().iterator();

									HashMap<Integer, List<WirelessCapability>> capabilityRadioList = new HashMap<Integer, List<WirelessCapability>>();

									while (it3.hasNext()) {

										Map.Entry<String, Object> pair = (Map.Entry) it3.next();

										JSONArray subItem = (JSONArray) pair.getValue();

										List<WirelessCapability> wirelessCapabilityList = new ArrayList<WirelessCapability>();

										for (int i = 0; i < subItem.size(); i++) {

											JSONObject capabilityObj = (JSONObject) subItem.get(i);

											int channel = 0;
											String ht40 = "";
											boolean nodfs = false;
											int cactime = 0;
											int cactime40 = 0;

											if (capabilityObj.containsKey("channel"))
												channel = Integer.parseInt(capabilityObj.get("channel").toString());
											if (capabilityObj.containsKey("ht40"))
												ht40 = capabilityObj.get("ht40").toString();
											if (capabilityObj.containsKey("nodfs"))
												nodfs = Boolean.valueOf(capabilityObj.get("nodfs").toString());
											if (capabilityObj.containsKey("cactime"))
												cactime = Integer.parseInt(capabilityObj.get("cactime").toString());
											if (capabilityObj.containsKey("cactime40"))
												cactime40 = Integer.parseInt(capabilityObj.get("cactime40").toString());

											wirelessCapabilityList.add(new WirelessCapability(channel, ht40, nodfs, cactime, cactime40));
										}

										capabilityRadioList.put(Integer.parseInt(pair.getKey()), wirelessCapabilityList);

									}

									JSONObject standardItems = (JSONObject) sub_item_first_element.get("standard");

									Iterator it4 = standardItems.entrySet().iterator();

									HashMap<Integer, List<String>> standardTypeList = new HashMap<Integer, List<String>>();

									while (it4.hasNext()) {

										Map.Entry<String, Object> pair = (Map.Entry) it4.next();

										JSONArray subItem = (JSONArray) pair.getValue();

										List<String> typeList = new ArrayList<String>();

										for (int i = 0; i < subItem.size(); i++) {

											JSONObject typeOject = (JSONObject) subItem.get(i);

											if (typeOject.containsKey("value"))
												typeList.add(typeOject.get("value").toString());
										}

										standardTypeList.put(Integer.parseInt(pair.getKey()), typeList);
									}

									WirelessData wirelessData = new WirelessData(status, radioList, ssidList, capabilityRadioList, standardTypeList);

									wirelessListener.onWirelessDataReceived(wirelessData);
									clientSocket.closeSocket();
									return;
								}

							}
						}
					}
					wirelessListener.onWirelessDataFailure();
					clientSocket.closeSocket();
				}
			}

			@Override
			public void onSocketError() {
				
				wirelessListener.onWirelessDataFailure();
			}
		});

		HashMap<String, String> headers = new HashMap<String, String>();

		headers.put("Accept", "*/*");
		headers.put("Host", "gestionbbox.lan");
		headers.put("Cookie", token_header);
		HttpFrame frameRequest = new HttpFrame(HttpMethod.GET_REQUEST, new HttpVersion(1, 1), headers, "/api/v1/wireless", new ListOfBytes(""));

		clientSocket.write(frameRequest.toString().getBytes());

		return false;
	}
}
