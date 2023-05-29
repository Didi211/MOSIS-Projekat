package elfak.mosis.tourguide.ui.screens.registerScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel  @Inject constructor(
    private val authRepository: AuthRepository,
    ) : ViewModel() {
//    private val _usersList = MutableStateFlow<List<UserModel>>(emptyList())
//    val usersList = _usersList.asStateFlow()

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun changeFullname(fullname: String) {
        uiState = uiState.copy(fullname = fullname)
    }
    fun changeUsername(username: String) {
        uiState = uiState.copy(username = username)
    }
    fun changeEmail(email: String) {
        uiState = uiState.copy(email = email)
    }
    fun changePassword(password: String) {
        uiState = uiState.copy(password = password)
    }
    fun changeConfirmPassword(confirm_password: String) {
        uiState = uiState.copy(confirm_password = confirm_password)
    }

    fun register(onSuccess: () -> Unit) {
        /* TODO - call api for register */
        viewModelScope.launch {
            // to launch coroutine - async function that does not block main thread
            try {
                if (uiState.password != uiState.confirm_password) {
                    throw Exception("Passwords doesn't match.")
                }
                authRepository.register(uiState.username, uiState.password, uiState.email, uiState.fullname)
                onSuccess()
            }
            catch (err:Exception) {
                uiState = uiState.copy(hasErrors = true, errorMessage = err.message ?: "Error occurred")
            }
        }
    }

    fun clearErrorMessage() {
        uiState = uiState.copy(hasErrors = false)
    }

}