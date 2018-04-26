package fr.bmartel.bboxapi.router.javasample;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.result.Result;
import fr.bmartel.bboxapi.router.BboxApiRouter;
import kotlin.Triple;

public class RemoteAccess {

    public static void main(String args[]) throws InterruptedException {
        BboxApiRouter bboxapi = new BboxApiRouter();
        bboxapi.setPassword("AAaaa*1111");
        Triple<Request, Response, Result<String, FuelError>> data = bboxapi.configureRemoteAccess(true);
        if (data != null) {
            Request request = data.getFirst();
            Response response = data.getSecond();
            Result<String, FuelError> obj = data.getThird();
            System.out.println(response.getStatusCode());
        } else {
            System.out.println("remote is not activable, change password strength to STRONG");
        }
    }
}
