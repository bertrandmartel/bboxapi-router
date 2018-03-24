package fr.bmartel.bboxapi.javasample;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.result.Result;
import fr.bmartel.bboxapi.BboxApi;
import fr.bmartel.bboxapi.model.Voip;
import kotlin.Triple;

import java.util.concurrent.CountDownLatch;

public class VoipDial {

    public static void main(String args[]) throws InterruptedException {
        BboxApi bboxapi = new BboxApi();
        bboxapi.setPassword("admin");

        //asynchronous call
        CountDownLatch latch = new CountDownLatch(1);
        bboxapi.voipDial(Voip.Line.LINE1, "0123456789", new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                error.printStackTrace();
                latch.countDown();
            }

            @Override
            public void success(Request request, Response response, String data) {
                System.out.println(response.getStatusCode());
                latch.countDown();
            }
        });
        latch.await();

        //synchronous call
        Triple<Request, Response, Result<String, FuelError>> data = bboxapi.voipDialSync(Voip.Line.LINE1, "0123456789");
        Request request = data.getFirst();
        Response response = data.getSecond();
        Result<String, FuelError> obj = data.getThird();
        System.out.println(response.getStatusCode());
    }
}
