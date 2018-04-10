# API reference

## Get Bbox Summary

> Asynchronous 

```kotlin
bboxapi.getSummary { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.getSummary(new Handler<List<Summary>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, List<Summary> data) {
        System.out.println(data);
    }
});
```

> Synchronous

```kotlin
val (_, _, result) = bboxapi.getSummarySync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<List<Summary>, FuelError>> data = bboxapi.getSummarySync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<List<Summary>, FuelError> obj = data.getThird();
System.out.println(obj.get());
```

Retrieve Bbox Summary information which is an aggregation of all general API info including : 

* voip
* hosts
* device info
* services
* display
* wireless 
* usb
* diags
* wan

<aside class="success">
This API doesn't require authentication
</aside>

## Get VOIP information

> Asynchronous 

```kotlin
bboxapi.getVoipInfo { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.getVoipInfo(new Handler<List<Voip>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, List<Voip> data) {
        System.out.println(data);
    }
});
```

> Synchronous 

```kotlin
val (_, _, result) = bboxapi.getVoipInfoSync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<List<Voip>, FuelError>> data = bboxapi.getVoipInfoSync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<List<Voip>, FuelError> obj = data.getThird();
System.out.println(obj.get());
```

Retrieve Voice over IP (VOIP) information including :

* voip status
* uri (which includes landline phone number)
* unread voice mail count
* voice mail count
* not answered voice mail count

<aside class="warning">This API requires authentication</aside>

## Get Hosts

> Asynchronous 

```kotlin
bboxapi.getHosts { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.getHosts(new Handler<List<Hosts>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, List<Hosts> data) {
        System.out.println(data);
    }
});
```

> Synchronous

```kotlin
val (_, _, result) = bboxapi.getHostsSync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<List<Hosts>, FuelError>> data = bboxapi.getHostsSync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<List<Hosts>, FuelError> obj = data.getThird();
System.out.println(obj.get());
```

Get a list of known hosts including : 

* hostname
* mac address
* ip address
* link type (online/offline)
* host status (Device/STB)
* firstseen date
* lastseen date
* wireless info

<aside class="success">
This API doesn't require authentication
</aside>

## Get device information

> Asynchronous

```kotlin
bboxapi.getDeviceInfo { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.getDeviceInfo(new Handler<List<Device>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, List<Device> data) {
        System.out.println(data);
    }
});
```

> Synchronous

```kotlin
val (_, _, result) = bboxapi.getDeviceInfoSync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<List<Device>, FuelError>> data = bboxapi.getDeviceInfoSync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<List<Device>, FuelError> obj = data.getThird();
System.out.println(obj.get());
```

Retrieve Bbox information including : 

* box time
* status
* number of boots
* model name
* user configuration status (if user has set admin interface password)
* display luminosity
* version of services
* first use date
* uptime
* usage of ipv4, ipv6, adsl, ftth, vdsl

<aside class="success">
This API doesn't require authentication
</aside>

## Get Call logs

> Asynchronous

```kotlin
bboxapi.getCallLogs(line = Line.LINE1) { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.getCallLogs(Line.LINE1, new Handler<List<CallLog>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, List<CallLog> data) {
        System.out.println(data);
    }
});
```

> Synchronous

```kotlin
val (_, _, result) = bboxapi.getCallLogsSync(line = Line.LINE1)
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<List<CallLog>, FuelError>> data = bboxapi.getCallLogsSync(Line.LINE1);

Request request = data.getFirst();
Response response = data.getSecond();
Result<List<CallLog>, FuelError> obj = data.getThird();
System.out.println(obj.get());
```

> replace `Line.LINE1` with `Line.LINE2` to get the call logs for second line

Retrieve full call logs since last reboot. Call logs information include the following info :

* call log id
* phone number
* call type (incoming/outgoing)
* answered status
* duration

<aside class="warning">This API requires authentication</aside>

## Get wireless info

> Asynchronous

