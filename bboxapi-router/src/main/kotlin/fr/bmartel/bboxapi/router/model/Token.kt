package fr.bmartel.bboxapi.router.model

data class Token(
        val device: DeviceToken? = null
)

data class DeviceToken(
        val token: String? = null,
        val now: String? = null,
        val expires: String? = null
)