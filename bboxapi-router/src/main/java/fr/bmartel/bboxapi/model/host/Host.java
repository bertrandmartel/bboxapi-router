/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2017-2018 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.bboxapi.model.host;

import com.google.gson.annotations.SerializedName;

/**
 * Hosts object.
 *
 * @author Bertrand Martel
 */
public class Host {

    /**
     * Host id
     */
    @SerializedName("id")
    private int mId;

    /**
     * Host name
     */
    @SerializedName("hostname")
    private String mHostname;

    /**
     * Host mac addr
     */
    @SerializedName("macaddress")
    private String mMacAddress;

    /**
     * host ip
     */
    @SerializedName("ipaddress")
    private String mIpaddress;

    /**
     * static or STB type ?
     */
    @SerializedName("type")
    private String mType;

    /**
     * link used (Offline / SummaryWifi 5 / SummaryWifi 2.4)
     */
    @SerializedName("link")
    private String mLink;

    /**
     * same as device "Device" or "STB"
     */
    @SerializedName("devicetype")
    private String mDeviceType;

    /**
     * date when device was first seen
     */
    @SerializedName("firstseen")
    private String mFirstseen;

    /**
     * date when device was last seen
     */
    @SerializedName("lastseen")
    private String mLastseen;

    @SerializedName("ethernet")
    private HostEthernet mEthernet;

    @SerializedName("wireless")
    private HostWireless mWireless;

    @SerializedName("plc")
    private HostPlc mPlc;

    @SerializedName("parentalcontrol")
    private HostParentalControl mParentalControl;

    @SerializedName("ping")
    private HostPing mPing;

    /**
     * lease time
     */
    @SerializedName("lease")
    private int mLease;

    /**
     * define if host is active
     */
    @SerializedName("active")
    private int mActive;

    public HostPing getPing() {
        return mPing;
    }

    public HostPlc getPlc() {
        return mPlc;
    }

    public HostParentalControl getParentalControl() {
        return mParentalControl;
    }

    public HostWireless getWireless() {
        return mWireless;
    }

    public HostEthernet getEthernet() {
        return mEthernet;
    }

    public int getId() {
        return mId;
    }

    public String getHostname() {
        return mHostname;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public String getIpAddress() {
        return mIpaddress;
    }

    public String getType() {
        return mType;
    }

    public String getLink() {
        return mLink;
    }

    public String getDeviceType() {
        return mDeviceType;
    }

    public String getFirstSeen() {
        return mFirstseen;
    }

    public String getLastSeen() {
        return mLastseen;
    }

    public int getLease() {
        return mLease;
    }

    public int isActive() {
        return mActive;
    }

    public void setHostname(String hostname) {
        mHostname = hostname;
    }
}
