package fr.bmartel.bboxapi.router.javasample;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.result.Result;
import fr.bmartel.bboxapi.router.BboxApi;
import kotlin.Triple;

import java.util.concurrent.CountDownLatch;

public class Reboot {

    public static void main(String args[]) throws InterruptedException {
        BboxApi bboxapi = new BboxApi();
        bboxapi.setPassword("admin");

        //asynchronous call
        CountDownLatch latch = new CountDownLatch(1);
        bboxapi.reboot(new Handler<String>() {
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
        Triple<Request, Response, Result<String, FuelError>> data = bboxapi.rebootSync();
        Request request = data.getFirst();
        Response response = data.getSecond();
        Result<String, FuelError> obj = data.getThird();
        System.out.println(response.getStatusCode());
    }
}
