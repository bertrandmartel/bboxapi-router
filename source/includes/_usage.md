# Usage

## Gradle

Dependency is available on JCenter or Maven Central
with Gradle, from JCenter or MavenCentral :

```groovy
repositories {
    jcenter() //or mavenCentral()
}

dependencies {
    compile 'fr.bmartel:bboxapi-router:2.0.1' //for JVM
    compile 'fr.bmartel:bboxapi-router-android:2.0.1' //for Android
}
```

## Synchronous & Asynchronous

All methods have an asynchronous & synchronous version. The synchronous version is suffixed with `Sync` :

* `getSummary` is asynchronous, result is returned in callback function
* `getSummarySync` is synchronous, it directly returns the result

## Format

The format is the same as the one used in [Fuel](https://github.com/kittinunf/Fuel) HTTP client library

* Asynchronous method

The callback function is given in parameter, a `Request` & `Response` objects are returned along with a [Result object](https://github.com/kittinunf/Result) holding the data and a `FuelError` object in case of failure.

For instance, for `getSummary` it will return `(Request, Response, Result<List<Summary>, FuelError>)`

In case of Java, an `Handler` function is used that have 2 callbacks : `onSuccess` & `onError`

* Synchronous method :

A `Triple<Request, Response, T>` object is returned.