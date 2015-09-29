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
 * Wireless capability template
 * 
 * @author Bertrand Martel
 *
 */
public class WirelessCapability {

	/**
	 * channel num
	 */
	private int channel = 0;

	/**
	 * channel used (52 to 136 are only available if Dynamic Frequency Selection
	 * is enabled)
	 */
	private String ht40 = "";

	/**
	 * no dynamic frequency selection used
	 */
	private boolean nodfs = false;

	/**
	 * channel availability check time
	 */
	private int cactime = 0;

	/**
	 * channel availability check time for 40MHz wide band
	 */
	private int cactime40 = 0;

	public WirelessCapability(int channel, String ht40, boolean nodfs, int cactime, int cactime40) {
		this.channel = channel;
		this.ht40 = ht40;
		this.nodfs = nodfs;
		this.cactime = cactime;
		this.cactime40 = cactime40;
	}

	public void displayInfo() {
		System.out.println("[CAPABILITY] channel   : " + channel);
		System.out.println("[CAPABILITY] ht40      : " + ht40);
		System.out.println("[CAPABILITY] nodfs     : " + nodfs);
		System.out.println("[CAPABILITY] cactime   : " + cactime);
		System.out.println("[CAPABILITY] cactime40 : " + cactime40);
	}
	
	/**
	 * @return channel num
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * @return channel used (52 to 136 are only available if Dynamic Frequency
	 *         Selection is enabled)
	 */
	public String getHt40() {
		return ht40;
	}

	/**
	 * @return no dynamic frequency selection used
	 */
	public boolean isNodfs() {
		return nodfs;
	}

	/**
	 * @return channel availability check time
	 */
	public int getCactime() {
		return cactime;
	}

	/**
	 * @return channel availability check time for 40MHz wide band
	 */
	public int getCactime40() {
		return cactime40;
	}

}
