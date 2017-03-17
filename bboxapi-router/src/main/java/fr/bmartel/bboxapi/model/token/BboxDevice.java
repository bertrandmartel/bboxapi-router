package fr.bmartel.bboxapi.model.token;

import com.google.gson.annotations.SerializedName;

/**
 * Bbox device used in token endpoint.
 *
 * @author Bertrand Martel
 */
public class BboxDevice {

    @SerializedName("device")
    private BboxToken mDevice;

    public BboxToken getBboxToken() {
        return mDevice;
    }
}