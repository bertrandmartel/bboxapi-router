# Bbox Api client library #
http://akinaru.github.io/bbox-api-client

<i>Last update on 23/09/2015</i>

Library for using Bbox Json API opened in end of september 2015 on Bbox Sensation

These apis are used by Bbox management interface in hostname : gestionbbox.lan port 80

You must be on the same network as your Bbox to use them

last release : https://github.com/akinaru/bbox-api-client/releases/tag/1.0

This README may contain inaccurate information about these api due to early release.

<h3>List of API implemented</h3>

| api     | prototype        |   access      | comment
|--------------|---------|-----|------------------------|
| summary | ``getSummary(IApiSummaryListener listener)`` |  public    | some information about Bbox      |
| authentication | ``authenticate(String password, IAuthenticationListener authenticationListener)`` |  public    | authenticate to be able to use private API      |
| voip | ``voipData(IVoipDataListener voipDataListener)`` |  private    | request voip data      |
| device information | ``bboxDevice(IBboxDeviceListener deviceListener)`` |  private    | request specific information about box      |
| full call log | ``getFullCallLog(IFullCallLogListener listener)`` |  private    | get call log since last reboot      |
| hosts | ``getHosts(IHostsListener listener)`` |  private    | request list of known hosts      |
| set wifi state | ``setWifiState(boolean state, IRequestStatusListener requestStatus)`` |  private    | set Wifi ON/OFF      |
| set led state | ``setBboxDisplayState(boolean state, IRequestStatusListener requestStatus)`` |  private    | set Bbox led display ON/OFF      |
| dial | ``voipDial(int lineNumber, String phone, IRequestStatusListener requestStatus)`` |  private    | dial a phone number on a line      |

<h3>How to use ?</h3>

Instanciate BboxApi class

```
import fr.bmartel.bboxapi.BboxApi;

.....

BboxApi apiWrapper = new BboxApi();
```

<h3>Retrieve summary info</h3>

Summary information contains basic information about Bbox

Request summary information (no authentication required)

```
apiWrapper.getSummary(new IApiSummaryListener() {
	
	@Override
	public void onApiSummaryReceived(ApiSummary summary) {
		
		//summary result received
		
	}
	
	@Override
	public void onApiSummaryFailure() {
		
		//summary request failure
		
	}
});
```

``ApiSummary`` object description 

| property             | Type              | comment
|--------------|--------------|------------------------|
|``rxOccupation`` | int    | RX occupation (in %)      |
|``txOccupation`` | int    | TX occupation (in %)      |
|``hostList`` | List<Host>    | List of hosts (see host api)      |
|``iptvAddr`` | String    | IPTV broadcast address      |
|``iptvIpAddr`` | String    | IPTV receiver address      |
|``iptvReceipt`` | int    | -      |
|``iptvNumber`` | int    | -     |
|``voipStatus`` | String    | voip status ("Up" if online)  |
|``callState`` | CallState    | voip callstate (IDLE,INCALL,OFFHOOK,OUTCALL)  |
|``message`` | int    | number of vocal message  |
|``notanswered`` | int    | number of not answered call  |
|``internetState`` | int    | -  |
|``authenticated`` | int    | 0 if not authenticated / 1 if authenticated |
|``displayState`` | boolean    | true if Bbox led is ON / false for OFF |

<h3>Authentication</h3>

To request private api, you have to authenticate with your bbox management interface password :

```
api.authenticate("your_password", new IAuthenticationListener() {

	@Override
	public void onAuthenticationSuccess(String token) {
		
		//successfull registration
		
	}

	@Override
	public void onAuthenticationError() {
		
		//failure registration
	}
	
});
```

If registration is successful, token is stored in RAM in Bboxapi object. Further call to BboxApi object will integrate a Cookie header with received token.

<h3>Retrieve Bbox device info</h3>

Some information about Bbox : 

```
api.bboxDevice(new IBboxDeviceListener() {

	@Override
	public void onBboxDeviceReceived(BBoxDevice device) {
		
		//Bbox device request successfulll
	}

	@Override
	public void onBboxDeviceFailure() {

		//Bbox device request failure
	}

});
```

``BBoxDevice`` object description 

| property             | Type              | comment
|--------------|--------------|------------------------|
|``status`` | int    | -      |
|``bootNumber`` | int    | number of boot  |
|``modelName`` | String    | Bbox model name  |
|``userConfigured`` | boolean    | define if user has already logged before  |
|``displayState`` | boolean    | true if Bbox led is ON / false for OFF |
|``firstuseDate`` | String    | date of bbox first use |
|``serialNumber`` | String    | bbox serial number |

<h3>Retrieve known host list</h3>

Get list of all host known by Bbox with firstseen and lastseen date

```
api.getHosts(new IHostsListener() {
					
	@Override
	public void onHostsReceived(List<Host> hostList) {
		
		//host request successfull
	}
	
	@Override
	public void onHostsFailure() {
		
		//host request failure
		
	}
});
```

