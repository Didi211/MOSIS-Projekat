package elfak.mosis.tourguide.ui.screens.registerScreen

import elfak.mosis.tourguide.data.models.UserModel

data class RegisterUiState(
    var fullname: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var confirm_password: String = "",
    var hasErrors: Boolean = false,
    var errorMessage: String = "",
    val phoneNumber: String = "",
    val hasPhoto: Boolean = false,
    val photoUrl: String = ""
) {
    fun getUserData(): UserModel {
        return UserModel(
            fullname = fullname,
            username = username,
            email = email,
            password = password,
            phoneNumber = phoneNumber,
            photoUrl = photoUrl
        )
    }
}
