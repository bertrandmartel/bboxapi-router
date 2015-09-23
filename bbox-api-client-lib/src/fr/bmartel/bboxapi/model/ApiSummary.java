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
package fr.bmartel.bboxapi.model;

import java.util.ArrayList;
import java.util.List;

import fr.bmartel.bboxapi.voip.CallState;

/**
 * Summary api object
 * 
 * @author Bertrand Martel
 *
 */
public class ApiSummary {
	
	/**
	 * RX occupation
	 */
	private int rxOccupation =0;

	/**
	 * TX occupation
	 */
	private int txOccupation = 0;

	/**
	 * list of hosts
	 */
	private List<Host> hostList = new ArrayList<Host>();

	/**
	 * iptv broadcast address
	 */
	private String iptvAddr = "";

	/**
	 * iptv receiver address
	 */
	private String iptvIpAddr ="";

	private int iptvReceipt = 0;

	private int iptvNumber = 0;

	/**
	 * status of voip (Up if fine)
	 */
	private String voipStatus = "";

	/**
	 * call state
	 */
	private CallState callState =CallState.UNKNOWN;

	/**
	 * number of message
	 */
	private int message = 0;

	/**
	 * number of call not answered
	 */
	private int notanswered = 0;

	/**
	 * Internet state
	 */
	private int internetState = 0;

	/**
	 * authenticated
	 */
	private int authenticated = 0;

	/**
	 * display state "." for the moment ?
	 */
	private boolean displayState = false;

	/**
	 * Build Api summary object
	 * 
	 * @param rxOccupation
	 * @param txOccupation
	 * @param hostList
	 * @param iptvAddr
	 * @param iptvIpAddr
	 * @param iptvReceipt
	 * @param iptvNumber
	 * @param voipStatus
	 * @param callState
	 * @param message
	 * @param notanswered
	 * @param internetState
	 * @param authenticated
	 * @param displayState
	 * @param luminosity
	 */
	public ApiSummary(int rxOccupation,
			int txOccupation,
			List<Host> hostList,
			String iptvAddr,
			String iptvIpAddr,
			int iptvReceipt,
			int iptvNumber,
			String voipStatus,
			CallState callState,
			int message,
			int notanswered,
			int internetState,
			int authenticated,
			boolean displayState){
		
		this.rxOccupation=rxOccupation;
		this.txOccupation=txOccupation;
		this.hostList=hostList;
		this.iptvAddr=iptvAddr;
		this.iptvIpAddr=iptvIpAddr;
		this.iptvReceipt=iptvReceipt;
		this.iptvNumber=iptvNumber;
		this.voipStatus=voipStatus;
		this.callState=callState;
		this.message=message;
		this.notanswered=notanswered;
		this.internetState=internetState;
		this.authenticated=authenticated;
		this.displayState=displayState;
	}

	public int getRxOccupation() {
		return rxOccupation;
	}
	
	public int getTxOccupation() {
		return txOccupation;
	}
	
	public List<Host> getHostList() {
		return hostList;
	}

	public String getIptvAddr() {
		return iptvAddr;
	}

	public String getIptvIpAddr() {
		return iptvIpAddr;
	}

	public int getIptvReceipt() {
		return iptvReceipt;
	}


	public int getIptvNumber() {
		return iptvNumber;
	}

	public String getVoipStatus() {
		return voipStatus;
	}

	public CallState getCallState() {
		return callState;
	}

	public int getMessage() {
		return message;
	}

	public int getNotanswered() {
		return notanswered;
	}

	public int getInternetState() {
		return internetState;
	}


	public int getAuthenticated() {
		return authenticated;
	}

	public boolean getDisplayState() {
		return displayState;
	}

	public void displayInfo(){
		System.out.println("[SUMMARY] rxOccupation  : " + rxOccupation);
		System.out.println("[SUMMARY] txOccupation  : " + txOccupation);
		System.out.println("[SUMMARY] iptvAddr      : " + iptvAddr);
		System.out.println("[SUMMARY] iptvIpAddr    : " + iptvIpAddr);
		System.out.println("[SUMMARY] iptvReceipt   : " + iptvReceipt);
		System.out.println("[SUMMARY] iptvNumber    : " + iptvNumber);
		System.out.println("[SUMMARY] voipStatus    : " + voipStatus);
		System.out.println("[SUMMARY] callState     : " + callState);
		System.out.println("[SUMMARY] message       : " + message);
		System.out.println("[SUMMARY] notanswered   : " + notanswered);
		System.out.println("[SUMMARY] internetState : " + internetState);
		System.out.println("[SUMMARY] authenticated : " + authenticated);
		System.out.println("[SUMMARY] displayState  : " + displayState);
		System.out.println("[SUMMARY] hosts    : ");
		
		for (int i = 0; i  < hostList.size();i++){
			hostList.get(i).displaySummaryInfo();
		}
	}
	
}
