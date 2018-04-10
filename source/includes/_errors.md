# Errors

> check exception type & response status code on failure :

```kotlin
val bboxapi = BboxApiRouter()
bboxapi.setPassword("root")

bboxapi.getVoipInfo { _, response, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            when {
                ex.exception is UnknownHostException -> println("hostname bbox.lan was not found")
                ex.exception is HttpException -> println("http error : ${response.statusCode}")
                else -> ex.printStackTrace()
            }
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
BboxApi bboxapi = new BboxApiRouter();
bboxapi.setPassword("root");

bboxapi.getVoipInfo(new Handler<List<Voip>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        if (error.getException() instanceof UnknownHostException) {
            System.out.println("hostname bbox.lan was not found");
        } else if (error.getException() instanceof HttpException) {
            System.out.println("http error : " + response.getStatusCode());
        } else {
            error.printStackTrace();
        }
    }

    @Override
    public void success(Request request, Response response, List<Voip> data) {
        System.out.println(data);
    }
});
```
> Replace `"root"` with your Bbox admin interface password


`FuelError` can be checked for exceptions, for instance : 

| Exception             |  description   |
|-----------------------|------------------------------------------|
| UnknownHostException  | [http://bbox.lan](http://bbox.lan) host was not found on network |
| HttpException         | a non 2XX HTTP response was received, check the status code from the response |

Among `HttpException`, you can find the following :

Error Code | Meaning
---------- | -------
400 | Bad Request -- request format is invalid
401 | Unauthorized -- password is invalid
404 | Not Found -- endpoint doesn't exist (check it starts with http://bbox.lan/api/v1)
429 | Too Many Requests -- too many fail login attempt, check the ban expiration date to retry more