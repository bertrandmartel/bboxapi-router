package fr.bmartel.bboxapi.examples.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.bmartel.bboxapi.BboxApi;
import fr.bmartel.bboxapi.examples.utils.ExampleUtils;
import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.model.recovery.VerifyRecovery;
import fr.bmartel.bboxapi.model.summary.ApiSummary;
import fr.bmartel.bboxapi.response.VerifyRecoveryResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.List;


public class VerifyPasswordRecovery {

    private final static Logger LOGGER = LogManager.getLogger(Summary.class.getName());

    public static void main(String[] args) {

        BboxApi api = new BboxApi();

        VerifyRecoveryResponse verifyRecoveryResponse = api.verifyPasswordRecovery();

        if (verifyRecoveryResponse.getStatus() == HttpStatus.OK) {

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.setPrettyPrinting().create();
            Type listOfTestObject = new TypeToken<List<VerifyRecovery>>() {
            }.getType();
            String verifyList = gson.toJson(verifyRecoveryResponse.getVerifyList(), listOfTestObject);

            LOGGER.debug(verifyList);
        } else {
            LOGGER.error("http error  : " + verifyRecoveryResponse.getStatus());
        }
    }
}