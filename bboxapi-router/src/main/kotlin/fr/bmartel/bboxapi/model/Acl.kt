package fr.bmartel.bboxapi.model

class Acl {

    data class Model(
            val acl: WirelessRules? = null
    )

    data class WirelessRules(
            val enable: Int? = null,
            val rules: List<Rules>? = null
    )

    data class Rules(
            val id: Int? = null,
            val enable: Int? = null,
            val name: String? = null,
            val macaddress: String? = null
    )

    //only used in the API client
    data class MacFilterRule(
            val enable: Boolean,
            val macaddress: String,
            val ip: String
    )
}