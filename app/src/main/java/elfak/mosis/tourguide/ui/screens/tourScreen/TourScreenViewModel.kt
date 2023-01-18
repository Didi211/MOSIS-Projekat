package elfak.mosis.tourguide.ui.screens.tourScreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.ui.components.maps.LocationState
import java.security.PermissionCollection
import javax.inject.Inject

@HiltViewModel
class TourScreenViewModel @Inject constructor(): ViewModel() {
    var uiState by mutableStateOf(TourScreenUiState())
        private set

    fun changeLocationState(state: LocationState) {
        uiState = uiState.copy(locationState = state)
    }

    fun toggleGps(state: Boolean) {
        uiState = uiState.copy(gpsEnabled = state)
    }
}