package elfak.mosis.tourguide.ui.screens.loginScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.helper.ValidationHelper
import elfak.mosis.tourguide.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val validationHelper: ValidationHelper
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun changeEmail(email: String) {
        uiState = uiState.copy(email = email)
    }
    fun changePassword(password: String) {
        uiState = uiState.copy(password = password)
    }


    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // to launch coroutine - async function that does not block main thread
            try {
               validateCredentials()
                authRepository.login(uiState.email, uiState.password)
                onSuccess()
            }
            catch (err: Exception) {
                uiState = uiState.copy(hasErrors = true, errorMessage = err.message ?: "Error occurred")
            }
        }
    }

    fun clearErrorMessage() {
        uiState = uiState.copy(hasErrors = false)
    }

    private fun validateCredentials() {
        validationHelper.validateLoginCredentials(uiState.toValidationModel())
    }
}