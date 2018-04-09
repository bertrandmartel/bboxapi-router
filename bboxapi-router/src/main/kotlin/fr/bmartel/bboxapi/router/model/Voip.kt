package fr.bmartel.bboxapi.router.model

data class Voip(
        val voip: List<VoipEntry>? = null
)

data class VoipEntry(
        val id: Int? = null,
        val status: String? = null,
        val callstate: CallState? = null,
        val uri: String? = null,
        val blockstate: Int? = null,
        val anoncallstate: Int? = null,
        val mwi: Int? = null,
        val message_count: Int? = null,
        val notanswered: Int? = null
)

enum class CallState {
    Idle, InCall, Ringing, Connecting, Disconnecting, Unknown
}

enum class Line {
    LINE1,
    LINE2
}