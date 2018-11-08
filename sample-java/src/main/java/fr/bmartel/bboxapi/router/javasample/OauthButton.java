package fr.bmartel.bboxapi.router.javasample;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.result.Result;
import fr.bmartel.bboxapi.router.BboxApiRouter;
import fr.bmartel.bboxapi.router.model.OauthToken;
import fr.bmartel.bboxapi.router.model.Scope;
import fr.bmartel.bboxapi.router.model.Wireless;
import kotlin.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class OauthButton {

    public static void main(String args[]) throws InterruptedException {
        BboxApiRouter bboxapi = new BboxApiRouter("client_id_test", "client_secret_test");
        bboxapi.init();

        List<Scope> scope = new ArrayList<>();
        scope.add(Scope.ALL);

        OauthToken token = bboxapi.authenticateOauthButton(20000, 1000, scope);

        if (token != null) {
            //store bboxapi.oauthToken?.refresh_token
            System.out.println(token);

            //asynchronous call
            CountDownLatch latch = new CountDownLatch(1);
            bboxapi.getWirelessInfo(new Handler<List<Wireless>>() {
                @Override
                public void failure(Request request, Response response, FuelError error) {
                    error.printStackTrace();
                    latch.countDown();
                }

                @Override
                public void success(Request request, Response response, List<Wireless> data) {
                    System.out.println(data);
                    latch.countDown();
                }
            });
            latch.await();

            //synchronous call
            Triple<Request, Response, Result<List<Wireless>, FuelError>> data = bboxapi.getWirelessInfoSync();
            Request request = data.getFirst();
            Response response = data.getSecond();
            Result<List<Wireless>, FuelError> obj = data.getThird();
            System.out.println(obj.get());
        } else {
            System.out.println("button wasn't pushed");
        }
    }
}
