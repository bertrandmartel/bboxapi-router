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
package fr.bmartel.bboxapi.model.wireless;

import com.google.gson.annotations.SerializedName;

/**
 * SSID model.
 *
 * @author Bertrand Martel
 */
public class SsidItem {

    @SerializedName("id")
    private String mId;

    @SerializedName("enable")
    private int mEnable;

    @SerializedName("hidden")
    private int mHidden;

    @SerializedName("bssid")
    private String mBssId;

    @SerializedName("wmmenable")
    private int mWmmEnable;

    @SerializedName("wps")
    private WirelessStatus mWps;

    @SerializedName("security")
    private WirelessSecurity mSecurity;

    public String getId() {
        return mId;
    }

    public int isEnable() {
        return mEnable;
    }

    public int getHidden() {
        return mHidden;
    }

    public String getBssId() {
        return mBssId;
    }

    public int getWmmEnable() {
        return mWmmEnable;
    }

    public WirelessStatus getWps() {
        return mWps;
    }

    public WirelessSecurity getSecurity() {
        return mSecurity;
    }
}
