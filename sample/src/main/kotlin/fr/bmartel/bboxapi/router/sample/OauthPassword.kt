package fr.bmartel.bboxapi.router.sample

import com.github.kittinunf.result.Result
import fr.bmartel.bboxapi.router.BboxApiRouter
import fr.bmartel.bboxapi.router.model.Scope
import java.util.concurrent.CountDownLatch

fun main(args: Array<String>) {

    val bboxapi = BboxApiRouter(clientId = "client_id_test", clientSecret = "client_secret_test")
    bboxapi.init()
    bboxapi.password = "admin"

    val token = bboxapi.authenticateOauthPassword(scope = listOf(Scope.ALL))
    if (token != null) {
        println(token)
        //asynchronous call
        val latch = CountDownLatch(1)
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
            latch.countDown()
        }
        latch.await()

        //synchronous call
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
    } else {
        println("error occured")
    }
}