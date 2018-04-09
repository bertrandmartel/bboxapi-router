package fr.bmartel.bboxapi.router

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * from https://github.com/kittinunf/Fuel/blob/master/fuel-android/src/test/kotlin/com/github/kittinunf/fuel/android/BaseTestCase.kt
 */
abstract class TestCase {

    private val DEFAULT_TIMEOUT = 15L

    lateinit var lock: CountDownLatch

    fun await(seconds: Long = DEFAULT_TIMEOUT) {
        lock.await(seconds, TimeUnit.SECONDS)
    }
}