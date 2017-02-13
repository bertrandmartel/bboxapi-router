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
 * VoipItem API data
 *
 * @author Bertrand Martel
 */
public class VoipItem {

    @SerializedName("id")
    private int mId;

    /**
     * voip status
     */
    @SerializedName("status")
    private String mStatus;

    /**
     * voip state
     */
    @SerializedName("callstate")
    private CallState mCallState;

    /**
     * SIP voip line
     */
    @SerializedName("uri")
    private String mUri;

    /**
     * blocking state
     */
    @SerializedName("blockstate")
    private int mBlockState;

    /**
     * anonymous state
     */
    @SerializedName("anoncallstate")
    private int mAnoncallState;

    /**
     * number of message waiting (?)
     */
    @SerializedName("mwi")
    private int mMwi;

    /**
     * number of message
     */
    @SerializedName("message_count")
    private int mMessageCount;

    /**
     * number of incoming call not answered
     */
    @SerializedName("notanswered")
    private int mNotanswered;

    public int getId() {
        return mId;
    }

    public String getStatus() {
        return mStatus;
    }

    public CallState getCallState() {
        return mCallState;
    }

    public String getUri() {
        return mUri;
    }

    public int getBlockState() {
        return mBlockState;
    }

    public int getAnoncallState() {
        return mAnoncallState;
    }

    public int getMwi() {
        return mMwi;
    }

    public int getMessageCount() {
        return mMessageCount;
    }

    public int getNotanswered() {
        return mNotanswered;
    }
}
