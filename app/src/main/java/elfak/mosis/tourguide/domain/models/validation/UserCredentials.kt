package elfak.mosis.tourguide.domain.models.validation

data class UserCredentials(
    val fullname: String = "",
    val username: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)
