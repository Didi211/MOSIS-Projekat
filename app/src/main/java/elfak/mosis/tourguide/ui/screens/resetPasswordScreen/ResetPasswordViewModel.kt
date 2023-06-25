package elfak.mosis.tourguide.ui.screens.resetPasswordScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.helper.ValidationHelper
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.ui.screens.loginScreen.LoginUiState
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val validationHelper: ValidationHelper
) : ViewModel() {
    var uiState by mutableStateOf(ResetPasswordUIState())
        private set

    fun changeEmail(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun sendResetEmail(){
        viewModelScope.launch {
            try{
                validationHelper.validateEmailAdress(uiState.email)
                authRepository.sendResetEmail(uiState.email)
                setSuccessMessage("Email was sent")

            }
            catch (err: Exception) {
//                err.message?.let { Log.e("Tag", it) }
                uiState = uiState.copy(hasErrors = true, errorMessage = err.message ?: "Error occurred")

            }
        }
    }

    //region Message Handler
    fun clearErrorMessage() {
        uiState = uiState.copy(toastData = uiState.toastData.copy(hasErrors = false))
    }
    private fun setErrorMessage(message: String) {
        uiState = uiState.copy(toastData = uiState.toastData.copy(errorMessage = message, hasErrors = true))
    }
    private fun setSuccessMessage(message: String) {
        uiState = uiState.copy(toastData = uiState.toastData.copy(successMessage = message, hasSuccessMessage = true))
    }
    fun clearSuccessMessage() {
        uiState = uiState.copy(toastData = uiState.toastData.copy(hasSuccessMessage = false))
    }




    //endregion
}
