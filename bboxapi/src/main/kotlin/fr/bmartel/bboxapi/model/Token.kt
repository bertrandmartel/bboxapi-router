package fr.bmartel.bboxapi.model

class Token {

    data class Model(
            val device: DeviceToken? = null
    )

    data class DeviceToken(
            val token: String? = null,
            val now: String? = null,
            val expires: String? = null
    )
}