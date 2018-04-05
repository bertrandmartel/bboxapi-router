package fr.bmartel.bboxapi.javasample;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.result.Result;
import fr.bmartel.bboxapi.BboxApi;
import kotlin.Triple;

public class PasswordRecovery {

    public static void main(String args[]) {
        BboxApi bboxapi = new BboxApi();
        System.out.println("push the button on your Bbox for setting your password, you have 20 seconds");
        Boolean state = bboxapi.waitForPushButton(20000, 1000);
        if (state) {
            Triple<Request, Response, Result<String, FuelError>> result = bboxapi.resetPasswordSync("123456");
            System.out.println("set password : " + result.component2().getStatusCode());
        } else {
            System.out.println("didn't detect the push button");
        }
    }
}
