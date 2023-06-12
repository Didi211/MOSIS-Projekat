package elfak.mosis.tourguide.ui.screens.registerScreen

import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.domain.models.Photo

data class RegisterUiState(
    var fullname: String = "",
    var username: String = "",
    val phoneNumber: String = "",
    var email: String = "",
    var password: String = "",
    var confirm_password: String = "",

    var hasErrors: Boolean = false,
    var errorMessage: String = "",

    val photo: Photo = Photo(),
) {
    fun getUserData(): UserModel {
        return UserModel(
            fullname = fullname,
            username = username,
            email = email,
            password = password,
            phoneNumber = phoneNumber,
            photoUrl = "profile_photo_$username"
        )
    }
}
