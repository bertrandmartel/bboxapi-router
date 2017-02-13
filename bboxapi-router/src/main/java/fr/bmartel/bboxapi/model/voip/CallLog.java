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
package fr.bmartel.bboxapi.model.voip;

import com.google.gson.annotations.SerializedName;

/**
 * Call log entry
 *
 * @author Bertrand Martel
 */
public class CallLog {

    /**
     * call log id
     */
    @SerializedName("id")
    private int mId = 0;

    /**
     * phone number
     */
    @SerializedName("number")
    private String mNumber = "";

    /**
     * call date
     */
    @SerializedName("date")
    private long mDate = 0;

    /**
     * call type
     */
    @SerializedName("type")
    private CallType mType = CallType.UNKNOWN;

    @SerializedName("answered")
    private int mAnswered;

    /**
     * call duration in seconds
     */
    @SerializedName("duree")
    private int mDuration = 0;

    public int isAnswered() {
        return mAnswered;
    }

    public int getId() {
        return mId;
    }

    public String getNumber() {
        return mNumber;
    }

    public long getDate() {
        return mDate;
    }

    public CallType getType() {
        return mType;
    }

    public int getDuration() {
        return mDuration;
    }
}
