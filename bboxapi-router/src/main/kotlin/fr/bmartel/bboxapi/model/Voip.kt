package fr.bmartel.bboxapi.model

class Voip {

    data class Model(
            val voip: List<Voip>? = null
    )

    data class Voip(
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
}