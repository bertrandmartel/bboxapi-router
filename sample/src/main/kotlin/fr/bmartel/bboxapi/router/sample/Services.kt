package fr.bmartel.bboxapi.router.sample

import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.result.Result
import fr.bmartel.bboxapi.router.BboxApiRouter
import java.net.UnknownHostException
import java.util.concurrent.CountDownLatch

fun main(args: Array<String>) {
    val bboxapi = BboxApiRouter()
    bboxapi.init()

    //asynchronous call
    val latch = CountDownLatch(1)
    bboxapi.getServices { _, response, result ->
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
    val (_, _, result) = bboxapi.getServicesSync()
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

    //check if remote access to API is activable eg password is strong enough
    println("remote access activable : ${bboxapi.isRemoteActivable()}")
}