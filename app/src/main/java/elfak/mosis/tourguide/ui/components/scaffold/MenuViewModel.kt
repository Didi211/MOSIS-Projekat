package elfak.mosis.tourguide.ui.components.scaffold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}