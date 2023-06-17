package elfak.mosis.tourguide.domain.models.validation

data class LoginCredentials(
    val email: String = "",
    val password: String = ""
)
