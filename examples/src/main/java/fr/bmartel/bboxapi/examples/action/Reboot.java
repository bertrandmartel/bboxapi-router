package fr.bmartel.bboxapi.examples.action;

import fr.bmartel.bboxapi.BboxApi;
import fr.bmartel.bboxapi.examples.utils.ExampleUtils;
import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.response.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Display request example.
 *
 * @author Bertrand Martel
 */
public class Reboot {

    private final static Logger LOGGER = LogManager.getLogger(Reboot.class.getName());

    public static void main(String[] args) throws IOException {

        BboxApi api = new BboxApi();

        String pass = ExampleUtils.getPassword();

        api.setPassword(pass);

        HttpResponse status = api.reboot();

        LOGGER.debug("status : " + status);
    }
}
