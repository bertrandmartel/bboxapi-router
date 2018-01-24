package fr.bmartel.bboxapi.examples.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.bmartel.bboxapi.BboxApi;
import fr.bmartel.bboxapi.examples.request.Summary;
import fr.bmartel.bboxapi.examples.utils.ExampleUtils;
import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.model.recovery.VerifyRecovery;
import fr.bmartel.bboxapi.response.VerifyRecoveryResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


/**
 * Setup password for main version >= 12
 */
public class SetupButton {

    private final static Logger LOGGER = LogManager.getLogger(Summary.class.getName());

    public static void main(String[] args) throws IOException {

        BboxApi api = new BboxApi();

        HttpStatus status = api.startPasswordRecovery();

        LOGGER.debug("status : " + status);

        if (status == HttpStatus.OK) {

            int expire = -1;

            while (expire != 0) {
                VerifyRecoveryResponse verifyRecoveryResponse = api.verifyPasswordRecovery();

                if (verifyRecoveryResponse.getStatus() == HttpStatus.OK) {

                    if (verifyRecoveryResponse.getVerifyList() == null) {

                        LOGGER.debug("push button success");

                        String password = ExampleUtils.getPassword();

                        HttpStatus setPasswordStatus = api.resetPassword(password);

                        LOGGER.debug("status : " + setPasswordStatus);

                        break;
                    }

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.setPrettyPrinting().create();
                    Type listOfTestObject = new TypeToken<List<VerifyRecovery>>() {
                    }.getType();
                    String verifyList = gson.toJson(verifyRecoveryResponse.getVerifyList(), listOfTestObject);

                    LOGGER.debug(verifyList);
                } else {
                    LOGGER.error("http error  : " + verifyRecoveryResponse.getStatus());
                }

                if (expire != 0) {
                    expire = verifyRecoveryResponse.getVerifyList().get(0).getExpires();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}