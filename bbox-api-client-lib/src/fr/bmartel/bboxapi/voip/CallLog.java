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
package fr.bmartel.bboxapi.voip;

/**
 * Call log entry
 * 
 * @author Bertrand Martel
 *
 */
public class CallLog {
	
	/**
	 * call log id
	 */
	private int id=0;
	
	/**
	 * phone number
	 */
	private String number ="";
	
	/**
	 * call date
	 */
	private long date =0;
	
	/**
	 * call type
	 */
	private CallType type=CallType.UNKNOWN;
	
	/**
	 * define if call was answered or not
	 */
	private boolean answered = false;
	
	/**
	 * call duration in seconds
	 */
	private int durationSeconds = 0;
	
	/**
	 * Build call log object
	 * 
	 * @param id
	 * @param number
	 * @param date
	 * @param type
	 * @param answered
	 * @param callDuration
	 */
	public CallLog(int id,String number,long date,CallType type,boolean answered,int callDuration){
		this.id=id;
		this.number=number;
		this.date=date;
		this.type=type;
		this.answered=answered;
		this.durationSeconds=callDuration;
	}
	
	public int getId(){
		return id;
	}
	
	public String getNumber(){
		return number;
	}
	
	public long getDate(){
		return date;
	}
	
	public boolean isAnswered(){
		return answered;
	}
	
	public CallType getType(){
		return type;
	}
	
	public int getDuration(){
		return durationSeconds;
	}
	
	public void displayInfo() {
		System.out.println("[CALL LOG] id       : " + id);
		System.out.println("[CALL LOG] number   : " + number);
		System.out.println("[CALL LOG] date     : " + date);
		System.out.println("[CALL LOG] answered : " + answered);
		System.out.println("[CALL LOG] type     : " + type);
	}
}
