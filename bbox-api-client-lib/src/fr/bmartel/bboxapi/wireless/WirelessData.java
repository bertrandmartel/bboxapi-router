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
package fr.bmartel.bboxapi.wireless;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Wireless data template
 * 
 * @author Bertrand Martel
 *
 */
public class WirelessData {

	/**
	 * wireless status (loaded ?)
	 */
	private String status = "";

	/**
	 * list of radio characteristic for each wireless channel used
	 */
	private HashMap<Integer, RadioObject> radioList = new HashMap<Integer, RadioObject>();

	/**
	 * list of ssid characteristic for each wireless channel used
	 */
	private HashMap<Integer, SsidObject> ssidList = new HashMap<Integer, SsidObject>();

	/**
	 * list of capabilities available for each wireless channel used
	 */
	private HashMap<Integer, List<WirelessCapability>> capabilityRadioList = new HashMap<Integer, List<WirelessCapability>>();

	/**
	 * type of each wireless channel used (bgnac...)
	 */
	private HashMap<Integer, List<String>> standardTypeList = new HashMap<Integer, List<String>>();

	public WirelessData(String status, HashMap<Integer, RadioObject> radioList, HashMap<Integer, SsidObject> ssidList,
			HashMap<Integer, List<WirelessCapability>> capabilityRadioList, HashMap<Integer, List<String>> standardTypeList) {

		this.status = status;
		this.radioList = radioList;
		this.ssidList = ssidList;
		this.capabilityRadioList = capabilityRadioList;
		this.standardTypeList = standardTypeList;
	}

	public void displayInfo() {
		System.out.println("[WIRELESS DATA] status   : " + status);
		System.out.println("[WIRELESS DATA] radioList   : ");

		Iterator it = radioList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, RadioObject> pair = (Map.Entry) it.next();
			System.out.println("\tchannel n° " + pair.getKey());
			pair.getValue().displayInfo();
		}

		System.out.println("[WIRELESS DATA] ssidList   : ");

		Iterator it2 = ssidList.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry<Integer, SsidObject> pair = (Map.Entry) it2.next();
			System.out.println("\tchannel n° " + pair.getKey());
			pair.getValue().displayInfo();
		}

		System.out.println("[WIRELESS DATA] capabilityRadioList   : ");

		Iterator it3 = capabilityRadioList.entrySet().iterator();
		while (it3.hasNext()) {
			Map.Entry<Integer, List<WirelessCapability>> pair = (Map.Entry) it3.next();
			System.out.println("\tchannel n° " + pair.getKey());
			for (int i = 0; i < pair.getValue().size(); i++) {
				pair.getValue().get(i).displayInfo();
			}
			System.out.println("----------------");
		}

		System.out.println("[WIRELESS DATA] standardTypeList   : ");
		Iterator it4 = standardTypeList.entrySet().iterator();
		while (it4.hasNext()) {
			Map.Entry<Integer, List<String>> pair = (Map.Entry) it4.next();
			System.out.println("\tchannel n° " + pair.getKey());
			for (int i = 0; i < pair.getValue().size(); i++) {
				System.out.println("=> " + pair.getValue().get(i));
			}
			System.out.println("----------------");
		}
	}

	/**
	 * Defined if at least one wireless radio is enabled
	 * 
	 * @return
	 */
	public boolean isRadioEnabled() {

		Iterator it = radioList.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry<Integer, RadioObject> pair = (Map.Entry) it.next();
			if (pair.getValue().isenable())
				return true;
		}
		return false;
	}

	/**
	 * Retrieve wireless status
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return list of radio characteristic for each wireless channel used
	 */
	public HashMap<Integer, RadioObject> getRadioList() {
		return radioList;
	}

	/**
	 * @return list of ssid characteristic for each wireless channel used
	 */
	public HashMap<Integer, SsidObject> getSsidList() {
		return ssidList;
	}

	/**
	 * 
	 * @return list of capabilities available for each wireless channel used
	 */
	public HashMap<Integer, List<WirelessCapability>> getCapabilityRadioList() {
		return capabilityRadioList;
	}

	/**
	 * 
	 * @return type of each wireless channel used (bgnac...)
	 */
	public HashMap<Integer, List<String>> getStandardTypeList() {
		return standardTypeList;
	}

}