```kotlin
bboxapi.getWirelessInfo { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.getWirelessInfo(new Handler<List<Wireless>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, List<Wireless> data) {
        System.out.println(data);
    }
});
```

> Synchronous

```kotlin
val (_, _, result) = bboxapi.getWirelessInfoSync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<List<Wireless>, FuelError>> data = bboxapi.getWirelessInfoSync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<List<Wireless>, FuelError> obj = data.getThird();
System.out.println(obj.get());
```

Get wireless information including : 

* status
* wifi 2.4Ghz & 5Ghz configurations
* channels
* ssid
* bssid
* wps
* security (wifi password, protocol & encryption)
* capabilities

<aside class="warning">This API requires authentication</aside>

## Set wifi state

> Asynchronous

```kotlin
bboxapi.setWifiState(state = true) { _, res, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println(res.statusCode)
        }
    }
}
```

```java
bboxapi.setWifiState(true, new Handler<String>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, String data) {
        System.out.println(response.getStatusCode());
    }
});
```

> Synchronous

```kotlin
val (_, res, result) = bboxapi.setWifiStateSync(state = true)
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        println(res.statusCode)
    }
}
```

```java
Triple<Request, Response, Result<String, FuelError>> data = bboxapi.setWifiStateSync(true);

Request request = data.getFirst();
Response response = data.getSecond();
Result<String, FuelError> obj = data.getThird();
System.out.println(response.getStatusCode());
```

Enable or disable Wifi on Bbox

<aside class="warning">This API requires authentication</aside>

## Set display state

> Asynchronous

```kotlin
bboxapi.setDisplayState(state = true) { _, res, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println(res.statusCode)
        }
    }
    latch.countDown()
}
```

```java
bboxapi.setDisplayState(true, new Handler<String>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, String data) {
        System.out.println(response.getStatusCode());
    }
});
```

> Synchronous

```kotlin
val (_, res, result) = bboxapi.setDisplayStateSync(state = true)
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        println(res.statusCode)
    }
}
```

```java
Triple<Request, Response, Result<String, FuelError>> data = bboxapi.setDisplayStateSync(true);

Request request = data.getFirst();
Response response = data.getSecond();
Result<String, FuelError> obj = data.getThird();
System.out.println(response.getStatusCode());
```

Set the Bbox luminosity display either to 0% or 100%

<aside class="warning">This API requires authentication</aside>

## Voip dial

> Asynchronous

```kotlin
bboxapi.voipDial(line = Line.LINE1, phoneNumber = "012345678") { _, res, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println(res.statusCode)
        }
    }
}
```

```java
bboxapi.voipDial(Line.LINE1, "0123456789", new Handler<String>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, String data) {
        System.out.println(response.getStatusCode());
    }
});
```

> Synchronous

```kotlin
val (_, res, result) = bboxapi.voipDialSync(line = Line.LINE1, phoneNumber = "012345678")
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        println(res.statusCode)
    }
}
```

```java
Triple<Request, Response, Result<String, FuelError>> data = bboxapi.voipDialSync(Line.LINE1, "0123456789");

Request request = data.getFirst();
Response response = data.getSecond();
Result<String, FuelError> obj = data.getThird();
System.out.println(response.getStatusCode());
```
> replace `Line.LINE1` with `Line.LINE2` to call on second line

Compose phone number on landline phone on line 1 or line 2 of Bbox. Also called click-to-call, the phone will ring & the call is triggered when someone pick up the phone

<aside class="warning">This API requires authentication</aside>

## Reboot

> Asynchronous

```kotlin
bboxapi.reboot { _, res, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println(res.statusCode)
        }
    }
}
```

```java
bboxapi.reboot(new Handler<String>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, String data) {
        System.out.println(response.getStatusCode());
    }
});
```

> Synchronous

```kotlin
val (_, res, result) = bboxapi.rebootSync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        println(res.statusCode)
    }
}
```

```java
Triple<Request, Response, Result<String, FuelError>> data = bboxapi.rebootSync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<String, FuelError> obj = data.getThird();
System.out.println(response.getStatusCode());
```

