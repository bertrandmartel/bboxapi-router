package fr.bmartel.bboxapi.model

import com.google.gson.annotations.SerializedName

class Wireless {

    data class Model(
            val wireless: Wireless? = null
    )

    data class Wireless(
            val status: String? = null,
            val radio: Radio? = null,
            val scheduler: Scheduler? = null,
            val ssid: Ssid? = null,
            val capabilities: WirelessCapability? = null,
            val standard: WirelessStandard? = null
    )

    data class Radio(
            @SerializedName("24") val wifi24: RadioItem? = null,
            @SerializedName("5") val wifi5: RadioItem? = null
    )

    data class RadioItem(
            val enable: Int? = null,
            val standard: String? = null,
            val state: Int? = null,
            val dfs: Int? = null,
            val channel: Int? = null,
            val current_channel: Int? = null,
            val ht40: WirelessFeature? = null
    )

    data class WirelessFeature(
            val enable: Int? = null
    )

    data class Scheduler(
            val now: String? = null,
            val enable: Int? = null
    )

    data class Ssid(
            @SerializedName("24") val wifi24: SsidItem? = null,
            @SerializedName("5") val wifi5: SsidItem? = null
    )

    data class SsidItem(
            val id: String? = null,
            val enable: Int? = null,
            val hidden: Int? = null,
            val bssid: String? = null,
            val wmmenable: Int? = null,
            val wps: WirelessStatus? = null,
            val security: WirelessSecurity? = null
    )

    data class WirelessStatus(
            val enable:Int? = null,
            val available:Int? = null,
            val status:String? = null
    )

    data class WirelessSecurity(
            val isdefault: Int? = null,
            val protocol: String? = null,
            val encryption: String? = null,
            val passphrase: String? = null
    )

    data class WirelessStandard(
            @SerializedName("24") val wifi24: List<WirelessValuePairs>? = null,
            @SerializedName("5") val wifi5: List<WirelessValuePairs>? = null
    )

    data class WirelessValuePairs(
            val key: String? = null,
            val value: String? = null
    )

    data class WirelessCapability(
            @SerializedName("24") val wifi24: List<WirelessCapabilityItem>? = null,
            @SerializedName("5") val wifi5: List<WirelessCapabilityItem>? = null
    )

    data class WirelessCapabilityItem(
            val channel: Int? = null,
            val ht40: String? = null,
            val nodfs: Boolean? = null,
            val cactime: Int? = null,
            val cactime40: Int? = null
    )
}