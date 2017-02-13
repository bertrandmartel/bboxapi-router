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
package fr.bmartel.bboxapi.model.host;

import com.google.gson.annotations.SerializedName;

/**
 * Wireless field for Host request.
 *
 * @author Bertrand Martel
 */
public class HostWireless {

    @SerializedName("band")
    private String mBand;

    @SerializedName("rssi0")
    private int mRssi0;

    @SerializedName("rssi1")
    private int mRssi1;

    @SerializedName("rssi2")
    private int mRssi2;

    @SerializedName("mcs")
    private int mMcs;

    @SerializedName("rate")
    private int mRate;

    @SerializedName("idle")
    private int mIdle;

    public String getBand() {
        return mBand;
    }

    public int getRssi0() {
        return mRssi0;
    }

    public int getRssi1() {
        return mRssi1;
    }

    public int getmRssi2() {
        return mRssi2;
    }

    public int getMcs() {
        return mMcs;
    }

    public int getRate() {
        return mRate;
    }

    public int getIdle() {
        return mIdle;
    }
}
