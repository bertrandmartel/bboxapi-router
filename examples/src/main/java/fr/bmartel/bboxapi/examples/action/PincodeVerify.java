package fr.bmartel.bboxapi.examples.action;

import fr.bmartel.bboxapi.BboxApi;
import fr.bmartel.bboxapi.examples.utils.ExampleUtils;
import fr.bmartel.bboxapi.model.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by akinaru on 24/03/17.
 */
public class PincodeVerify {

    private final static Logger LOGGER = LogManager.getLogger(PincodeVerify.class.getName());

    public static void main(String[] args) {

        BboxApi api = new BboxApi();

        String pincode = ExampleUtils.getPincode();

        HttpStatus status = api.sendPincodeVerify(pincode);

        LOGGER.debug("status : " + status);

        if (status == HttpStatus.OK) {

            String password = ExampleUtils.getPassword();

            HttpStatus setPasswordStatus = api.resetPassword(password);

            LOGGER.debug("status : " + setPasswordStatus);
        }
    }
}
