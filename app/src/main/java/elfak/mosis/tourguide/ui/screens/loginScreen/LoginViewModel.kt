package elfak.mosis.tourguide.ui.screens.loginScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.data.respository.AuthRepository
import elfak.mosis.tourguide.data.respository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
//    private val _usersList = MutableStateFlow<List<UserModel>>(emptyList())
//    val usersList = _usersList.asStateFlow()

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
        viewModelScope.launch {
            authRepository.login(uiState.username, uiState.password)


//            usersRepository.createUser(
//                UserModel(
//                    username = uiState.username,
//                    password = uiState.password,
//                    loggedIn = true,
//                    firstname = "Dimitrije",
//                    lastname = "Mitic"
//                )
//            )
        }
    }
}