Reboot Bbox

<aside class="warning">This API requires authentication</aside>

## Get XDSL info

> Asynchronous

```kotlin
bboxapi.getXdslInfo { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.getXdslInfo(new Handler<List<Wan>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, List<Wan> data) {
        System.out.println(data);
    }
});
```

> Synchronous

```kotlin
val (_, _, result) = bboxapi.getXdslInfoSync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<List<Wan>, FuelError>> data = bboxapi.getXdslInfoSync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<List<Wan>, FuelError> obj = data.getThird();
System.out.println(obj.get());
```

Get information about link type including : 

* state
* modulation
* showtime
* synchronization count

<aside class="success">
This API doesn't require authentication
</aside>

## Get WAN info

> Asynchronous

```kotlin
//asynchronous call
val latch = CountDownLatch(1)
bboxapi.getWanIpInfo { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.getWanIpInfo(new Handler<List<Wan>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, List<Wan> data) {
        System.out.println(data);
    }
});
```

> Synchronous

```kotlin
val (_, _, result) = bboxapi.getWanIpInfoSync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<List<Wan>, FuelError>> data = bboxapi.getWanIpInfoSync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<List<Wan>, FuelError> obj = data.getThird();
System.out.println(obj.get());
```

Information about WAN interface :

* internet state
* ip address
* gateway address
* dns servers
* ipv6
* mac address
* mtu
* link type (ADSL,VDSL,FTTH)

<aside class="success">
This API doesn't require authentication
</aside>

## Set Wifi mac filter state

> Asynchronous

```kotlin
bboxapi.setWifiMacFilter(state = false) { _, res, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println("wifi mac filter enabled ${res.statusCode}")
        }
    }
}
```


```java
bboxapi.setWifiMacFilter(false, new Handler<String>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, String data) {
        System.out.println("wifi mac filter enabled : " + response.getStatusCode());
    }
});
```
> Synchronous

```kotlin
val (_, res, stateResult) = bboxapi.setWifiMacFilterSync(state = false)
when (stateResult) {
    is Result.Failure -> {
        val ex = stateResult.getException()
        println(ex)
    }
    is Result.Success -> {
        println("wifi mac filter enabled ${res.statusCode}")
    }
}
```

```java
Triple<Request, Response, Result<String, FuelError>> result = bboxapi.setWifiMacFilterSync(false);

request = result.getFirst();
response = result.getSecond();
System.out.println("wifi mac filter enabled : " + response.getStatusCode());
```

Enable/Disable Wifi mac filtering

<aside class="warning">This API requires authentication</aside>

## Get Wifi mac filter rules

> Asynchronous

```kotlin
bboxapi.getWifiMacFilter { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.getWifiMacFilter(new Handler<List<Acl>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, List<Acl> data) {
        System.out.println(data);
    }
});
```

> Synchronous

```kotlin
val (_, _, wifiMacFilter) = bboxapi.getWifiMacFilterSync()
when (wifiMacFilter) {
    is Result.Failure -> {
        val ex = wifiMacFilter.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = wifiMacFilter.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<List<Acl>, FuelError>> data = bboxapi.getWifiMacFilterSync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<List<Acl>, FuelError> wifiMacFilter = data.getThird();
System.out.println(wifiMacFilter.get());
```

Retrieve all mac filter rules

<aside class="warning">This API requires authentication</aside>

## Create Wifi mac filter rule

> Asynchronous

```kotlin
val rule1 = Acl.MacFilterRule(enable = true, macaddress = "01:23:45:67:89:01", ip = "")

bboxapi.createMacFilterRule(rule = rule1) { _, res, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println("create rule ${res.statusCode}")
        }
    }
}
```


```java
Acl.MacFilterRule rule1 = new Acl.MacFilterRule(true, "01:23:45:67:89:01", "");

bboxapi.createMacFilterRule(rule1, new Handler<String>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, String data) {
        System.out.println("created rule : " + response.getStatusCode());
    }
});
```
> Synchronous

