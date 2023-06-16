package elfak.mosis.tourguide.ui.screens.settingsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(): ViewModel() {
    var uiState by mutableStateOf(SettingsUiState())
        private set

    init {
        // check if service is working
        // setEnabledService(enabled)
    }

    private fun setEnabledService(enabled: Boolean) {
        uiState = uiState.copy(isServiceEnabled = enabled)
    }

    fun toggleService(enabled: Boolean) {
        setEnabledService(enabled)
    }
}
