package fr.bmartel.bboxapi.router.sample

import com.github.kittinunf.result.Result
import fr.bmartel.bboxapi.router.BboxApiRouter
import java.util.concurrent.CountDownLatch

fun main(args: Array<String>) {
    val bboxapi = BboxApiRouter()

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
        latch.countDown()
    }
    latch.await()

    //synchronous call
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
}