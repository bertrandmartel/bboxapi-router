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
package fr.bmartel.bboxapi.model.wan;

import com.google.gson.annotations.SerializedName;

/**
 * A link object featuring a downlink or uplink meta info.
 *
 * @author Bertrand Martel
 */
public class Link {

    @SerializedName("bitrates")
    private int mBitrates;

    @SerializedName("noise")
    private int mNoise;

    @SerializedName("attenuation")
    private int mAttenuation;

    @SerializedName("power")
    private int mPower;

    @SerializedName("phyr")
    private int mPhyr;

    @SerializedName("ginp")
    private int mGinp;

    @SerializedName("nitro")
    private String mNitro;

    @SerializedName("interleave_delay")
    private int mInterleaveDelay;

    public int getBitrates() {
        return mBitrates;
    }

    public int getNoise() {
        return mNoise;
    }

    public int getAttenuation() {
        return mAttenuation;
    }

    public int getPower() {
        return mPower;
    }

    public int getPhyr() {
        return mPhyr;
    }

    public int getGinp() {
        return mGinp;
    }

    public String getNitro() {
        return mNitro;
    }

    public int getInterleaveDelay() {
        return mInterleaveDelay;
    }
}