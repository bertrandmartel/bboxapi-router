package fr.bmartel.bboxapi.router.sample

import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.result.Result
import fr.bmartel.bboxapi.router.BboxApi
import java.net.UnknownHostException
import java.util.concurrent.CountDownLatch

fun main(args: Array<String>) {
    val bboxapi = BboxApi()

    println("authentication attempts : ${bboxapi.attempts}")
    println("user is authenticated   : ${bboxapi.authenticated}")
    println("user is blocked         : ${bboxapi.blocked}")
    println("ban expiration date     : ${bboxapi.blockedUntil}")

    //asynchronous call
    val latch = CountDownLatch(1)
    bboxapi.getSummary { _, response, result ->
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
        latch.countDown()
    }
    latch.await()

    //synchronous call
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
}