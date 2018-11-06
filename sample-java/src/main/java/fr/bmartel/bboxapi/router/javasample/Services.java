package fr.bmartel.bboxapi.router.javasample;

import com.github.kittinunf.fuel.core.*;
import com.github.kittinunf.result.Result;
import fr.bmartel.bboxapi.router.BboxApiRouter;
import kotlin.Triple;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Services {

    public static void main(String args[]) throws InterruptedException {
        BboxApiRouter bboxapi = new BboxApiRouter();
        bboxapi.init();
        //asynchronous call
        CountDownLatch latch = new CountDownLatch(1);
        bboxapi.getServices(new Handler<List<fr.bmartel.bboxapi.router.model.ServiceObject>>() {
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
            public void success(Request request, Response response, List<fr.bmartel.bboxapi.router.model.ServiceObject> data) {
                System.out.println(data);
                latch.countDown();
            }
        });
        latch.await();

        //synchronous call
        Triple<Request, Response, Result<List<fr.bmartel.bboxapi.router.model.ServiceObject>, FuelError>> data = bboxapi.getServicesSync();
        Request request = data.getFirst();
        Response response = data.getSecond();
        Result<List<fr.bmartel.bboxapi.router.model.ServiceObject>, FuelError> obj = data.getThird();
        System.out.println(obj.get());

        //check if remote access to API is activable eg password is strong enough
        System.out.println("remote access activable : " + bboxapi.isRemoteActivable());
    }
}
