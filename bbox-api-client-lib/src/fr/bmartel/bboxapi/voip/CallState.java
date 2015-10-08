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
 * Call state enum for voip api
 *
 * IDLE       : normal state
 * INCALL     : call 
 * RINGING    : phone is ringing
 * CALLINGOUT : calling to outside
 * OFFHOOK    : phone is offhook
 * 
 * @author Bertrand Martel
 *
 */
public enum CallState {

	IDLE,INCALL,RINGING,OFFHOOK,CALLINGOUT,UNKNOWN;
	
	public static CallState getValue(String value){
		
		if (value.equals("Idle")){
			return IDLE;
		}
		else if (value.equals("InCall")){
			return INCALL;
		}
		else if (value.equals("Ringing")){
			return RINGING;
		}
		else if (value.equals("OffHook")){
			return OFFHOOK;
		}
		
		return UNKNOWN;
	}
}