```kotlin
val rule2 = Acl.MacFilterRule(enable = true, macaddress = "34:56:78:90:12:34", ip = "")

val (_, res2, createResult) = bboxapi.createMacFilterRuleSync(rule = rule2)
when (createResult) {
    is Result.Failure -> {
        val ex = createResult.getException()
        println(ex)
    }
    is Result.Success -> {
        println("create rule ${res2.statusCode}")
    }
}
```

```java
Acl.MacFilterRule rule2 = new Acl.MacFilterRule(true, "34:56:78:90:12:34", "");

Triple<Request, Response, Result<String, FuelError>> createdResult = bboxapi.createMacFilterRuleSync(rule2);
System.out.println("created rule : " + createdResult.getSecond().getStatusCode());
```

Create Wifi mac filter rule with the following information : 

* state (enabled/disabled)
* mac address
* ip address

<aside class="warning">This API requires authentication</aside>

## Update Wifi mac filter rule

> Asynchronous

```kotlin
val rule2 = Acl.MacFilterRule(enable = true, macaddress = "34:56:78:90:12:34", ip = "")

bboxapi.updateMacFilterRule(ruleIndex = 1, rule = rule2) { _, res, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println("updated rule 1 ${res.statusCode}")
        }
    }
}
```


```java
Acl.MacFilterRule rule2 = new Acl.MacFilterRule(true, "34:56:78:90:12:34", "");

bboxapi.updateMacFilterRule(1, rule2, new Handler<String>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, String data) {
        System.out.println("updated rule 1 : " + response.getStatusCode());
    }
});
```
> Synchronous

```kotlin
val rule1 = Acl.MacFilterRule(enable = true, macaddress = "01:23:45:67:89:01", ip = "")

val (_, res, updateResult) = bboxapi.updateMacFilterRuleSync(ruleIndex = 2, rule = rule1)
when (updateResult) {
    is Result.Failure -> {
        val ex = updateResult.getException()
        println(ex)
    }
    is Result.Success -> {
        println("updated rule 2 ${res.statusCode}")
    }
}
```

```java
Acl.MacFilterRule rule1 = new Acl.MacFilterRule(true, "01:23:45:67:89:01", "");

Triple<Request, Response, Result<String, FuelError>> updateResult = bboxapi.updateMacFilterRuleSync(2, rule1);
System.out.println("updated rule 2 : " + updateResult.getSecond().getStatusCode());
```

Update Wifi mac filter with the following properties :

* state (enabled/disabled)
* mac address
* ip address

<aside class="warning">This API requires authentication</aside>

## Delete Wifi mac filter rule

> Asynchronous

```kotlin
bboxapi.deleteMacFilterRule(ruleIndex = 1) { _, res, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println(res.statusCode)
        }
    }
}
```


```java
bboxapi.deleteMacFilterRule(1, new Handler<String>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, String data) {
        System.out.println(response.getStatusCode());
    }
});
```
> Synchronous

```kotlin
val (_, res, result) = bboxapi.deleteMacFilterRuleSync(ruleIndex = 1)
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(res.statusCode)
    }
}
```

```java
Triple<Request, Response, Result<String, FuelError>> result = bboxApi.deleteMacFilterRuleSync(1);

Response response = result.getSecond();
```

Delete wifi mac filter rule by index

<aside class="warning">This API requires authentication</aside>

## Logout

> Asynchronous

```kotlin
bboxapi.logout { _, response, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println(response.statusCode)
        }
    }
}
```

```java
bboxapi.logout(new Handler<byte[]>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, byte[] data) {
        System.out.println(response.getStatusCode());
    }
});
```

> Synchronous

```kotlin
val (_, response, result) = bboxapi.logoutSync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        println(response.statusCode)
    }
}
```

```java
Triple<Request, Response, Result<byte[], FuelError>> data = bboxapi.logoutSync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<byte[], FuelError> obj = data.getThird();
System.out.println(response.getStatusCode());
```

