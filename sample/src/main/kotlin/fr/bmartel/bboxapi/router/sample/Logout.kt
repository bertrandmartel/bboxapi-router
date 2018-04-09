package fr.bmartel.bboxapi.router.sample

import com.github.kittinunf.result.Result
import fr.bmartel.bboxapi.router.BboxApi
import java.util.concurrent.CountDownLatch


fun main(args: Array<String>) {
    val bboxapi = BboxApi()

    //asynchronous call
    val latch = CountDownLatch(1)
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
        latch.countDown()
    }
    latch.await()

    //synchronous call
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
}