Result is a list of ``Host`` object define as following :

| property             | Type              | comment
|--------------|--------------|------------------------|
|``id`` | int    | host id (begin from 1)      |
|``hostname`` | String    | host name     |
|``macaddress`` | String    | host mac address      |
|``ipaddress`` | String    | host IP address      |
|``type`` | String    | "Static" or "STB"      |
|``link`` | String    | type of link for this host (Wifi 2.4 / Wifi 5 / Offline)    |
|``devicetype`` | String    | "Device" or "STB"      |
|``firstseen`` | String    | host was first seen on this date      |
|``lastseen`` | String    | host was last seen on this date     |
|``lease`` | int    | lease time for this host     |
|``active`` | boolean    |  define if host is active |

<h3>Retrieve voip data</h3>

Get voip information

```
api.voipData(new IVoipDataListener() {

	@Override
	public void onVoipDataReceived(Voip voipData) {
		
		//voip request successfull

	}

	@Override
	public void onVoipDataFailure() {
		
		//voip request failure

	}
	
});
```

Result is a list of ``Voip`` object define as following :

| property             | Type              | comment
|--------------|--------------|------------------------|
|``id`` | int    | -      |
|``status`` | String    | voip status ("Up" if online)  |
|``callState`` | CallState    | voip callstate (IDLE,INCALL,OFFHOOK,OUTCALL)  |
|``uri`` | String    | SIP phone line |
|``blockState`` | int    | number of blocked call (to verify) |
|``anoncallState`` | int    | number of anonymous call |
|``mwi`` | int    | number of message waiting |
|``messageCount`` | int    | number of messages |
|``notanswered`` | int    | number of call not answered |

<h3>Retrieve list of call log</h3>

Get full list of call log since last reboot

```
api.getFullCallLog(new IFullCallLogListener() {

	@Override
	public void onFullCallLogReceived(List<CallLog> callLogList) {
		
		//call log request successfull

	}

	@Override
	public void onFullCallLogFailure() {
		
		//call log request failure
	}

});
```

Result is a list of ``CallLog`` object define as following :

| property             | Type              | comment
|--------------|--------------|------------------------|
|``id`` | int    | call id      |
|``number`` | String    | phone number     |
|``date`` | long    | call date    |
|``type`` | CallType    | call type (INCALL / OUTCALL)    |
|``answered`` | boolean    | define if call was answered or not    |
|``duration`` | int    | call duration (answered or not)  |

<hr/>

<h3>Dial a phone number - Trigger phone ring before call</h3>

Dial a specified phone number. Phone will ring and call will be processed once user hookoff

```
api.voipDial(1,"0666666666",new IRequestStatusListener() {
	
	@Override
	public void onSuccess() {
		
		//voip dial success

	}
	
	@Override
	public void onFailure() {
		
		//voip dial failure

	}
});
```

Input  :

* line number (int) : 1 or 2 according to the line on which you plugged your phone
* phone number (String) : number to call
* task completion listener (IRequestStatusListener) : retrieve asynchronous task completion or failure

<h3>Set Bbox led state</h3>


Switch led display to ON / OFF on Bbox 

```
api.setBboxDisplayState(true, new IRequestStatusListener() {
					
	@Override
	public void onSuccess() {
		//box display set success
	}
	
	@Override
	public void onFailure() {
		//box display set failure
	}
});
```

Input : 
* display state (boolean) 
* task completion listener (IRequestStatusListener) :  retrieve asynchronous task completion or failure


<h3>Set Wifi state</h3>

Switch Wifi to ON/OFF 

```
api.setWifiState(true, new IRequestStatusListener() {
	
	@Override
	public void onSuccess() {
		//box wifi set status success
	}
	
	@Override
	public void onFailure() {
		//box wifi set status failure
	}

});
```

Input : 
* wifi state (boolean) 
* task completion listener (IRequestStatusListener) :  retrieve asynchronous task completion or failure

<hr/>

<h2>Android integration</h2>

To integrate with Android add Internet permission to manifest : 
```
<uses-permission android:name="android.permission.INTERNET" />
```

To include jar :
```
repositories {
    flatDir {
        dirs 'libs'
    }
}
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
}
```

<h2>Testing Bbox APIs</h2>

* You can test all previous API in java project bbox-api-client-test :

```
cd ./bbox-api-client-test/release
java -jar bbox-api-client-test-1.0.jar
```

* You can test Bbox API with a Linux Bash script ``bboxapi-curl.sh`` script accomplishing authentication, request voip data and dial a number.

Usage :
```
./bboxapi-curl.sh <your_password> <phone_number>
```

<hr/>

<b>External JAVA Library</b>

* json-simple  : http://code.google.com/p/json-simple/

* clientsocket : https://github.com/akinaru/socket-multiplatform/tree/master/client/socket-client/java

* http-endec   : https://github.com/akinaru/http-endec-java

<b>TODO</b>

* authentication session timeout
* more APIs left to implement
