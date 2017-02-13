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
import fr.bmartel.bboxapi.model.device.Display;

import java.util.List;

/**
 * Summary api object.
 *
 * @author Bertrand Martel
 */
public class ApiSummary {

    @SerializedName("now")
    private String mNow;

    @SerializedName("authenticated")
    private int mAuthenticated;

    @SerializedName("display")
    private Display mDisplay;

    @SerializedName("internet")
    private InternetItem mInternet;

    @SerializedName("voip")
    private List<SummaryVoip> mVoip;

    @SerializedName("wireless")
    private SummaryWireless mWireless;

    @SerializedName("services")
    private Services mServices;

    @SerializedName("diags")
    private List<DiagItem> mDiagList;

    @SerializedName("hosts")
    private List<SummaryHost> mHostList;

    @SerializedName("wan")
    private Wan mWan;

    public String getNow() {
        return mNow;
    }

    public int isAuthenticated() {
        return mAuthenticated;
    }

    public Display getDisplay() {
        return mDisplay;
    }

    public InternetItem getInternetItem() {
        return mInternet;
    }

    public List<SummaryVoip> getVoip() {
        return mVoip;
    }

    public SummaryWireless getWireless() {
        return mWireless;
    }

    public Services getServices() {
        return mServices;
    }

    public List<DiagItem> getDiagList() {
        return mDiagList;
    }

    public List<SummaryHost> getHostList() {
        return mHostList;
    }

    public Wan getWan() {
        return mWan;
    }
}
