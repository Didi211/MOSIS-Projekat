package elfak.mosis.tourguide.ui.screens.registerScreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.PhotoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel  @Inject constructor(
    private val authRepository: AuthRepository,
    private val photoRepository: PhotoRepository
    ) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set


    // region UI STATE METHODS
    fun setFullname(fullname: String) {
        uiState = uiState.copy(fullname = fullname)
    }
    fun setUsername(username: String) {
        uiState = uiState.copy(username = username)
    }
    fun setPhoneNumber(phone: String) {
        uiState = uiState.copy(phoneNumber = phone)
    }
    fun setEmail(email: String) {
        uiState = uiState.copy(email = email)
    }
    fun setPassword(password: String) {
        uiState = uiState.copy(password = password)
    }
    fun setConfirmPassword(confirm_password: String) {
        uiState = uiState.copy(confirm_password = confirm_password)
    }
    //endregion

    // region CAMERA
    fun setHasPhoto(value: Boolean) {
        uiState = uiState.copy(photo = uiState.photo.copy(hasPhoto = value))
    }
    fun setPhotoUri(uri: Uri?) {
        uiState = uiState.copy(photo = uiState.photo.copy(uri = uri))
    }
    fun setPreviousPhoto() {
        uiState = uiState.copy(previousPhoto = uiState.photo)
    }
    private fun setPhotoUrl(url: String) {
        uiState = uiState.copy(photo = uiState.photo.copy(filename = url))
    }
    //endregion

    fun register(onSuccess: () -> Unit) {
        /* TODO - call api for register */
        viewModelScope.launch {
            // to launch coroutine - async function that does not block main thread
            try {
                validateUserInfo()
                if (!authRepository.tryRegister(uiState.username)) {
                    throw Exception("Username is already taken.")
                }
                if (uiState.photo.hasPhoto) {
                    setPhotoUrl(uiState.username)
                    val photoDownloadUrl = photoRepository.uploadUserPhoto(uiState.photo)
                    setPhotoUrl(photoDownloadUrl)
                }
                authRepository.register(uiState.getUserData())
                onSuccess()
            }
            catch (err:Exception) {
                uiState = uiState.copy(hasErrors = true, errorMessage = err.message ?: "Error occurred")
            }
        }
    }

    private fun validateUserInfo() {
        val emailRegex = Regex("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$")
        val charsOnly = Regex("^[a-zA-Z ]+$")

        // fullname
        if (uiState.fullname.isBlank()) {
            throw Exception("Fullname cannot be empty.")
        }
        if (!uiState.fullname.matches(charsOnly)) {
            throw Exception("Fullname must be only characters.")
        }

        // username
        if (uiState.username.isBlank()) {
            throw Exception("Username cannot be empty.")
        }

        // phone number
        if (uiState.phoneNumber.isNotBlank()) {
            if (!uiState.phoneNumber.isDigitsOnly()) {
                throw Exception("Phone number must be only digits")
            }
        }

        // email
        if (uiState.email.isBlank() ) {
            throw Exception("Email cannot be empty.")
        }
        if (!uiState.email.matches(emailRegex)) {
            throw Exception("Email not valid. Proper form: 'tour@tourguide.com'")
        }

        // passwords
        if (uiState.password.isBlank()) {
            throw Exception("Password cannot be empty.")
        }
        if (uiState.password.length < 6) {
            throw Exception("Password must have 6 symbols.")
        }
        if (uiState.password != uiState.confirm_password) {
            throw Exception("Passwords are not matching!")
        }
    }


    fun clearErrorMessage() {
        uiState = uiState.copy(hasErrors = false)
    }



    fun checkPermissions(context: Context): Boolean {
        val res = context.checkCallingOrSelfPermission(Manifest.permission.CAMERA)
        return res == PackageManager.PERMISSION_GRANTED
    }

}