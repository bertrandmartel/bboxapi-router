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
 * SSID Template
 * 
 * @author Bertrand Martel
 *
 */
public class SsidObject {

	/**
	 * ssid name
	 */
	private String id = "";

	/**
	 * ssid enabled
	 */
	private boolean enabled = false;

	/**
	 * is hidden ssid
	 */
	private boolean hidden = false;

	/**
	 * basic set of service address
	 */
	private String bssid = "";

	/**
	 * Wifi multimedia enabled
	 */
	private boolean wmmenable = false;

	/**
	 * WPS security is enabled
	 */
	private boolean wpsenabled = false;

	/**
	 * WPS status
	 */
	private String wpsstatus = "";

	/**
	 * security use by default for this ssid
	 */
	private boolean securityDefault = false;

	/**
	 * security protocol used (WPA / WPA2 ...)
	 */
	private String securityProtocol = "";

	/**
	 * enryption used
	 */
	private String securityEncryption = "";

	/**
	 * wifi passphrase
	 */
	private String securityPassphrase = "";

	public SsidObject(String id, boolean enabled, boolean hidden, String bssid, boolean wmmenable, boolean wpsenabled, String wpsstatus,
			boolean securityDefault, String securityProtocol, String securityEncryption, String securityPassphrase) {

		this.id = id;
		this.enabled = enabled;
		this.hidden = hidden;
		this.bssid = bssid;
		this.wmmenable = wmmenable;
		this.wpsenabled = wpsenabled;
		this.wpsstatus = wpsstatus;
		this.securityDefault = securityDefault;
		this.securityProtocol = securityProtocol;
		this.securityEncryption = securityEncryption;
		this.securityPassphrase = securityPassphrase;
	}

	public void displayInfo() {
		System.out.println("[SSID] id                 : " + id);
		System.out.println("[SSID] enabled            : " + enabled);
		System.out.println("[SSID] hidden             : " + hidden);
		System.out.println("[SSID] bssid              : " + bssid);
		System.out.println("[SSID] wmmenable          : " + wmmenable);
		System.out.println("[SSID] wpsenabled         : " + wpsenabled);
		System.out.println("[SSID] wpsstatus          : " + wpsstatus);
		System.out.println("[SSID] securityDefault    : " + securityDefault);
		System.out.println("[SSID] securityProtocol   : " + securityProtocol);
		System.out.println("[SSID] securityEncryption : " + securityEncryption);
		System.out.println("[SSID] securityPassphrase : " + securityPassphrase);
	}
	/**
	 * @return ssid name
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return ssid enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @return is hidden ssid
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * @return basic set of service address
	 */
	public String getBssid() {
		return bssid;
	}

	/**
	 * @return Wifi multimedia enabled
	 */
	public boolean isWmmenable() {
		return wmmenable;
	}

	/**
	 * @return WPS security is enabled
	 */
	public boolean isWpsenabled() {
		return wpsenabled;
	}

	/**
	 * @return WPS status
	 */
	public String getWpsstatus() {
		return wpsstatus;
	}

	/**
	 * @return security use by default for this ssid
	 */
	public boolean isSecurityDefault() {
		return securityDefault;
	}

	/**
	 * @return security protocol used (WPA / WPA2 ...)
	 */
	public String getSecurityProtocol() {
		return securityProtocol;
	}

	/**
	 * @return enryption used
	 */
	public String getSecurityEncryption() {
		return securityEncryption;
	}

	/**
	 * @return wifi passphrase
	 */
	public String getSecurityPassphrase() {
		return securityPassphrase;
	}

}
