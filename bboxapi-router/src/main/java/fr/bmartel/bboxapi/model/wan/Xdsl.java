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
 * XDSL info.
 *
 * @author Bertrand Martel
 */
public class Xdsl {

    @SerializedName("state")
    private String mState;

    @SerializedName("modulation")
    private String mModulation;

    @SerializedName("showtime")
    private int mShowtime;

    @SerializedName("atur_provider")
    private String mAturProvider;

    @SerializedName("atuc_provider")
    private String mAtucProvider;

    @SerializedName("sync_count")
    private int mSyncCount;

    @SerializedName("up")
    private Link mUplink;

    @SerializedName("down")
    private Link mDownlink;

    public String getState() {
        return mState;
    }

    public String getModulation() {
        return mModulation;
    }

    public int getShowTime() {
        return mShowtime;
    }

    public String getAturProvider() {
        return mAturProvider;
    }

    public String getAtucProvider() {
        return mAtucProvider;
    }

    public int getSyncCount() {
        return mSyncCount;
    }

    public Link getUplink() {
        return mUplink;
    }

    public Link getDownlink() {
        return mDownlink;
    }
}