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
 * Bbox device data
 * 
 * @author Bertrand Martel
 *
 */
public class BBoxDevice {

	/**
	 * device status ?
	 */
	private int status = 0;
	
	/**
	 * number of boot
	 */
	private int bootNumber = 0;
	
	/**
	 * box model name
	 */
	private String modelName="";
	
	/**
	 * define if user has already logged before
	 */
	private boolean userConfigured=false;
	
	/**
	 * display state 
	 */
	private boolean displayState = false;
	
	/**
	 * date of first use of bbox
	 */
	private String firstuseDate = "";
	
	/**
	 * bbox serial number
	 */
	private String serialNumber = "";
	
	/**
	 * Build Bbox device object
	 * 
	 * @param status
	 * @param bootNumber
	 * @param modelName
	 * @param userConfigured
	 * @param displayLuminosity
	 * @param displayState
	 * @param firstuseDate
	 */
	public BBoxDevice(int status, int bootNumber,String modelName,boolean userConfigured,boolean displayState,String firstuseDate){
		
		this.status=status;
		this.bootNumber=bootNumber;
		this.modelName=modelName;
		this.userConfigured=userConfigured;
		this.displayState=displayState;
		this.firstuseDate=firstuseDate;
	}
	
	public void setSerialNumber(String serialNumber){
		this.serialNumber=serialNumber;
	}
	
	public String getSerialNumber(){
		return serialNumber;
	}
	
	public int getStatus(){
		return status;
	}
	
	public int getBootNumber(){
		return bootNumber;
	}
	
	public String getModelName(){
		return modelName;
	}
	
	public boolean getUserConfigured(){
		return userConfigured;
	}
	
	public boolean displayState(){
		return displayState;
	}
	
	public String getFirstUseDate(){
		return firstuseDate;
	}
	
	public void displayInfo(){
		
		System.out.println("[DEVICE] status            : " + status);
		System.out.println("[DEVICE] bootNumber        : " + bootNumber);
		System.out.println("[DEVICE] modelName         : " + modelName);
		System.out.println("[DEVICE] userConfigured    : " + userConfigured);
		System.out.println("[DEVICE] displayState      : " + displayState);
		System.out.println("[DEVICE] firstuseDate      : " + firstuseDate);
		System.out.println("[DEVICE] serialNumber      : " + serialNumber);
	}
}
