# Setting Password

The first time user connects to Bbox admin interface or when a user lose his/her password, the password should be resetted. User will have to do the following things : 

* trigger a call to `startPasswordRecovery`, on the admin interface, this is done by clicking a button
* call `verifyPasswordRecovery` many times (polling) to check if user has pressed the button on Bbox device
* when user has pressed the button the next call to `verifyPasswordRecovery` will grant authentication
* a call to `resetPassword("yourNewPassword")` is used to set a new password

You can simply use `waitForPushButton` to wait for an amount of time for the user to press the button with a configurable polling interval (default 1 second)

```kotlin
val bboxapi = BboxApiRouter()
println("push the button on your Bbox for setting your password, you have 20 seconds")
val state = bboxapi.waitForPushButton(maxDuration = 20000)
if (state) {
    val setPasswordRes = bboxapi.resetPasswordSync(password = "admin2")
    println("set password : ${setPasswordRes.second.statusCode}")
}
```

```java
BboxApi bboxapi = new BboxApiRouter();
System.out.println("push the button on your Bbox for setting your password, you have 20 seconds");
Boolean state = bboxapi.waitForPushButton(20000, 1000);
if (state) {
    Triple<Request, Response, Result<String, FuelError>> result = bboxapi.resetPasswordSync("123456");
    System.out.println("set password : " + result.component2().getStatusCode());
} else {
    System.out.println("didn't detect the push button");
}
```

# Oauth2.0 (Experimental)

> Oauth2.0 via push button

```kotlin
val bboxapi = BboxApiRouter(clientId = "client_id_test", clientSecret = "client_secret_test")
bboxapi.init()

val token = bboxapi.authenticateOauthButton(
        maxDuration = 20000,
        pollInterval = 1000,
        scope = listOf(Scope.ALL))
if (token != null) {
    //store bboxapi.oauthToken?.refresh_token
    println(token)
}
```

```java
BboxApiRouter bboxapi = new BboxApiRouter("client_id_test", "client_secret_test");
bboxapi.init();

List<Scope> scope = new ArrayList<>();
scope.add(Scope.ALL);

OauthToken token = bboxapi.authenticateOauthButton(20000, 1000, scope);

if (token != null) {
    //store bboxapi.oauthToken?.refresh_token
    System.out.println(token);
}
```

You can request Oauth2.0 access token/refresh token via button push.

A call to `authenticateOauthButton` will wait for a button push. This will give a refresh token & an access token

Subsequent call to secured api endpoint will use this access token instead of the Basic auth Cookie. Afterwards, when secured call return 401, a refresh token request will be automatically issues before retrying the request with the new access token

User should store the refresh token in local storage (`bboxapi.oauthToken?.refresh_token`) when button is push so you can set it when you initialize `BboxApiRouter` the next time user start your app

> Set refresh token manually

```kotlin
val bboxapi = BboxApiRouter(clientId = "client_id_test", clientSecret = "client_secret_test")
bboxapi.init()
bboxapi.oauthToken = OauthToken(
        access_token = "",
        refresh_token = "some refresh token you have stored",
        expires_in = 0,
        issued_at = "",
        token_type = "Bearer"
)
```

```java
BboxApiRouter bboxapi = new BboxApiRouter("client_id_test", "client_secret_test");
bboxapi.init();
bboxapi.setOauthToken(new OauthToken(
        "", //access token
        "some refresh token you have stored", //refresh token
        "Bearer", //token type
        0, //expires_in
        "" //issues_at
));
```


<aside class="warning">This API is experimental</aside>
