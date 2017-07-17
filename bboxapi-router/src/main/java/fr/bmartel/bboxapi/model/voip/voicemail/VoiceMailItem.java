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
package fr.bmartel.bboxapi.model.voip.voicemail;

import com.google.gson.annotations.SerializedName;
import fr.bmartel.bboxapi.model.voip.CallState;

/**
 * VoiceMailItem API data
 *
 * @author Bertrand Martel
 */
public class VoiceMailItem {

    @SerializedName("id")
    private int mId;

    @SerializedName("callername")
    private String mCallerName;

    @SerializedName("callernumber")
    private String mCallerNumber;

    @SerializedName("dateconsult")
    private String mDateConsult;

    @SerializedName("duration")
    private String mDuration;

    @SerializedName("linkmsg")
    private String mLinkMsg;

    @SerializedName("readstatus")
    private String mReadStatus;

    public int getId() {
        return mId;
    }

    public String getCallerName() {
        return mCallerName;
    }

    public String getCallerNumber() {
        return mCallerNumber;
    }

    public String getDateConsult() {
        return mDateConsult;
    }

    public String getDuration() {
        return mDuration;
    }

    public String getLinkMsg() {
        return mLinkMsg;
    }

    public String getReadStatus() {
        return mReadStatus;
    }
}
