package fr.bmartel.bboxapi.response;

import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.model.recovery.VerifyRecovery;
import org.apache.http.StatusLine;

import java.util.List;

public class VerifyRecoveryResponse extends HttpResponse {

    private List<VerifyRecovery> mVerifyList;

    public VerifyRecoveryResponse(List<VerifyRecovery> verifyList, HttpStatus status, StatusLine statusLine) {
        super(status, statusLine);
        mVerifyList = verifyList;
    }

    public List<VerifyRecovery> getVerifyList() {
        return mVerifyList;
    }
}
