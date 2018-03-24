package fr.bmartel.bboxapi.javasample;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.result.Result;
import fr.bmartel.bboxapi.BboxApi;
import fr.bmartel.bboxapi.model.Voip;
import kotlin.Triple;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CallLog {

    public static void main(String args[]) throws InterruptedException {
        BboxApi bboxapi = new BboxApi();
        bboxapi.setPassword("admin");

        //asynchronous call
        CountDownLatch latch = new CountDownLatch(1);
        bboxapi.getCallLogs(Voip.Line.LINE1, new Handler<List<fr.bmartel.bboxapi.model.CallLog.Model>>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                error.printStackTrace();
                latch.countDown();
            }

            @Override
            public void success(Request request, Response response, List<fr.bmartel.bboxapi.model.CallLog.Model> data) {
                System.out.println(data);
                latch.countDown();
            }
        });
        latch.await();

        //synchronous call
        Triple<Request, Response, Result<List<fr.bmartel.bboxapi.model.CallLog.Model>, FuelError>> data = bboxapi.getCallLogsSync(Voip.Line.LINE1);
        Request request = data.getFirst();
        Response response = data.getSecond();
        Result<List<fr.bmartel.bboxapi.model.CallLog.Model>, FuelError> obj = data.getThird();
        System.out.println(obj.get());
    }
}
