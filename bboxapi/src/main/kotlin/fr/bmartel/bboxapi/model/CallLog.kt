package fr.bmartel.bboxapi.model

import com.google.gson.annotations.SerializedName

class CallLog {

    data class Model(
            val calllog: List<CallLogEntry>? = null
    )

    data class CallLogEntry(
            val id: Int? = null,
            val number: String? = null,
            val date: Long? = null,
            val type: CallType? = null,
            val answered: Int? = null,
            val duree: Int? = null
    )

    enum class CallType {
        @SerializedName("in")
        IN_CALL,
        @SerializedName("out")
        OUT_CALL
    }
}