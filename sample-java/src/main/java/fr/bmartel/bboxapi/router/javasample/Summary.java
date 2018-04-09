package fr.bmartel.bboxapi.router.javasample;

import com.github.kittinunf.fuel.core.*;
import com.github.kittinunf.result.Result;
import fr.bmartel.bboxapi.router.BboxApi;
import kotlin.Triple;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Summary {

    public static void main(String args[]) throws InterruptedException {
        BboxApi bboxapi = new BboxApi();

        System.out.println("authentication attempts : " + bboxapi.getAttempts());
        System.out.println("user is authenticated   : " + bboxapi.getAuthenticated());
        System.out.println("user is blocked         : " + bboxapi.getBlocked());
        System.out.println("ban expiration date     : " + bboxapi.getBlockedUntil());

        //asynchronous call
        CountDownLatch latch = new CountDownLatch(1);
        bboxapi.getSummary(new Handler<List<fr.bmartel.bboxapi.router.model.Summary>>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                if (error.getException() instanceof UnknownHostException) {
                    System.out.println("hostname bbox.lan was not found");
                } else if (error.getException() instanceof HttpException) {
                    System.out.println("http error : " + response.getStatusCode());
                } else {
                    error.printStackTrace();
                }
                latch.countDown();
            }

            @Override
            public void success(Request request, Response response, List<fr.bmartel.bboxapi.router.model.Summary> data) {
                System.out.println(data);
                latch.countDown();
            }
        });
        latch.await();

        //synchronous call
        Triple<Request, Response, Result<List<fr.bmartel.bboxapi.router.model.Summary>, FuelError>> data = bboxapi.getSummarySync();
        Request request = data.getFirst();
        Response response = data.getSecond();
        Result<List<fr.bmartel.bboxapi.router.model.Summary>, FuelError> obj = data.getThird();
        System.out.println(obj.get());
    }
}
