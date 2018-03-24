package fr.bmartel.bboxapi.model

class Device {

    data class Model(
            val device: BboxDevice? = null
    )

    data class BboxDevice(
            val now: String? = null,
            val status: Int? = null,
            val numberofboots: Int? = null,
            val modelname: String? = null,
            val user_configured: Int? = null,
            val display: Display? = null,
            val main: Versionning? = null,
            val reco: Versionning? = null,
            val running: Versionning? = null,
            val bcck: Versionning? = null,
            val ldr1: Versionning? = null,
            val ldr2: Versionning? = null,
            val firstusedate: String? = null,
            val uptime: Int? = null,
            val serialnumber: String? = null,
            val using: DeviceService? = null
    )

    data class Display(
            val luminosity: Int? = null,
            val state: String? = null
    )

    data class Versionning(
            val version: String? = null,
            val date: String? = null
    )

    data class DeviceService(
            val ipv4: Int? = null,
            val ipv6: Int? = null,
            val ftth: Int? = null,
            val adsl: Int? = null,
            val vdsl: Int? = null
    )
}