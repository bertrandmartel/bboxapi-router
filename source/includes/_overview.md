# Overview

> This call requires authentication :

```kotlin
val bboxapi = BboxApi()
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
BboxApi bboxapi = new BboxApi();
bboxapi.setPassword("root");

bboxapi.getVoipInfo(new Handler<List<Voip.Model>>() {
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
    public void success(Request request, Response response, List<Voip.Model> data) {
        System.out.println(data);
    }
});
```
> Replace `"root"` with your Bbox admin interface password

Most of the APIs require authentication via `setPassword(password: String)` which is Bbox admin interface password.

* get summary
* get XDSL
* get hosts
* get wan ip info
* get device info


| method              | API call                               | require auth | description                    
|---------------------|----------------------------------------|---------------|--------------------------------
| login               | `POST /login`                            | false | login                          
| getSummary          | `GET /summary`                           | false | information summary            
| getVoipInfo         | `GET /voip`                              | true | voip data                      
| getDeviceInfo       | `GET /device`                            | false(*) | device information             
| getCallLogs         | `GET /voip/fullcalllog/$line`            | true | call log                       
| getHosts            | `GET /hosts`                             | false(*) | known hosts                    
| getWirelessInfo     | `GET /wireless`                          | true | wireless info                  
| setWifiState        | `PUT /wireless?radio.enable=1`           | true | set wifi state                 
| setDisplayState     | `PUT /device/display?luminosity=100`     | true | set display state              
| voipDial            | `PUT /voip/dial?line=$line&number=$num`  | true | dial phone number              
| reboot              | `POST /device/reboot?btoken=xxx`         | true | reboot bbox                    
| getXdslInfo         | `GET /wan/xdsl`                          | false | get xdsl information           
| getWanIpInfo        | `GET /wan/ip`                            | false | get wan ip info                
| setWifiMacFilter    | `PUT /wireless/acl`                      | true | enable/disable wifi mac filter 
| getWifiMacFilter    | `GET /wireless/acl`                      | true | get wifi mac filters           
| createMacFilterRule | `POST /wireless/acl?btoken=xxx`          | true | create wifi mac filter         
| updateMacFilterRule | `PUT /wireless/acl/$rule`                | true | update wifi mac filter         
| deleteMacFilterRule | `DELETE /wireless/acl/$rule`             | true | delete wifi mac filter         

(*) some information may be missing when unauthenticated