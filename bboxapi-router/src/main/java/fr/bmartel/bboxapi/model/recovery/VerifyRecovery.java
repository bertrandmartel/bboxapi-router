package fr.bmartel.bboxapi.model.recovery;

import com.google.gson.annotations.SerializedName;

/**
 * Verify password recovery response.
 */
public class VerifyRecovery {

    @SerializedName("method")
    private String mMethod;

    @SerializedName("expires")
    private int mExpires;

    public String getMethod() {
        return mMethod;
    }

    public int getExpires() {
        return mExpires;
    }
}
