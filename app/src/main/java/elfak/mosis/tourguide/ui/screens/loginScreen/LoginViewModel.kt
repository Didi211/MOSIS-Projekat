package elfak.mosis.tourguide.ui.screens.loginScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.respository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun changeUsername(username: String) {
        uiState = uiState.copy(username = username)
    }
    fun changePassword(password: String) {
        uiState = uiState.copy(password = password)
    }


    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // to launch coroutine - async function that does not block main thread
            try {
                val result = authRepository.login(uiState.username, uiState.password)
                // TODO - send user id on home screen or save it locally as currentUser
                onSuccess()
            }
            catch (err: Exception) {
                uiState = uiState.copy(hasErrors = true, errorMessage = err.message ?: "Error occurred")
            }
        }
    }

    fun clearErrorMessge() {
        uiState = uiState.copy(hasErrors = false)
    }
}