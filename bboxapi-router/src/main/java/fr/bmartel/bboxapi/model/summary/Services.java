/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2017 Bertrand Martel
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
package fr.bmartel.bboxapi.model.summary;

import com.google.gson.annotations.SerializedName;

/**
 * Services listed in Summary model.
 *
 * @author Bertrand Martel
 */
public class Services {

    @SerializedName("hotspot")
    private Status mHotspot;

    @SerializedName("firewall")
    private Status mFirewall;

    @SerializedName("dyndns")
    private Status mDynDns;

    @SerializedName("dhcp")
    private Status mDhcp;

    @SerializedName("nat")
    private Status mNat;

    @SerializedName("dmz")
    private Status mDmz;

    @SerializedName("natpat")
    private Status mNatPat;

    @SerializedName("upnp")
    private Upnp mUpnp;

    @SerializedName("notification")
    private Status mNotification;

    @SerializedName("proxywol")
    private Status mProxyWol;

    @SerializedName("remoteweb")
    private Status mRemoteWeb;

    @SerializedName("parentalcontrol")
    private Status mParentalControl;

    @SerializedName("wifischeduler")
    private Status mWifiScheduler;

    @SerializedName("samba")
    private Status mSamba;

    @SerializedName("printer")
    private Status mPrinter;

    @SerializedName("dlna")
    private Status mDlna;

    public Status getHotspot() {
        return mHotspot;
    }

    public Status getFirewall() {
        return mFirewall;
    }

    public Status getDynDns() {
        return mDynDns;
    }

    public Status getDhcp() {
        return mDhcp;
    }

    public Status getNat() {
        return mNat;
    }

    public Status getDmz() {
        return mDmz;
    }

    public Status getNatPat() {
        return mNatPat;
    }

    public Upnp getUpnp() {
        return mUpnp;
    }

    public Status getNotification() {
        return mNotification;
    }

    public Status getProxyWol() {
        return mProxyWol;
    }

    public Status getRemoteWeb() {
        return mRemoteWeb;
    }

    public Status getParentalControl() {
        return mParentalControl;
    }

    public Status getWifiScheduler() {
        return mWifiScheduler;
    }

    public Status getSamba() {
        return mSamba;
    }

    public Status getPrinter() {
        return mPrinter;
    }

    public Status getDlna() {
        return mDlna;
    }
}
