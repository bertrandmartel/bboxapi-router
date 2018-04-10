package fr.bmartel.bboxapi.router

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import fr.bmartel.bboxapi.router.BboxApiTest.Companion.fromJson
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Assert.*
import org.skyscreamer.jsonassert.JSONAssert
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.UnknownHostException
import java.util.*


class TestUtils {

    companion object {

        @Throws(UnsupportedEncodingException::class)
        fun splitQuery(query: String): Map<String, List<String>> {
            val query_pairs = LinkedHashMap<String, List<String>>()
            val pairs = query.split("&")
            for (pair in pairs) {
                val idx = pair.indexOf("=")
                val key = if (idx > 0) URLDecoder.decode(pair.substring(0, idx), "UTF-8") else pair
                if (!query_pairs.containsKey(key)) {
                    query_pairs.put(key, LinkedList())
                }
                val value = if (idx > 0 && pair.length > idx + 1) URLDecoder.decode(pair.substring(idx + 1), "UTF-8") else null
                (query_pairs[key] as LinkedList<String>).add(value ?: "")
            }
            return query_pairs
        }

        fun getResFile(fileName: String): String {
            val classLoader = javaClass.classLoader
            return File(classLoader.getResource(fileName).file).readText()
        }

        fun <T> executeSync(filename: String?, body: () -> T, expectedException: Exception? = null) {
            val (_, response, result) = body() as Triple<Request, Response, *>
            var error: FuelError? = null
            var resulData: Any? = null
            if (result != null) {
                val (data, err) = result as Result<*, *>
                resulData = data
                if (err != null && err is FuelError) {
                    error = err
                }
            }
            checkSyncResult(filename = filename, response = response, data = resulData, err = error, expectedException = expectedException)
        }

        fun <T, Y> executeSyncOneParam(filename: String?, input: Y, body: (input: Y) -> T, expectedException: Exception? = null) {
            val (_, response, result) = body(input) as Triple<Request, Response, *>
            val (data, err) = result as Result<*, FuelError>
            checkSyncResult(filename = filename, response = response, data = data, err = err, expectedException = expectedException)
        }

        fun <T, Y, Z> executeSyncTwoParam(filename: String?,
                                          input1: Y,
                                          input2: Z,
                                          body: (input1: Y, input2: Z) -> T, expectedException: Exception? = null) {
            val (_, response, result) = body(input1, input2) as Triple<Request, Response, *>
            val (data, err) = result as Result<*, FuelError>
            checkSyncResult(filename = filename, response = response, data = data, err = err, expectedException = expectedException)
        }

        private fun <T> checkSyncResult(filename: String?, response: Response, data: T, err: FuelError?, expectedException: Exception?) {
            if (expectedException != null) {
                checkFullError(fuelError = err, expectedException = expectedException)
            } else {
                assertEquals(200, response.statusCode)
                MatcherAssert.assertThat(err?.exception, CoreMatchers.nullValue())
                //MatcherAssert.assertThat(data, CoreMatchers.notNullValue())
                if (filename != null) {
                    JSONAssert.assertEquals(getResFile(fileName = filename), Gson().toJson(data), false)
                }
            }
        }

        fun <T> executeAsync(testcase: TestCase, filename: String?, body: (handler: (Request, Response, Result<*, *>?) -> Unit) -> T, expectedException: Exception? = null) {
            var request: Request? = null
            var response: Response? = null
            var data: Any? = null
            var err: FuelError? = null
            body { req, res, result ->
                request = req
                response = res
                if (result != null) {
                    val (d, e) = result
                    data = d
                    if (e != null && e is FuelError) {
                        err = e
                    }
                }
                testcase.lock.countDown()
            }
            testcase.await()
            checkAsyncResult(
                    filename = filename,
                    request = request,
                    response = response,
                    data = data,
                    err = err,
                    expectedException = expectedException)
        }

        fun <T> executeAsyncCb(testcase: TestCase, filename: String?, body: (handler: Handler<T>) -> T, expectedException: Exception? = null) {
            var request: Request? = null
            var response: Response? = null
            var data: Any? = null
            var err: FuelError? = null

            body(object : Handler<T> {
                override fun failure(req: Request, res: Response, e: FuelError) {
                    request = req
                    response = res
                    err = e
                    testcase.lock.countDown()
                }

                override fun success(req: Request, res: Response, d: T) {
                    request = req
                    response = res
                    data = d
                    testcase.lock.countDown()
                }
            })
            testcase.await()
            checkAsyncResult(
                    filename = filename,
                    request = request,
                    response = response,
                    data = data,
                    err = err,
                    expectedException = expectedException)
        }

        fun <T, Y> executeAsyncOneParam(testcase: TestCase, input: Y, filename: String?, body: (input: Y, handler: (Request, Response, Result<*, FuelError>) -> Unit) -> T, expectedException: Exception? = null) {
            var request: Request? = null
            var response: Response? = null
            var data: Any? = null
            var err: FuelError? = null
            body(input) { req, res, result ->
                request = req
                response = res
                val (d, e) = result
                data = d
                err = e
                testcase.lock.countDown()
            }
            testcase.await()
            checkAsyncResult(
                    filename = filename,
                    request = request,
                    response = response,
                    data = data,
                    err = err,
                    expectedException = expectedException)
        }

        fun <T, Y> executeAsyncOneParamCb(testcase: TestCase, input: Y, filename: String?, body: (input: Y, handler: Handler<T>) -> T, expectedException: Exception? = null) {
            var request: Request? = null
            var response: Response? = null
            var data: Any? = null
            var err: FuelError? = null
            body(input, object : Handler<T> {
                override fun failure(req: Request, res: Response, e: FuelError) {
                    request = req
                    response = res
                    err = e
                    testcase.lock.countDown()
                }

                override fun success(req: Request, res: Response, d: T) {
                    request = req
                    response = res
                    data = d
                    testcase.lock.countDown()
                }
            })
            testcase.await()
            checkAsyncResult(
                    filename = filename,
                    request = request,
                    response = response,
                    data = data,
                    err = err,
                    expectedException = expectedException)
        }

        fun <T, Y, Z> executeAsyncTwoParam(testcase: TestCase,
                                           input1: Y,
                                           input2: Z,
                                           filename: String?,
                                           body: (line: Y, input2: Z, handler: (Request, Response, Result<*, FuelError>) -> Unit) -> T, expectedException: Exception? = null) {
            var request: Request? = null
            var response: Response? = null
            var data: Any? = null
            var err: FuelError? = null
            body(input1, input2) { req, res, result ->
                request = req
                response = res
                val (d, e) = result
                data = d
                err = e
                testcase.lock.countDown()
            }
            testcase.await()
            checkAsyncResult(
                    filename = filename,
                    request = request,
                    response = response,
                    data = data,
                    err = err,
                    expectedException = expectedException)
        }

        fun <T, Y, Z> executeAsyncTwoParamCb(testcase: TestCase,
                                             input1: Y,
                                             input2: Z,
                                             filename: String?,
                                             body: (line: Y, input2: Z, handler: Handler<T>) -> T, expectedException: Exception? = null) {
            var request: Request? = null
            var response: Response? = null
            var data: Any? = null
            var err: FuelError? = null
            body(input1, input2, object : Handler<T> {
                override fun failure(req: Request, res: Response, e: FuelError) {
                    request = req
                    response = res
                    err = e
                    testcase.lock.countDown()
                }

                override fun success(req: Request, res: Response, d: T) {
                    request = req
                    response = res
                    data = d
                    testcase.lock.countDown()
                }
            })
            testcase.await()
            checkAsyncResult(
                    filename = filename,
                    request = request,
                    response = response,
                    data = data,
                    err = err,
                    expectedException = expectedException)
        }

        private fun checkAsyncResult(filename: String?,
                                     request: Request?,
                                     response: Response?,
                                     data: Any?,
                                     err: FuelError?,
                                     expectedException: Exception?) {
            MatcherAssert.assertThat(request, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(response, CoreMatchers.notNullValue())

            if (expectedException != null) {
                checkFullError(fuelError = err, expectedException = expectedException)
            } else {
                assertEquals(200, response?.statusCode)
                MatcherAssert.assertThat(err?.exception, CoreMatchers.nullValue())
                //MatcherAssert.assertThat(data, CoreMatchers.notNullValue())
                if (filename != null) {
                    JSONAssert.assertEquals(getResFile(fileName = filename), Gson().toJson(data), false)
                }
            }
        }

        inline fun <reified U> checkCustomResponse(testcase: TestCase,
                                                   inputReq: Request,
                                                   auth: Boolean,
                                                   filename: String?,
                                                   expectedException: Exception?,
                                                   body: (Request, Boolean, handler: (Request, Response, Result<*, FuelError>) -> Unit) -> Unit) {
            var request: Request? = null
            var response: Response? = null
            var data: Any? = null
            var err: FuelError? = null
            body(inputReq, auth) { req, res, result ->
                request = req
                response = res
                val (d, e) = result
                data = d
                err = e
                testcase.lock.countDown()
            }
            testcase.await()
            checkFuelResponseResult<U>(filename = filename,
                    request = request,
                    response = response,
                    data = data,
                    err = err,
                    expectedException = expectedException)
        }

        inline fun <reified U> checkCustomResponseCb(testcase: TestCase,
                                                     inputReq: Request,
                                                     auth: Boolean,
                                                     filename: String?,
                                                     expectedException: Exception?,
                                                     body: (Request, Boolean, handler: Handler<String>) -> Unit) {
            var request: Request? = null
            var response: Response? = null
            var data: Any? = null
            var err: FuelError? = null
            body(inputReq, auth, object : Handler<String> {
                override fun failure(req: Request, res: Response, e: FuelError) {
                    request = req
                    response = res
                    err = e
                    testcase.lock.countDown()
                }

                override fun success(req: Request, res: Response, d: String) {
                    request = req
                    response = res
                    data = d
                    testcase.lock.countDown()
                }
            })
            testcase.await()
            checkFuelResponseResult<U>(filename = filename,
                    request = request,
                    response = response,
                    data = data,
                    err = err,
                    expectedException = expectedException)
        }

        inline fun <reified U> checkCustomResponseSync(
                inputReq: Request,
                auth: Boolean,
                filename: String?,
                expectedException: Exception?,
                body: (Request, Boolean) -> Triple<Request, Response, *>) {
            val (request, response, result) = body(inputReq, auth)
            val (data, err) = result as Result<*, FuelError>

            checkFuelResponseResult<U>(filename = filename,
                    request = request,
                    response = response,
                    data = data,
                    err = err,
                    expectedException = expectedException)
        }

        inline fun <reified U> checkFuelResponseResult(filename: String?,
                                                       request: Request?,
                                                       response: Response?,
                                                       data: Any?,
                                                       err: FuelError?,
                                                       expectedException: Exception?) {
            Assert.assertNotNull(request)
            Assert.assertNotNull(response)
            if (expectedException != null) {
                Assert.assertNull(data)
                checkFullError(fuelError = err, expectedException = expectedException)
            } else {
                Assert.assertNull(err)
                Assert.assertNotNull(data)
                if (filename != null) {
                    JSONAssert.assertEquals(
                            getResFile(fileName = filename),
                            Gson().toJson(Gson().fromJson<U>(data as String)), false)
                }
            }
        }

        fun checkFullError(fuelError: FuelError?, expectedException: Exception) {
            MatcherAssert.assertThat(fuelError, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(fuelError?.exception, CoreMatchers.notNullValue())
            if (expectedException is BboxApiRouter.BboxAuthException) {
                assertTrue(fuelError?.exception is BboxApiRouter.BboxAuthException)
                val exception = fuelError?.exception as BboxApiRouter.BboxAuthException
                Assert.assertEquals(exception.error.exception?.code, exception.error.exception?.code)
                Assert.assertEquals(exception.error.exception?.domain, exception.error.exception?.domain)
            } else if (expectedException is UnknownHostException) {
                assertTrue(fuelError?.exception is UnknownHostException)
            } else if (expectedException is HttpException) {
                assertTrue(fuelError?.exception is HttpException)
                val exception = fuelError?.exception as HttpException
                assertEquals(exception.message, fuelError.exception.message)
            } else if (expectedException is JsonSyntaxException) {
                assertTrue(fuelError?.exception is JsonSyntaxException)
            } else {
                fail("unchecked exception : $fuelError")
            }
        }
    }
}