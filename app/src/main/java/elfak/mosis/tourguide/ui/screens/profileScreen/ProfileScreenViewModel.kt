package elfak.mosis.tourguide.ui.screens.profileScreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.domain.helper.ValidationHelper
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.PhotoRepository
import elfak.mosis.tourguide.domain.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle,
    private val validationHelper: ValidationHelper,
    private val photoRepository: PhotoRepository,
): ViewModel() {

    var uiState by mutableStateOf(ProfileScreenUiState())
        private set

    var isEditMode by mutableStateOf(false)
    var isEditEnabled by mutableStateOf(true)

    init {
        viewModelScope.launch {
            isEditEnabled = canEdit()
            loadData()
        }
    }

    // region UI STATE METHODS
    private fun setUserId(id: String) {
        uiState = uiState.copy(userId = id)
    }
    private fun setUserAuthId(id: String) {
        uiState = uiState.copy(userAuthId = id)
    }
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
    fun setInProgress(value: Boolean) {
        uiState = uiState.copy(inProgress = value)
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
    fun setPhotoIsInUrl(value: Boolean) {
        uiState = uiState.copy(photo = uiState.photo.copy(photoIsInUrl = value))
    }
    //endregion

    //region MESSAGE HANDLER
    fun clearErrorMessage() {
        uiState = uiState.copy(toastData = uiState.toastData.copy(hasErrors = false))
        setInProgress(false)
    }
    private fun setErrorMessage(message: String) {
        uiState = uiState.copy(toastData = uiState.toastData.copy(errorMessage = message, hasErrors = true))
    }
    private fun setSuccessMessage(message: String) {
        uiState = uiState.copy(toastData = uiState.toastData.copy(successMessage = message, hasSuccessMessage = true))
    }
    fun clearSuccessMessage() {
        uiState = uiState.copy(toastData = uiState.toastData.copy(hasSuccessMessage = false))
        setInProgress(false)
    }
    //endregion


    fun checkPermissions(context: Context): Boolean {
        val res = context.checkCallingOrSelfPermission(Manifest.permission.CAMERA)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun saveData() {
        viewModelScope.launch {
            try {
                validateUserInfo()
                if (uiState.photo.hasPhoto && !uiState.photo.photoIsInUrl) {
                    setPhotoUrl(uiState.username)
                    val photoDownloadUrl = photoRepository.uploadUserPhoto(uiState.photo)
                    setPhotoUrl(photoDownloadUrl)
                }
                usersRepository.updateUserData(uiState.userId, uiState.getUserData())
                // save password
                isEditMode = false
                setSuccessMessage("Profile successfully updated.")
            }
            catch (ex: Exception) {
                ex.message?.let { setErrorMessage(it) }
            }
        }
    }

    private fun validateUserInfo() {
        validationHelper.validateUserCredentials(uiState.toValidationModel())
    }

    private suspend fun loadData() {
        try {
            var id = getUserIdFromPath()
            if (id == null) {
                id = authRepository.getUserIdLocal()
            }
            var user: UserModel
            withContext(Dispatchers.IO) {
                user = usersRepository.getUserData(id!!)
            }
            if (user.photoUrl.isNotBlank()) {
                setPhotoUri(Uri.parse(user.photoUrl))
                setHasPhoto(true)
                setPhotoIsInUrl(true)
            } else {
                setHasPhoto(false)
                setPhotoIsInUrl(false)
            }
            setUserId(user.id)
            setUserAuthId(user.authId)
            setFullname(user.fullname)
            setUsername(user.username)
            setPhoneNumber(user.phoneNumber)
            setEmail(user.email)
        }
        catch (ex: Exception) {
            ex.message?.let { setErrorMessage(it) }
        }

    }

    private suspend fun canEdit(): Boolean {
        // can edit if is not visiting another user's profile
        val userId = getUserIdFromPath() ?: return true
        val documentId = authRepository.getUserIdLocal()
        if (userId == documentId!!) return true
        return false
    }

    private fun getUserIdFromPath(): String? {
        // userId sent when navigating to profile screen
        if (savedStateHandle.contains("userId")) {
            return savedStateHandle["userId"]
        }
        return null
    }

    fun changePassword(password: String, confirmPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                validationHelper.validatePasswords(password, confirmPassword)
                authRepository.changePassword(password)
                onSuccess()
                setSuccessMessage("Password successfully changed.")
            }
            catch (ex: Exception) {
                ex.message?.let { setErrorMessage(it) }
            }
        }
    }

}