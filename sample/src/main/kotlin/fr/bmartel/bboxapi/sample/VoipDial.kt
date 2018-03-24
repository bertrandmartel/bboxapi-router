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
    bboxapi.voipDial(line = Voip.Line.LINE1, phoneNumber = "012345678") { _, res, result ->
        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                println(ex)
            }
            is Result.Success -> {
                println(res.statusCode)
            }
        }
        latch.countDown()
    }
    latch.await()

    //synchronous call
    val (_, res, result) = bboxapi.voipDialSync(line = Voip.Line.LINE1, phoneNumber = "012345678")
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            println(res.statusCode)
        }
    }
}