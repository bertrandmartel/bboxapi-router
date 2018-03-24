package fr.bmartel.bboxapi.sample

import com.github.kittinunf.result.Result
import fr.bmartel.bboxapi.BboxApi
import fr.bmartel.bboxapi.model.Voip
import java.util.concurrent.CountDownLatch

fun main(args: Array<String>) {
    val bboxapi = BboxApi()
    bboxapi.setPassword("admin")

    //asynchronous call
    val latch = CountDownLatch(1)
    bboxapi.getCallLogs(line = Voip.Line.LINE1) { _, _, result ->
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
    val (_, _, result) = bboxapi.getCallLogsSync(line = Voip.Line.LINE1)
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