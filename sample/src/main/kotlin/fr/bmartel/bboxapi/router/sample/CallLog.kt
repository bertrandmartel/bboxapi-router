package fr.bmartel.bboxapi.router.sample

import com.github.kittinunf.result.Result
import fr.bmartel.bboxapi.router.BboxApi
import fr.bmartel.bboxapi.router.model.Line
import java.util.concurrent.CountDownLatch

fun main(args: Array<String>) {
    val bboxapi = BboxApi()
    bboxapi.password = "admin"

    //asynchronous call
    val latch = CountDownLatch(1)
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
        latch.countDown()
    }
    latch.await()

    //synchronous call
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
}