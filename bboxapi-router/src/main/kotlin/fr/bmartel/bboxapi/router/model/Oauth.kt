package fr.bmartel.bboxapi.router.model

data class OauthParam(
        val grantType: GrantType,
        val scope: List<Scope> = listOf(Scope.ALL),
        val code: String? = null,
        val refreshToken: String? = null,
        val responseType: ResponseType = ResponseType.NONE
)

data class CodeResponse(val code: String, val interval: Int, val token_type: String, val expires_in: Int, val issued_at: String)

data class TokenResponse(val access_token: String, val refresh_token: String, val token_type: String, val expires_in: Int, val issued_at: String)

enum class GrantType(val field: String) {
    REFRESH_TOKEN("refresh_token"),
    BUTTON("urn:bouyguestelecom:params:oauth:grant-type:button")
}

enum class ResponseType(val field: String) {
    NONE("none"),
    CODE("code")
}

enum class Scope(val field: String, val desc: String) {
    ALL("*", "Contrôle total de la Bbox"),
    DHCP_READ("dhcp-read", "Informations sur la configuration DHCP"), //GET api/v1/dhcp/*
    DHCP_WRITE("dhcp-write", "Configuration DHCP"), //POST,PUT,DELETE /api/v1/dhcp/*
    DEVICE_READ("device-read", "Informations sur la Bbox"), //GET /api/v1/device/*
    DEVICE_WRITE("device-write", "Modifications des informations de la Bbox"), //POST /api/v1/device/*
    HOST_READ("host-read", "Liste des équipements du LAN"), //GET /api/v1/hosts/*
    HOST_WRITE("host-write", "Suppression, scan, ping, renommage des équipements du LAN"), //POST,PUT,DELETE /api/v1/hosts/*
    LAN_READ("lan-read", "Informations sur le LAN"), //GET /api/v1/lan/*
    LAN_WRITE("lan-write", "Configuration du LAN"), //POST, PUT /api/v1/lan/*
    WIFI_READ("wifi-read", "Informations sur la configuration Wifi"), //GET /api/v1/wireless/*
    WIFI_WRITE("wifi-write", "Configuration Wifi"), //POST,PUT,DELETE /api/v1/wireless/*
    DYNS_READ("dyns-read", "Informations sur la configuration DynDNS"), //GET /api/v1/dyndns/*
    DYNS_WRITE("dyns-write", "Configuration DynDNS"), //POST,PUT,DELETE /api/v1/dyndns/*
    FW_READ("fw-read", "Informations sur la configuration du pare-feu"), //GET /api/v1/firewall/*
    FW_WRITE("fw-write", "Configuration du pare-feu"), //POST,PUT,DELETE /api/v1/firewall/*
    NAT_READ("nat-read", "Informations sur la configuration NAT et DMZ"), //GET /api/v1/nat/*
    NAT_WRITE("nat-write", "Configuration NAT et DMZ"), //POST,PUT,DELETE /api/v1/nat/*
    NOTIF_READ("notif-read", "Informations sur la configuration des notifications"), //GET /api/v1/notification/*
    NOTIF_WRITE("notif-write", "Configuration des notifications"), //POST,PUT,DELETE /api/v1/notification/*
    ALERT_READ("alert-read", "Récupération des alertes"), //GET /api/v1/alerts/*
    ALERT_WRITE("alert-write", "Modification des alertes"), //POST,PUT,DELETE /api/v1/alerts/*
    PARENTAL_READ("parental-read", "Informations sur la configuration du contrôle parental"), //GET /api/v1/parentalcontrol/*
    PARENTAL_WRITE("parental-write", "Configuration du contrôle parental"), //POST,PUT,DELETE /api/v1/parentalcontrol/*
    BACKUP_READ("backup-read", "Informations sur la sauvegarde et restauration de la configuration"), //GET /api/v1/configs/*
    BACKUP_WRITE("backup-write", "Modification de la sauvegarde et restauration de la configuration"), //POST, DELETE /api/v1/configs/*
    REBOOT("reboot", "Redémarrage de la Bbox"), //POST /api/v1/device/reboot
    FACTORY("factory", "Reset factory"), //POST /api/v1/device/factory
    REMOTE("remote", "Configuration et activation des services distants"), //GET, PUT /api/v1/remote/*
    VOIP_READ("voip-read", "Informations sur la VoIP"), //GET /api/v1/voip/*
    VOIP_WRITE("voip-write", "Configuration de la VoIP"), //PUT, POST, DELETE /api/v1/voip/*
    PASSWORD("password", "Changement de mot de passe"), //POST /api/v1/reset-password
    WAN_READ("wan-read", "Informations sur le WAN"), //GET /api/v1/wan/*
    WAN_WRITE("wan-write", "Configuration du WAN"), //PUT /api/v1/wan/*
}
