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

/**
 * 
 * Radio Object template
 * 
 * @author Bertrand Martel
 *
 */
public class RadioObject {

	/**
	 * is radio enabled
	 */
	private boolean enable = false;

	/**
	 * type of wifi (bgnac)
	 */
	private String standard = "";

	/**
	 * state ?
	 */
	private int state = 0;

	/**
	 * channel used
	 */
	private int channel = 0;

	/**
	 * current channel
	 */
	private int currentChannel = 0;

	/**
	 * using dynamic frequency selection
	 */
	private boolean dfs = false;

	/**
	 * using 40MHz wide channel
	 */
	private boolean ht40 = false;

	public RadioObject(boolean enable, String standard, int state, int channel, int currentChannel, boolean dfs, boolean ht40) {

		this.enable = enable;
		this.standard = standard;
		this.state = state;
		this.channel = channel;
		this.currentChannel = currentChannel;
		this.dfs = dfs;
		this.ht40 = ht40;
	}

	public void displayInfo() {
		System.out.println("[RADIO OBJECT] enable         : " + enable);
		System.out.println("[RADIO OBJECT] standard       : " + standard);
		System.out.println("[RADIO OBJECT] state          : " + state);
		System.out.println("[RADIO OBJECT] channel        : " + channel);
		System.out.println("[RADIO OBJECT] currentChannel : " + currentChannel);
		System.out.println("[RADIO OBJECT] dfs            : " + dfs);
		System.out.println("[RADIO OBJECT] ht40           : " + ht40);
	}
	
	/**
	 * @return is radio enabled
	 */
	public boolean isenable() {
		return enable;
	}

	/**
	 * @return type of wifi (bgnac)
	 */
	public String getstandard() {
		return standard;
	}

	/**
	 * @return state ?
	 */
	public int getstate() {
		return state;
	}

	/**
	 * @return channel used
	 */
	public int getchannel() {
		return channel;
	}

	/**
	 * @return current channel
	 */
	public int getcurrentChannel() {
		return currentChannel;
	}

	/**
	 * @return using dynamic frequency selection
	 */
	public boolean isdfs() {
		return dfs;
	}

	/**
	 * @return using 40MHz wide channel
	 */
	public boolean isht40() {
		return ht40;
	}
}
