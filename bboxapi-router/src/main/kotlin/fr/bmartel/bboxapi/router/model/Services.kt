package fr.bmartel.bboxapi.router.model

data class ServiceObject(
        val services: Services
)

data class ServiceRuleState(
        val state: Int,
        val enable: Int,
        val nbrules: Int
)

data class ServiceRuleStatus(
        val status: Int,
        val enable: Int,
        val nbrules: Int
)

data class UpnpStatus(
        val igd: ServiceRuleStatus
)

data class ProxyWolStatus(
        val status: Int,
        val enable: Int,
        val ip: String
)

data class AdminStatus(
        val status: Int,
        val enable: Int,
        val port: Int,
        val ip: String,
        val duration: String,
        val activable: Int,
        val ip6address: String
)

data class RemoteStatus(
        val proxywol: ProxyWolStatus,
        val admin: AdminStatus
)

data class ServiceState(
        val enable: Int
)

data class ServiceStatus(
        val enable: Int,
        val status: Int
)

data class UsbStatus(
        val samba: ServiceStatus,
        val printer: ServiceStatus,
        val dlna: ServiceStatus
)

data class Services(
        val now: String,
        val firewall: ServiceRuleStatus,
        val dyndns: ServiceRuleState,
        val dhcp: ServiceRuleStatus,
        val nat: ServiceRuleStatus,
        val upnp: UpnpStatus,
        val remote: RemoteStatus,
        val parentalcontrol: ServiceState,
        val wifischeduler: ServiceState,
        val voipscheduler: ServiceState,
        val notification: ServiceState,
        val hotspot: ServiceStatus,
        val usb: UsbStatus
)