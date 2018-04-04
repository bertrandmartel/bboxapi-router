package fr.bmartel.bboxapi.sample

import com.github.kittinunf.result.Result
import fr.bmartel.bboxapi.BboxApi
import fr.bmartel.bboxapi.model.Acl
import java.util.concurrent.CountDownLatch

fun main(args: Array<String>) {
    val bboxapi = BboxApi()
    bboxapi.password = "admin"

    /**
     * Get wifi mac filter
     */
    //asynchronous call
    var latch = CountDownLatch(1)
    bboxapi.getWifiMacFilter { _, _, result ->
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
    val (_, _, wifiMacFilter) = bboxapi.getWifiMacFilterSync()
    when (wifiMacFilter) {
        is Result.Failure -> {
            val ex = wifiMacFilter.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = wifiMacFilter.get()
            println(data)
        }
    }

    /**
     * enable/disable wifi mac filter
     */
    //asynchronous call
    latch = CountDownLatch(1)
    bboxapi.setWifiMacFilter(state = false) { _, res, result ->
        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                println(ex)
            }
            is Result.Success -> {
                println("wifi mac filter enabled ${res.statusCode}")
            }
        }
        latch.countDown()
    }
    latch.await()

    //synchronous call
    val (_, res, stateResult) = bboxapi.setWifiMacFilterSync(state = false)
    when (stateResult) {
        is Result.Failure -> {
            val ex = stateResult.getException()
            println(ex)
        }
        is Result.Success -> {
            println("wifi mac filter enabled ${res.statusCode}")
        }
    }

    /**
     * delete wifi mac filter rules
     */
    deleteAllRules(bboxapi = bboxapi, size = wifiMacFilter.get()[0].acl?.rules?.size ?: 0)

    showNewSize(bboxapi = bboxapi)

    /**
     * create wifi mac filter rule
     */
    //asynchronous call
    val rule1 = Acl.MacFilterRule(enable = true, macaddress = "01:23:45:67:89:01", ip = "")
    latch = CountDownLatch(1)
    bboxapi.createMacFilterRule(rule = rule1) { _, res, result ->
        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                println(ex)
            }
            is Result.Success -> {
                println("create rule 1 ${res.statusCode}")
            }
        }
        latch.countDown()
    }
    latch.await()

    //synchronous call
    val rule2 = Acl.MacFilterRule(enable = true, macaddress = "34:56:78:90:12:34", ip = "")
    val (_, res2, createResult) = bboxapi.createMacFilterRuleSync(rule = rule2)
    when (createResult) {
        is Result.Failure -> {
            val ex = createResult.getException()
            println(ex)
        }
        is Result.Success -> {
            println("create rule 2 ${res2.statusCode}")
        }
    }

    showNewSize(bboxapi = bboxapi)

    /**
     * update rule
     */
    //asynchronous call
    latch = CountDownLatch(1)
    bboxapi.updateMacFilterRule(ruleIndex = 1, rule = rule2) { _, res, result ->
        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                println(ex)
            }
            is Result.Success -> {
                println("updated rule 1 ${res.statusCode}")
            }
        }
        latch.countDown()
    }
    latch.await()

    //synchronous call
    val (_, res3, updateResult) = bboxapi.updateMacFilterRuleSync(ruleIndex = 2, rule = rule1)
    when (updateResult) {
        is Result.Failure -> {
            val ex = updateResult.getException()
            println(ex)
        }
        is Result.Success -> {
            println("updated rule 2 ${res3.statusCode}")
        }
    }

    showNewSize(bboxapi = bboxapi)

    deleteAllRules(bboxapi = bboxapi, size = 2)
}

fun showNewSize(bboxapi: BboxApi) {
    val (_, _, wifiMacFilter2) = bboxapi.getWifiMacFilterSync()
    when (wifiMacFilter2) {
        is Result.Failure -> {
            val ex = wifiMacFilter2.getException()
            println(ex)
        }
        is Result.Success -> {
            println("new size : ${wifiMacFilter2.get()[0].acl?.rules?.size}")
        }
    }
}

fun deleteAllRules(bboxapi: BboxApi, size: Int) {
    for (i in 1..size) {
        val (_, res, stateResult) = bboxapi.deleteMacFilterRuleSync(ruleIndex =  i)
        when (stateResult) {
            is Result.Failure -> {
                val ex = stateResult.getException()
                println("failed to delete rule with id $i")
                println(ex)
            }
            is Result.Success -> {
                println("deleted rule with id $i")
            }
        }
    }
}