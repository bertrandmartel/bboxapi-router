package fr.bmartel.bboxapi.router.javasample;

import com.github.kittinunf.fuel.core.*;
import com.github.kittinunf.result.Result;
import fr.bmartel.bboxapi.router.BboxApiRouter;
import kotlin.Triple;

import java.util.concurrent.CountDownLatch;

public class CustomRequest {

    public static void main(String args[]) throws InterruptedException {
        BboxApiRouter bboxapi = new BboxApiRouter();
        bboxapi.setPassword("admin");

        //asynchronous call
        CountDownLatch latch = new CountDownLatch(1);
        bboxapi.createCustomRequest(bboxapi.getManager().request(Method.GET, "/summary", null), false, new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                error.printStackTrace();
                latch.countDown();
            }

            @Override
            public void success(Request request, Response response, String data) {
                System.out.println(data);
                latch.countDown();
            }
        });
        latch.await();

        //synchronous call
        Triple<Request, Response, Result<String, FuelError>> data = bboxapi.createCustomRequestSync(bboxapi.getManager().request(Method.GET, "/voip", null), true);
        System.out.println(data.getThird().get());
    }
}
