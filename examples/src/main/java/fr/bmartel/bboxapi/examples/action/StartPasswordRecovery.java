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

import java.lang.reflect.Type;
import java.util.List;


public class StartPasswordRecovery {

    private final static Logger LOGGER = LogManager.getLogger(Summary.class.getName());

    public static void main(String[] args) {

        BboxApi api = new BboxApi();

        //String pass = ExampleUtils.getPassword();

        //api.setPassword(pass);

        HttpStatus status = api.startPasswordRecovery();

        LOGGER.debug("status : " + status);
    }
}