package fr.bmartel.bboxapi.router.sample

import com.github.kittinunf.result.Result
import fr.bmartel.bboxapi.router.BboxApiRouter
import fr.bmartel.bboxapi.router.model.Scope
import fr.bmartel.bboxapi.router.model.TokenResponse

fun main(args: Array<String>) {

    val bboxapi = BboxApiRouter(clientId = "YOUR_CLIENT_ID", clientSecret = "YOUR_CLIENT_SECRET")

    val (_, response, result) = bboxapi.authenticateOauthButton(
            maxDuration = 20000,
            pollInterval = 1000,
            scope = listOf(Scope.ALL))
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
            println(String(response.data))
        }
        is Result.Success -> {
            val data = result.get() as TokenResponse
            println(data)

            //refresh the token when it expires
            val (_, refreshResp, refreshResult) = bboxapi.refreshTokenSync(
                    refreshToken = data.refresh_token,
                    scope = listOf(Scope.ALL))
            when (refreshResult) {
                is Result.Failure -> {
                    val ex = refreshResult.getException()
                    println(ex)
                    println(String(refreshResp.data))
                }
                is Result.Success -> {
                    val data = refreshResult.get()
                    println(data)
                }
            }
        }
    }
}