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

/**
 *  Hosts entry
 * 
 * @author Bertrand Martel
 *
 */
public class Host {

	/**
	 * Host id
	 */
	private int id = 0;
	
	/**
	 * Host name
	 */
	private String hostname = "";
	
	/**
	 * Host mac addr
	 */
	private String macaddress="";
	
	/**
	 * host ip
	 */
	private String ipaddress="";
	
	/**
	 * static or STB type ?
	 */
	private String type="";
	
	/**
	 * link used (Offline / Wifi 5 / Wifi 2.4)
	 */
	private String link = "";
	
	/**
	 * same as device "Device" or "STB"
	 */
	private String devicetype ="";
	
	/**
	 * date when device was first seen
	 */
	private String firstseen="";
	
	/**
	 * date when device was last seen
	 */
	private String lastseen ="";
	
	/**
	 * lease time
	 */
	private int lease=0;
	
	/**
	 * define if host is active
	 */
	private boolean active = false;
	
	/**
	 * Build host object
	 * 
	 * @param id
	 * @param hostname
	 * @param macaddress
	 * @param ipaddress
	 * @param type
	 * @param link
	 * @param devicetype
	 * @param firstseen
	 * @param lastseen
	 * @param lease
	 * @param active
	 */
	public Host(int id,String hostname,String macaddress,String ipaddress,String type,String link,String devicetype,String firstseen,String lastseen,int lease,boolean active){
		
		this.id=id;
		this.hostname=hostname;
		this.macaddress=macaddress;
		this.ipaddress=ipaddress;
		this.type=type;
		this.link=link;
		this.devicetype=devicetype;
		this.firstseen=firstseen;
		this.lastseen=lastseen;
		this.lease=lease;
		this.active=active;
	}
	
	public int getId(){
		return id;
	}
	
	public String getHostname(){
		return hostname;
	}
	
	public String getMacAddress(){
		return macaddress;
	}
	
	public String getIpAddress(){
		return ipaddress;
	}
	
	public String getType(){
		return type;
	}
	
	public String getLink(){
		return link;
	}
	public String getDeviceType(){
		return devicetype;
	}
	
	public String getFirstSeen(){
		return firstseen;
	}
	
	public String getLastSeen(){
		return lastseen;
	}
	
	public int getLease(){
		return lease;
	}
	
	public boolean isActive(){
		return active;
	}
	
	public void displayInfo() {
		System.out.println("[HOST] id         : " + id);
		System.out.println("[HOST] hostname   : " + hostname);
		System.out.println("[HOST] macaddress : " + macaddress);
		System.out.println("[HOST] ipaddress  : " + ipaddress);
		System.out.println("[HOST] type       : " + type);
		System.out.println("[HOST] link       : " + link);
		System.out.println("[HOST] devicetype : " + devicetype);
		System.out.println("[HOST] firstseen  : " + firstseen);
		System.out.println("[HOST] lastseen   : " + lastseen);
		System.out.println("[HOST] lease      : " + lease);
		System.out.println("[HOST] active     : " + active);
	}

	public void displaySummaryInfo() {
		System.out.println("[HOST] hostname   : " + hostname);
		System.out.println("[HOST] ipaddress  : " + ipaddress);
	}
}
