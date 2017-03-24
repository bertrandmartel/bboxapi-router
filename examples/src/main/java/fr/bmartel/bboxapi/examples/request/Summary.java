/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2017 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.bboxapi.examples.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.bmartel.bboxapi.BboxApi;
import fr.bmartel.bboxapi.model.HttpStatus;
import fr.bmartel.bboxapi.model.summary.ApiSummary;
import fr.bmartel.bboxapi.response.SummaryResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Summary request example.
 *
 * @author Bertrand Martel
 */
public class Summary {

    private final static Logger LOGGER = LogManager.getLogger(Summary.class.getName());

    public static void main(String[] args) {

        BboxApi api = new BboxApi();

        SummaryResponse summaryResponse = api.getDeviceSummary(false);

        if (summaryResponse.getStatus() == HttpStatus.OK) {

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.setPrettyPrinting().create();
            Type listOfTestObject = new TypeToken<List<ApiSummary>>() {
            }.getType();
            String summary = gson.toJson(summaryResponse.getSummary(), listOfTestObject);

            LOGGER.debug(summary);
        } else {
            LOGGER.error("http error  : " + summaryResponse.getStatus());
        }
    }
}
