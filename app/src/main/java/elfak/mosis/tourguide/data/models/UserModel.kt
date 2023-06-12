package elfak.mosis.tourguide.data.models


data class UserModel(
    val username: String,
    val fullname: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val photoUrl: String,
)
