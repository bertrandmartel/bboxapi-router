package fr.bmartel.bboxapi.examples.action;

import fr.bmartel.bboxapi.BboxApi;
import fr.bmartel.bboxapi.examples.utils.ExampleUtils;
import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.response.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Setup Password for main version < 12
 */
public class SetupPincode {

    private final static Logger LOGGER = LogManager.getLogger(SetupPincode.class.getName());

    public static void main(String[] args) throws IOException {

        BboxApi api = new BboxApi();

        String pincode = ExampleUtils.getPincode();

        HttpResponse response = api.sendPincodeVerify(pincode);

        LOGGER.debug("status : " + response.getStatus());

        if (response.getStatus() == HttpStatus.OK) {

            String password = ExampleUtils.getPassword();

            HttpResponse setPasswordStatus = api.resetPassword(password);

            LOGGER.debug("status : " + setPasswordStatus.getStatus());
        }
    }
}
