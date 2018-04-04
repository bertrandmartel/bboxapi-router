package fr.bmartel.bboxapi.sample

import fr.bmartel.bboxapi.BboxApi


fun main(args: Array<String>) {
    val bboxapi = BboxApi()
    println("push the button on your Bbox for setting your password, you have 20 seconds")
    val state = bboxapi.waitForPushButton(maxDuration = 20000)
    if (state) {
        val setPasswordRes = bboxapi.resetPasswordSync(password = "admin2")
        println("set password : ${setPasswordRes.second.statusCode}")
    }
}