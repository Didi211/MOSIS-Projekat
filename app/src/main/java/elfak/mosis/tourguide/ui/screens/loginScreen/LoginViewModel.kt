package elfak.mosis.tourguide.ui.screens.loginScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set


    fun changeUsername(username: String) {
        uiState = uiState.copy(username = username)
    }
    fun changePassword(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun login() {
        /* TODO - call api for login */
    }
}