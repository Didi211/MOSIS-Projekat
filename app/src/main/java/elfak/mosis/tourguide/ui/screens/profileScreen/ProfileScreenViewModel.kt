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
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.domain.helper.ValidationHelper
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.PhotoRepository
import elfak.mosis.tourguide.domain.repository.UsersRepository
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
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

    fun setDirty(dirty: Boolean) {
        uiState = uiState.copy(isDataDirty = dirty)
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
    private fun setPhotoOriginalFilename(originalFilename: String) {
        uiState = uiState.copy(photo = uiState.photo.copy(filename = originalFilename))
    }

    private fun setShouldDeletePhotos(delete: Boolean) {
        uiState = uiState.copy(shouldDeletePhotos = delete)
    }
    private fun setShouldUpdatePhoto(update: Boolean) {
        uiState = uiState.copy(shouldUpdatePhotos = update)
    }
//    private fun setPhotoIsInUrl(value: Boolean) {
////        uiState = uiState.copy(photo = uiState.photo.copy(photoIsInUrl = value))
//    }
    fun removeCurrentPhoto() {
        setHasPhoto(false)
//        setPhotoIsInUrl(false)
        setPhotoUri(null)
        setShouldDeletePhotos(true)
    }

    fun handleCameraLauncherResult(success: Boolean) {
        if (success) {
            setShouldUpdatePhoto(true)
        }
        else {
            setPhotoUri(uiState.previousPhoto.uri)
        }
//        setPhotoIsInUrl(false)
        setHasPhoto(true)
        setShouldDeletePhotos(false)
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

    @OptIn(DelicateCoroutinesApi::class)
    fun saveData() {
        val originalFilename = uiState.username
//        val photo = uiState.photo
//        val previousPhoto = uiState.previousPhoto
        val allGood = !uiState.isDataDirty && !uiState.shouldUpdatePhotos && !uiState.shouldDeletePhotos

        if (allGood) {
            finishLoadingAnimation()
            setSuccessMessage("All good, nothing to change.")
            return
        }
        //update data
        viewModelScope.launch {
            try {
                if (!uiState.isDataDirty ) {
                    setSuccessMessage("User data good, nothing to change.")
                    finishLoadingAnimation()
                    return@launch
                }
                validationHelper.validateUserCredentials(uiState.toValidationModel())
                usersRepository.updateUserData(uiState.userId, uiState.getUserData())

                finishLoadingAnimation()
                setSuccessMessage("Profile successfully updated.")
            }
            catch (ex: Exception) {
                ex.message?.let { setErrorMessage(it) }
            }
        }
        // TODO - create foreground service and upload image there
        // TODO - cache photo locally
        // TODO - turn back to view model scope when create service
        // service will have updating status notification
        // cascade update photo url for notifications
        GlobalScope.launch {
            try {
                if (uiState.shouldUpdatePhotos) {
                    // call service - Action == Update
                    photoRepository.uploadUserPhoto(uiState.photo)
                    photoRepository.updateUserPhotos(uiState.userId, originalFilename)
                    setShouldUpdatePhoto(false)
                    setSuccessMessage("User photo will be uploaded on the server.")
                    return@launch
                }
                if (uiState.shouldDeletePhotos) {
                    // call service - Action == Delete
                    launch {
                        try {
                            photoRepository.deleteOldImages(originalFilename)
                        }
                        catch (ex: Exception) {
                            ex.message?.let { setErrorMessage(it) }
                        }
                    }
                    launch {
                        usersRepository.updateUserPhotos(uiState.userId, UserModel())
                    }
                    setShouldDeletePhotos(false)
                    setSuccessMessage("Photo will be removed from the server.")
                }
            }
            catch (ex: Exception) {
                ex.message?.let { setErrorMessage(it) }
            }
        }
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
            if (user.profilePhotoUrl.isNotBlank()) {
                setPhotoUri(Uri.parse(user.profilePhotoUrl))
                setHasPhoto(true)
    //            setPhotoIsInUrl(true)
            } else {
                setHasPhoto(false)
    //            setPhotoIsInUrl(false)
            }
            setUserId(user.id)
            setUserAuthId(user.authId)
            setFullname(user.fullname)
            setUsername(user.username)
            setPhotoOriginalFilename(user.username)
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

    private fun finishLoadingAnimation() {
        isEditMode = false
        setInProgress(false)
        setDirty(false)
    }
}