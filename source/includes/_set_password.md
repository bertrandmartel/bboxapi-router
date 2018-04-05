# Setting Password

The first time user connects to Bbox admin interface or when a user lose his/her password, the password should be resetted. User will have to do the following things : 

* trigger a call to `startPasswordRecovery`, on the admin interface, this is done by clicking a button
* call `verifyPasswordRecovery` many times (polling) to check if user has pressed the button on Bbox device
* when user has pressed the button the next call to `verifyPasswordRecovery` will grant authentication
* a call to `resetPassword("yourNewPassword")` is used to set a new password

You can simply use `waitForPushButton` to wait for an amount of time for the user to press the button with a configurable polling interval (default 1 second)

```kotlin
val bboxapi = BboxApi()
println("push the button on your Bbox for setting your password, you have 20 seconds")
val state = bboxapi.waitForPushButton(maxDuration = 20000)
if (state) {
    val setPasswordRes = bboxapi.resetPasswordSync(password = "admin2")
    println("set password : ${setPasswordRes.second.statusCode}")
}
```

```java
BboxApi bboxapi = new BboxApi();
System.out.println("push the button on your Bbox for setting your password, you have 20 seconds");
Boolean state = bboxapi.waitForPushButton(20000, 1000);
if (state) {
    Triple<Request, Response, Result<String, FuelError>> result = bboxapi.resetPasswordSync("123456");
    System.out.println("set password : " + result.component2().getStatusCode());
} else {
    System.out.println("didn't detect the push button");
}
```