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

import fr.bmartel.bboxapi.voip.CallState;

/**
 * 
 * Voip API data
 * 
 * 
 * @author Bertrand Martel
 *
 */
public class Voip {
	
	private int id=0;
	
	/**
	 * voip status
	 */
	private String status ="";
	
	/**
	 * voip state
	 */
	private CallState callState = CallState.UNKNOWN;
	
	/**
	 * SIP voip line
	 */
	private String uri = "";
	
	/**
	 * blocking state
	 */
	private int blockState = 0;
	
	/**
	 * anonymous state
	 */
	private int anoncallState = 0;
	
	/**
	 * number of message waiting (?)
	 */
	private int mwi =0;
	
	/**
	 * number of message
	 */
	private int messageCount = 0;
	
	/**
	 * number of incoming call not answered
	 */
	private int notanswered = 0;
	
	/**
	 * Build voip object
	 * 
	 * @param id
	 * @param status
	 * @param callState
	 * @param uri
	 * @param blockState
	 * @param anoncallState
	 * @param mwi
	 * @param messageCount
	 * @param notanswered
	 */
	public Voip(int id,String status,CallState callState,String uri,int blockState,int anoncallState,int mwi,int messageCount,int notanswered){
		this.id=id;
		this.status=status;
		this.callState=callState;
		this.uri=uri;
		this.blockState=blockState;
		this.anoncallState=anoncallState;
		this.mwi=mwi;
		this.messageCount=messageCount;
		this.notanswered=notanswered;
	}
	
	public int getId(){
		return id;
	}
	
	public String getStatus(){
		return status;
	}
	
	public CallState getCallState(){
		return callState;
	}
	
	public String getUri(){
		return uri;
	}
	
	public int getBlockState(){
		return blockState;
	}
	
	public int getAnoncallState(){
		return anoncallState;
	}
	
	public int getMwi(){
		return mwi;
	}
	
	public int getMessageCount(){
		return messageCount;
	}
	
	public int getNotanswered(){
		return notanswered;
	}

	public void displayInfo() {
		System.out.println("[VOIP] id            : " + id);
		System.out.println("[VOIP] status        : " + status);
		System.out.println("[VOIP] callState     : " + callState);
		System.out.println("[VOIP] uri           : " + uri);
		System.out.println("[VOIP] blockState    : " + blockState);
		System.out.println("[VOIP] anoncallState : " + anoncallState);
		System.out.println("[VOIP] mwi           : " + mwi);
		System.out.println("[VOIP] messageCount  : " + messageCount);
		System.out.println("[VOIP] notanswered   : " + notanswered);
	}
	
	
}
