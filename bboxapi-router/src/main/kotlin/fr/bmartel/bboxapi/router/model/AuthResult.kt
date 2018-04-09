package fr.bmartel.bboxapi.router.model

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response

data class AuthResult(val request: Request, val response: Response, val exception: Exception?, val bboxid: String?)
