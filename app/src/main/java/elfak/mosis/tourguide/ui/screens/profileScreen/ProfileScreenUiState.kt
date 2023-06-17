package elfak.mosis.tourguide.ui.screens.profileScreen

import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.domain.models.Photo
import elfak.mosis.tourguide.domain.models.ToastData
import elfak.mosis.tourguide.domain.models.validation.UserCredentials

data class ProfileScreenUiState(
    val userId: String = "",
    val userAuthId: String = "",
    var fullname: String = "",
    var username: String = "",
    val phoneNumber: String = "",
    var email: String = "",

    var hasErrors: Boolean = false,
    var errorMessage: String = "",

    val photo: Photo = Photo(),
    val previousPhoto: Photo = Photo(),


    val toastData: ToastData = ToastData(),
    val inProgress: Boolean = false,
) {

    fun getUserData(): UserModel {
        return UserModel(
            id = "",
            authId = userAuthId,
            fullname = fullname,
            username = username,
            email = email,
//            password = password,
            phoneNumber = phoneNumber,
            photoUrl = getPhotoUrl()
        )
    }

    private fun getPhotoUrl():String {
        if (photo.hasPhoto) {
            if (photo.photoIsInUrl) {
                return photo.uri.toString()
            }
            return photo.filename
        }
        return ""
    }

    fun toValidationModel(): UserCredentials {
        return UserCredentials(
            fullname = fullname,
            username = username,
            email = email,
//            password = password,
//            confirmPassword = confirmPassword,
            phoneNumber = phoneNumber,
        )
    }
}


