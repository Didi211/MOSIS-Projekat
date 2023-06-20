package elfak.mosis.tourguide.ui.screens.registerScreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.helper.ValidationHelper
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel  @Inject constructor(
    private val authRepository: AuthRepository,
    private val photoRepository: PhotoRepository,
    private val validationHelper: ValidationHelper
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set
    init {
//        mockRegisterUsers()
    }


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
        uiState = uiState.copy(confirmPassword = confirm_password)
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
                val userId = authRepository.register(uiState.getUserData(), uiState.password)

                if (uiState.photo.hasPhoto) {
                    val originalFilename = uiState.username
                    photoRepository.uploadUserPhoto(uiState.photo)
                    photoRepository.updateUserPhotos(userId, originalFilename)
                }
                onSuccess()
            }
            catch (err:Exception) {
                uiState = uiState.copy(hasErrors = true, errorMessage = err.message ?: "Error occurred")
            }
        }
    }

    private fun mockRegisterUsers() {
        val emails = listOf(
            "user1@example.com",
            "user2@example.com",
            "user3@example.com",
            "user4@example.com",
            "user5@example.com"
        )
        val fullnames = listOf(
            "John Doe",
            "Jane Smith",
            "Michael Johnson",
            "Emily Williams",
            "David Brown"
        )
        val usernames = listOf(
            "user1",
            "user2",
            "user3",
            "user4",
            "user5",
        )

        viewModelScope.launch {
            for (i in 0..4) {
                setUsername(usernames[i])
                setEmail(emails[i])
                setFullname(fullnames[i])
                setPassword("111222")
                setConfirmPassword("111222")
                register { }
                delay(300)
            }
        }
    }

    private fun validateUserInfo() {
        validationHelper.validateUserCredentials(uiState.toValidationModel())
        validationHelper.validatePasswords(uiState.password,uiState.confirmPassword)
    }

    fun clearErrorMessage() {
        uiState = uiState.copy(hasErrors = false)
    }

    fun checkPermissions(context: Context): Boolean {
        val res = context.checkCallingOrSelfPermission(Manifest.permission.CAMERA)
        return res == PackageManager.PERMISSION_GRANTED
    }
}