package fr.bmartel.bboxapi.router.javasample;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.result.Result;
import fr.bmartel.bboxapi.router.BboxApiRouter;
import fr.bmartel.bboxapi.router.model.Voip;
import kotlin.Triple;

import java.util.List;

public class PasswordRecovery {

    public static void main(String args[]) {
        BboxApiRouter bboxapi = new BboxApiRouter();
        System.out.println("push the button on your Bbox for setting your password, you have 20 seconds");
        Boolean state = bboxapi.waitForPushButton(20000, 1000);
        if (state) {
            Triple<Request, Response, Result<String, FuelError>> result = bboxapi.resetPasswordSync("admin123");
            System.out.println("set password : " + result.component2().getStatusCode());
            Triple<Request, Response, Result<List<Voip>, FuelError>> data = bboxapi.getVoipInfoSync();
            Result<List<fr.bmartel.bboxapi.router.model.Voip>, FuelError> obj = data.getThird();
            System.out.println(obj.get());
        } else {
            System.out.println("didn't detect the push button");
        }
    }
}
