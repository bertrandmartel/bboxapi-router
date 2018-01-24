package fr.bmartel.bboxapi.examples.action;

import fr.bmartel.bboxapi.BboxApi;
import fr.bmartel.bboxapi.examples.utils.ExampleUtils;
import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.model.voip.voicemail.VoiceMailItem;
import fr.bmartel.bboxapi.response.VoiceMailResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Mark all voicemail as read.
 *
 * @author Bertrand Martel
 */
public class VoiceMailRead {

    private final static Logger LOGGER = LogManager.getLogger(VoiceMailRead.class.getName());

    public static void main(String[] args) throws IOException {

        BboxApi api = new BboxApi();

        String pass = ExampleUtils.getPassword();

        api.setPassword(pass);

        VoiceMailResponse voiceMailResponse = api.getVoiceMailData();

        if (voiceMailResponse.getStatus() == HttpStatus.OK) {

            List<VoiceMailItem> voiceMails = voiceMailResponse.getVoiceMailList().get(0).getVoiceMailItems();

            for (VoiceMailItem item : voiceMails) {

                HttpStatus readResponse = api.readVoiceMail(item.getId());

                if (readResponse == HttpStatus.OK) {
                    LOGGER.debug("succesfully read voicemail " + item.getId());
                } else {
                    LOGGER.error("readResponse error for voicemail " + item.getId() + " : " + readResponse);
                }
            }

        } else {
            LOGGER.error("voiceMailResponse error  : " + voiceMailResponse.getStatus());
        }
    }
}
