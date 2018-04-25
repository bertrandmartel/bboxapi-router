package fr.bmartel.bboxapi.router.model

data class RecoveryVerify(val method: String, val expires: Int)

enum class PasswordStrength {
    MEDIUM, STRONG
}