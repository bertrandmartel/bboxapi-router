package fr.bmartel.bboxapi.model

class BboxException {

    data class Model(
            val exception: ApiException? = null
    )

    data class ApiException(
            val domain: String? = null,
            val code: String? = null,
            val errors: List<ApiError>? = null
    )

    data class ApiError(
            val name: String? = null,
            val reason: String? = null
    )
}