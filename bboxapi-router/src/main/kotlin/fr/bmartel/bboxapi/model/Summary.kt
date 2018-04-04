package fr.bmartel.bboxapi.model

import com.google.gson.annotations.SerializedName
import fr.bmartel.bboxapi.model.Wan.Internet
import fr.bmartel.bboxapi.model.Voip.CallState

class Summary {

    data class Voip(
            val status: String? = null,
            val callstate: CallState? = null,
            val message: Int? = null,
            var notanswered: Int? = null
    )

    data class Iptv(
            val address: String? = null,
            val ipaddress: String? = null,
            val receipt: Int? = null,
            val number: Int? = null,
            val channels: Int? = null
    )

    data class WirelessStatus(
            val available: Int? = null
    )

    data class Wifi(
            @SerializedName("24") val wifi24: WirelessStatus? = null,
            @SerializedName("5") val wifi5: WirelessStatus? = null,
            val enable: Int? = null,
            val timeout: Int? = null,
            val status: String? = null
    )

    data class Wireless(
            val status: String? = null,
            val radio: Int? = null,
            val changedate: String? = null,
            val wps: Wifi? = null
    )

    data class Upnp(
            val igd: Status? = null
    )

    data class Printer(
            val status: String? = null
    )

    data class Storage(
            val status: String? = null
    )

    data class Usb(
            val printer: List<Printer>? = null,
            val storage: List<Storage>? = null
    )

    data class EnhancedStatus(
            val available: Int? = null,
            val enable: Int? = null,
            val status: String? = null,
            val statusUntil: String? = null,
            val statusRemaining: Int? = null
    )

    data class Status(
            val enable: Int? = null,
            val status: Int? = null
    )

    data class Service(
            val hotspot: Status? = null,
            val firewall: Status? = null,
            val dyndns: Status? = null,
            val dhcp: Status? = null,
            val nat: Status? = null,
            val dmz: Status? = null,
            val natpat: Status? = null,
            val upnp: Upnp? = null,
            val notification: Status? = null,
            val proxywol: Status? = null,
            val remoteweb: Status? = null,
            val parentalcontrol: EnhancedStatus? = null,
            val wifischeduler: EnhancedStatus? = null,
            val samba: Status? = null,
            val printer: Status? = null,
            val dlna: Status? = null
    )

    data class DiagTest(
            val id: Int? = null,
            val status: String? = null
    )

    data class Diag(
            val ring_test: DiagTest? = null,
            val echo_test: DiagTest? = null
    )

    data class Host(
            val hostname: String? = null,
            val ipaddress: String? = null
    )

    data class StatsMeasurement(
            val occupation: Int? = null
    )

    data class ConnectionStats(
            val rx: StatsMeasurement? = null,
            val tx: StatsMeasurement? = null
    )

    data class IpState(
            val ip: Int? = null,
            val ipv6: String? = null
    )

    data class Ip(
            val stats: ConnectionStats? = null,
            val state: IpState? = null
    )

    data class Wan(
            val ip: Ip? = null
    )

    data class Model(
            var now: String? = null,
            var authenticated: Int? = null,
            var display: Device.Display? = null,
            val internet: Internet? = null,
            val voip: List<Voip>? = null,
            val iptv: List<Iptv>? = null,
            val usb: Usb? = null,
            val wireless: Wireless? = null,
            val services: Service? = null,
            val diags: List<Diag>? = null,
            val hosts: List<Host>? = null,
            val wan: Wan? = null
    )
}