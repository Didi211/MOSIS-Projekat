package elfak.mosis.tourguide.ui.screens.registerScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.UserModel
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
                validateUserInfo()
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
        val charsOnly = Regex("^[a-zA-Z]+$")
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

    fun changePhoneNumber(phone: String) {
        uiState = uiState.copy(phoneNumber = phone)
    }

}