package fr.bmartel.bboxapi.router.sample

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import fr.bmartel.bboxapi.router.BboxApiRouter

fun main(args: Array<String>) {
    val bboxapi = BboxApiRouter()
    bboxapi.password = "AAaaa*1111"
    val triple = bboxapi.configureRemoteAccess(state = false)
    if (triple != null) {
        when (triple.third) {
            is Result.Failure -> {
                val ex = (triple.third as Result.Failure<String, FuelError>).getException()
                println(ex)
            }
            is Result.Success -> {
                println(triple.second.statusCode)
            }
        }
    } else {
        println("remote is not activable, change password strength to STRONG")
    }
}