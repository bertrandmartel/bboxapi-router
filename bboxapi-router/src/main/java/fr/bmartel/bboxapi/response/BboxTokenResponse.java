package fr.bmartel.bboxapi.response;

import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.model.token.BboxDevice;

import java.util.List;

/**
 * Response Bbox token request.
 *
 * @author Bertrand Martel
 */
public class BboxTokenResponse extends HttpResponse {

    private List<BboxDevice> mDeviceList;

    public BboxTokenResponse(List<BboxDevice> deviceList, HttpStatus status) {
        super(status);
        mDeviceList = deviceList;
    }

    public List<BboxDevice> getDeviceList() {
        return mDeviceList;
    }
}
