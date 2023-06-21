package elfak.mosis.tourguide.ui.screens.settingsScreen

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.helper.LocationHelper
import elfak.mosis.tourguide.domain.helper.PermissionHelper
import elfak.mosis.tourguide.domain.services.foreground.LocationTrackingService
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val permissionHelper: PermissionHelper,
    private val locationHelper: LocationHelper,
): ViewModel() {
    var uiState by mutableStateOf(SettingsScreenUiState())
        private set


    //region UiState Methods
    fun setEnabledService(enabled: Boolean) {
        uiState = uiState.copy(isServiceEnabled = enabled)
    }
    private fun setPermissionsAllowed(allowed: Boolean) {
        uiState = uiState.copy(permissionsAllowed = allowed)
    }
    private fun setGps(enabled: Boolean) {
        uiState = uiState.copy(gpsEnabled = enabled)
    }
    //endregion
    fun checkGps(): Boolean {
        val enabled = locationHelper.isGpsOn()
        setGps(enabled)
        return enabled
    }

    @Suppress("DEPRECATION")
    fun isServiceRunning(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

        for (serviceInfo in runningServices) {
            if (LocationTrackingService::class.java.name == serviceInfo.service.className) {
                return true
            }
        }

        return false
    }

    fun toggleService(enabled: Boolean, context: Context) {
        val command = if (enabled) LocationTrackingService.ACTION_START else LocationTrackingService.ACTION_STOP
        val intent = Intent(context, LocationTrackingService::class.java).apply {
            action = command
        }
        context.startForegroundService(intent)
        setEnabledService(enabled)
        val state = if (enabled) "started" else "stopped"
        setSuccessMessage("Service $state.")
    }

    fun getLocationPermissions(): List<String> {
        return permissionHelper.createLocationPermissions()
    }

    fun checkPermissions(): Boolean {
        val allowed = permissionHelper.hasAllowedLocationPermissions()
        setPermissionsAllowed(allowed)
        return allowed
    }


    //region MESSAGE HANDLER
    fun clearErrorMessage() {
        uiState = uiState.copy(toastData = uiState.toastData.copy(hasErrors = false))
    }
    fun setErrorMessage(message: String) {
        uiState = uiState.copy(toastData = uiState.toastData.copy(errorMessage = message, hasErrors = true))
    }
    fun setSuccessMessage(message: String) {
        uiState = uiState.copy(toastData = uiState.toastData.copy(successMessage = message, hasSuccessMessage = true))
    }
    fun clearSuccessMessage() {
        uiState = uiState.copy(toastData = uiState.toastData.copy(hasSuccessMessage = false))
    }
    private fun handleError (ex: Exception) {
        if (ex.message != null) {
            setErrorMessage(ex.message!!)
            return
        }
        setErrorMessage("Error has occurred")
    }
    //endregion



}
