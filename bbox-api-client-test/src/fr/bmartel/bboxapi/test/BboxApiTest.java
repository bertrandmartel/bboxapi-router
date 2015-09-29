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
package fr.bmartel.bboxapi.test;

import java.util.List;

import fr.bmartel.bboxapi.BboxApi;
import fr.bmartel.bboxapi.listeners.IApiSummaryListener;
import fr.bmartel.bboxapi.listeners.IAuthenticationListener;
import fr.bmartel.bboxapi.listeners.IBboxDeviceListener;
import fr.bmartel.bboxapi.listeners.IFullCallLogListener;
import fr.bmartel.bboxapi.listeners.IHostsListener;
import fr.bmartel.bboxapi.listeners.IRequestStatusListener;
import fr.bmartel.bboxapi.listeners.IVoipDataListener;
import fr.bmartel.bboxapi.listeners.IWirelessListener;
import fr.bmartel.bboxapi.model.ApiSummary;
import fr.bmartel.bboxapi.model.BBoxDevice;
import fr.bmartel.bboxapi.model.Host;
import fr.bmartel.bboxapi.model.Voip;
import fr.bmartel.bboxapi.voip.CallLog;
import fr.bmartel.bboxapi.wireless.WirelessData;

/**
 * 
 * Testing class for bbox api client
 * 
 * @author Bertrand Martel
 *
 */
public class BboxApiTest {

	public static void main(String[] args) {

		/* instantiate bbox api object */
		final BboxApi api = new BboxApi();

		/* retrieve summary api result (summaru api is public) */
		api.getSummary(new IApiSummaryListener() {

			@Override
			public void onApiSummaryReceived(ApiSummary summary) {

				summary.displayInfo();

			}

			@Override
			public void onApiSummaryFailure() {

				System.out.println("summary api failure");

			}
		});

		/*
		 * authenticate and process further api testing if authentication is
		 * successfull
		 */
		api.authenticate("your_password", new IAuthenticationListener() {

			@Override
			public void onAuthenticationSuccess(String token) {

				System.out.println("authentication success. Received token : " + token);

				api.voipData(new IVoipDataListener() {

					@Override
					public void onVoipDataReceived(Voip voipData) {
						System.out.println("voip data received : ");
						voipData.displayInfo();
					}

					@Override
					public void onVoipDataFailure() {
						System.out.println("voip api failure");
					}

				});

				/*
				 * api.voipDial(1,"0666666666",new IRequestStatusListener() {
				 * 
				 * @Override public void onSuccess() {
				 * System.out.println("Voip dial success"); }
				 * 
				 * @Override public void onFailure() {
				 * System.out.println("Voip dial failure"); } });
				 */
				api.bboxDevice(new IBboxDeviceListener() {

					@Override
					public void onBboxDeviceReceived(BBoxDevice device) {

						device.displayInfo();
					}

					@Override
					public void onBboxDeviceFailure() {
						System.out.println("bbox device failure");
					}

				});

				api.setBboxDisplayState(true, new IRequestStatusListener() {

					@Override
					public void onSuccess() {
						System.out.println("box display set success");
					}

					@Override
					public void onFailure() {
						System.out.println("box display set failure");
					}
				});

				api.setWifiState(true, new IRequestStatusListener() {

					@Override
					public void onSuccess() {
						System.out.println("box wifi set status success");
					}

					@Override
					public void onFailure() {
						System.out.println("box wifi set status failure");
					}
				});

				api.getFullCallLog(new IFullCallLogListener() {

					@Override
					public void onFullCallLogFailure() {

						System.out.println("call log failure");
					}

					@Override
					public void onFullCallLogReceived(List<CallLog> callLogList) {

						for (int i = 0; i < callLogList.size(); i++) {
							System.out.println("Log list item " + i);
							callLogList.get(i).displayInfo();
						}
					}
				});

				api.getHosts(new IHostsListener() {

					@Override
					public void onHostsReceived(List<Host> hostList) {

						for (int i = 0; i < hostList.size(); i++) {
							System.out.println("Host list item " + i);
							hostList.get(i).displayInfo();
						}
					}

					@Override
					public void onHostsFailure() {

						System.out.println("host failure");

					}
				});

				api.getWirelessData(new IWirelessListener() {

					@Override
					public void onWirelessDataReceived(WirelessData wirelessData) {

						wirelessData.displayInfo();

					}

					@Override
					public void onWirelessDataFailure() {

						System.out.println("wireless data request failure");

					}
				});
			}

			@Override
			public void onAuthenticationError() {
				System.out.println("authentication error");
			}

		});

	}

}
