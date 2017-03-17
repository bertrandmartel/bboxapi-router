package fr.bmartel.bboxapi.model.token;

import com.google.gson.annotations.SerializedName;

/**
 * Bbox token.
 *
 * @author Bertrand Martel
 */
public class BboxToken {

    @SerializedName("token")
    private String mToken;

    @SerializedName("now")
    private String mNow;

    @SerializedName("expires")
    private String mExpires;

    public String getToken() {
        return mToken;
    }

    public String getNow() {
        return mNow;
    }

    public String getExpire() {
        return mExpires;
    }

}