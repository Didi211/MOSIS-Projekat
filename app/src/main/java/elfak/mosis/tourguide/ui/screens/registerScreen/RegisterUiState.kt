package elfak.mosis.tourguide.ui.screens.registerScreen

import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.domain.models.Photo
import elfak.mosis.tourguide.domain.models.validation.UserCredentials

data class RegisterUiState(
    var fullname: String = "",
    var username: String = "",
    val phoneNumber: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",

    var hasErrors: Boolean = false,
    var errorMessage: String = "",

    val photo: Photo = Photo(),
    val previousPhoto: Photo = Photo()
) {
    fun getUserData(): UserModel {
        return UserModel(
            id = "",
            authId = "",
            fullname = fullname,
            username = username,
            email = email,
//            password = password,
            phoneNumber = phoneNumber,
            photoUrl = photo.filename
        )
    }
    fun toValidationModel(): UserCredentials {
        return UserCredentials(
            fullname = fullname,
            username = username,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            phoneNumber = phoneNumber,
        )
    }
}