Logout. This will set the authenticated state to `false`

<aside class="success">
This API doesn't require authentication
</aside>

## Start password recovery

> Asynchronous

```kotlin
bboxapi.startPaswordRecovery { _, response, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println(response.statusCode)
        }
    }
}
```

```java
bboxapi.startPaswordRecovery(new Handler<byte[]>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, byte[] data) {
        System.out.println(response.getStatusCode());
    }
});
```

> Synchronous

```kotlin
val (_, response, result) = bboxapi.startPaswordRecoverySync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        println(response.statusCode)
    }
}
```

```java
Triple<Request, Response, Result<byte[], FuelError>> data = bboxapi.startPaswordRecoverySync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<byte[], FuelError> obj = data.getThird();
System.out.println(response.getStatusCode());
```

Start password recovery process. This call is usually followed by a polling on `verifyPasswordRecovery` to check if the user has pressed the Wifi button on Bbox

<aside class="success">
This API doesn't require authentication
</aside>

## Verify password recovery

> Asynchronous

```kotlin
bboxapi.verifyPasswordRecovery { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.verifyPasswordRecovery(new Handler<List<RecoveryVerify>>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, List<RecoveryVerify> data) {
        System.out.println(data);
    }
});
```

> Synchronous

```kotlin
val (_, _, result) = bboxapi.verifyPasswordRecoverySync()
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<List<RecoveryVerify>, FuelError>> data = bboxapi.verifyPasswordRecoverySync();

Request request = data.getFirst();
Response response = data.getSecond();
Result<List<RecoveryVerify>, FuelError> obj = data.getThird();
System.out.println(obj.get());
```

Check the remaining time for the user to push the Wifi button on Bbox to set password. This API will return a `List<RecoveryVerify>` object only if the user has not pressed the button yet. When the user push the button, the next call to `verifyPasswordRecovery` will return an empty String (eg a `null` result) with the authentication cookie.

When an empty response (or a null result) is detected, you may want to reset password with `resetPassword`.

<aside class="success">
This API doesn't require authentication
</aside>

## Reset password

> Asynchronous

```kotlin
bboxapi.resetPassword(password = "123456") { _, res, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println(res.statusCode)
        }
    }
}
```


```java
bboxapi.resetPassword("123456", new Handler<String>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, String data) {
        System.out.println(response.getStatusCode());
    }
});
```
> Synchronous

```kotlin
val (_, res, result) = bboxapi.resetPasswordSync(password = "123456")
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(res.statusCode)
    }
}
```

```java
Triple<Request, Response, Result<String, FuelError>> result = bboxApi.resetPasswordSync("123456");

Response response = result.getSecond();
```

Reset password by setting a new one

<aside class="warning">This API requires authentication</aside>

## Custom HTTP request

> Asynchronous

```kotlin
bboxapi.createCustomRequest(request = Fuel.get("/summary"), auth = false) { _, _, result ->
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = result.get()
            println(data)
        }
    }
}
```

```java
bboxapi.createCustomRequest(Fuel.get("/summary"), false, new Handler<String>() {
    @Override
    public void failure(Request request, Response response, FuelError error) {
        error.printStackTrace();
    }

    @Override
    public void success(Request request, Response response, String data) {
        System.out.println(data);
    }
});
```

> Synchronous

```kotlin
val (_, _, result) = bboxapi.createCustomRequestSync(request = Fuel.get("/voip"), auth = true)
when (result) {
    is Result.Failure -> {
        val ex = result.getException()
        println(ex)
    }
    is Result.Success -> {
        val data = result.get()
        println(data)
    }
}
```

```java
Triple<Request, Response, Result<String, FuelError>> data = bboxapi.createCustomRequestSync(Fuel.get("/voip"), true);
System.out.println(data.getThird().get());
```

Create your own HTTP request, this can be useful for not relying on the library implementation

**All request construction are prefixed with [http://bbox.lan/api/v1](http://bbox.lan/api/v1) if host is not specified**