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

import fr.bmartel.bboxapi.listeners.IApiSummaryListener;
import fr.bmartel.bboxapi.listeners.IAuthenticationListener;
import fr.bmartel.bboxapi.listeners.IBboxDeviceListener;
import fr.bmartel.bboxapi.listeners.IFullCallLogListener;
import fr.bmartel.bboxapi.listeners.IHostsListener;
import fr.bmartel.bboxapi.listeners.IRequestStatusListener;
import fr.bmartel.bboxapi.listeners.IVoipDataListener;

/**
 * Set of Bbox api retrieved from Bbox management interface
 * 
 * @author Bertrand Martel
 *
 */
public interface IBboxApi {

	/**
	 * Authenticate to Bbox Api
	 * 
	 * @param password
	 *            BBox management interface password
	 * @param authenticationListener
	 *            listener called when authentication result has been received
	 * @return true if authentication has been initiated successfully
	 */
	public boolean authenticate(String password, IAuthenticationListener authenticationListener);

	/**
	 * Voip data
	 * 
	 * @param voipDataListener
	 *            listener called when voip data result has been received
	 * @return true if request has been successfully initiated
	 */
	public boolean voipData(IVoipDataListener voipDataListener);

	/**
	 * Voip dial a phone number
	 * 
	 * @param lineNumber
	 * @param phone
	 * @param requestStatus
	 *            listener for status of request
	 * @return true if request has been successfully initiated
	 */
	public boolean voipDial(int lineNumber, String phone, IRequestStatusListener requestStatus);

	/**
	 * Bbox device api
	 * 
	 * @param deviceListener
	 *            bbox device listener
	 * @return true if request has been successfully initiated
	 */
	public boolean bboxDevice(IBboxDeviceListener deviceListener);

	/**
	 * Set Bbox display state (luminosity of Bbox device)
	 * 
	 * @param state
	 *            ON/OFF
	 * @param requestStatus
	 *            listener for request status
	 * @return true if request has been successfully initiated
	 */
	public boolean setBboxDisplayState(boolean state, IRequestStatusListener requestStatus);

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
	public boolean setWifiState(boolean state, IRequestStatusListener requestStatus);

	/**
	 * 
	 * Retrieve full call log
	 * 
	 * @param listener
	 *            listener for request result / failure
	 * 
	 * @return true if request has been successfully initiated
	 */
	public boolean getFullCallLog(final IFullCallLogListener listener);

	/**
	 * 
	 * Retrieve all hosts
	 * 
	 * @param listener
	 *            listener for request result / failure
	 * 
	 * @return true if request has been successfully initiated
	 */
	public boolean getHosts(final IHostsListener listener);

	/**
	 * 
	 * Retrieve summary api result
	 * 
	 * @param listener
	 *            listener for request result / failure
	 * 
	 * @return true if request has been successfully initiated
	 */
	public boolean getSummary(final IApiSummaryListener listener);
}
