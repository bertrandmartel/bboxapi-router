package fr.bmartel.bboxapi.router.model

import com.google.gson.annotations.SerializedName

data class Wan(
        val wan: WanEntry? = null
)

data class WanEntry(
        val xdsl: Xdsl? = null,
        val internet: Internet? = null,
        @SerializedName("interface") val wanInterface: Interface? = null,
        val ip: WanIp? = null,
        val link: WanLink? = null
)

data class Xdsl(
        val state: String? = null,
        val modulation: String? = null,
        val showtime: Int? = null,
        val atur_provider: String? = null,
        val atuc_provider: String? = null,
        val sync_count: Int? = null,
        val up: Link? = null,
        val down: Link? = null
)

data class Link(
        val bitrates: Int? = null,
        val noise: Int? = null,
        val attenuation: Int? = null,
        val power: Int? = null,
        val phyr: Int? = null,
        val ginp: Int? = null,
        val nitro: Any? = null,
        val interleave_delay: Int? = null
)

data class Interface(
        val state: Int? = null,
        val id: Int? = null,
        val default: Int? = null
)

data class WanIp(
        val address: String? = null,
        val state: String? = null,
        val gateway: String? = null,
        val dnsservers: String? = null,
        val subnet: String? = null,
        val ip6state: String? = null,
        val ip6address: List<IpAdress>? = null,
        val ip6prefix: List<IpAdress>? = null,
        val mac: String? = null,
        val mtu: Int? = null
)

data class WanLink(
        val state: String? = null,
        val type: String? = null
)

data class Internet(
        val state: Int? = null
)