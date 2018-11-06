package fr.bmartel.bboxapi.router.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.annotations.SerializedName
import fr.bmartel.bboxapi.router.BboxApiUtils

data class Hosts(
        val hosts: HostList? = null
) {
    class Deserializer : ResponseDeserializable<List<Hosts>> {
        override fun deserialize(content: String) = BboxApiUtils.fromJson<List<Hosts>>(content)
    }
}

data class HostList(
        val list: List<Host>? = null
)

data class Host(
        val id: Int? = null,
        var hostname: String? = null,
        val macaddress: String? = null,
        val ipaddress: String? = null,
        val type: String? = null,
        val link: String? = null,
        val devicetype: String? = null,
        val firstseen: String? = null,
        val lastseen: String? = null,
        val ip6address: List<IpAdress>? = null,
        val lease: Int? = null,
        val active: Int? = null,
        val scan: Scan? = null,
        val ethernet: HostEthernet? = null,
        val wireless: HostWireless? = null,
        val plc: HostPlc? = null,
        val parentalcontrol: HostParentalControl? = null,
        val ping: HostPing? = null
)

data class HostEthernet(
        val physicalport: Int? = null,
        val logicalport: Int? = null,
        val speed: Int? = null,
        val mode: String? = null
)

data class HostWireless(
        val band: String? = null,
        val rssi0: Int? = null,
        val rssi1: Int? = null,
        val rssi2: Int? = null,
        val mcs: Int? = null,
        val rate: String? = null,
        val idle: Int? = null,
        val wexindex: Int? = null,
        val starealmac: String? = null
)

data class HostPlc(
        val rxphyrate: String? = null,
        val txphyrate: String? = null,
        val associateddevice: Int? = null,
        @SerializedName("interface") val hostInterface: Int? = null,
        val ethernetspeed: Int? = null
)

data class HostParentalControl(
        val enable: Int? = null,
        val status: String? = null,
        val statusRemaining: Int? = null,
        val statusUntil: String? = null
)

data class HostPing(
        val average: Int? = null
)

data class Scan(
        val services: List<ScanService>? = null
)

data class ScanService(
        val status: String? = null
)

data class IpAdress(
        val ipaddress: String? = null,
        val status: String? = null,
        val lastseen: String? = null,
        val lastscan: String? = null,
        val valid: String? = null,
        val preferred: String? = null,
        val prefix: String? = null
)