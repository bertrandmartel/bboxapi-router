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
package fr.bmartel.bboxapi.model.profile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * ProfileItem API data
 *
 * @author Bertrand Martel
 */
public class ProfileItem {

    @SerializedName("invoice")
    private List<Invoice> mInvoiceList;

    @SerializedName("sms")
    private Sms mSms;

    @SerializedName("vod")
    private Service mVod;

    @SerializedName("services")
    private Service mServices;

    @SerializedName("line")
    private String mLine;

    @SerializedName("session")
    private String mSession;

    @SerializedName("login")
    private String mLogin;

    @SerializedName("state")
    private int mState;

    @SerializedName("code")
    private int mCode;

    @SerializedName("codemsg")
    private String mCodeMessage;

    @SerializedName("changedate")
    private String mChangedDate;

    public List<Invoice> getInvoiceList() {
        return mInvoiceList;
    }

    public Sms getSms() {
        return mSms;
    }

    public Service getVod() {
        return mVod;
    }

    public Service getServices() {
        return mServices;
    }

    public String getLine() {
        return mLine;
    }

    public String getSession() {
        return mSession;
    }

    public String getLogin() {
        return mLogin;
    }

    public int getState() {
        return mState;
    }

    public int getCode() {
        return mCode;
    }

    public String getCodeMessage() {
        return mCodeMessage;
    }

    public String getChangedDate() {
        return mChangedDate;
    }
}
