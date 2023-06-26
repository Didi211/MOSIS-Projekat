@file:Suppress("DEPRECATION")

package elfak.mosis.tourguide.ui.screens.settingsScreen

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.helper.LocationHelper
import elfak.mosis.tourguide.domain.helper.PermissionHelper
import elfak.mosis.tourguide.domain.models.tour.TourCard
import elfak.mosis.tourguide.domain.models.tour.TourSelectionDisplay
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.TourRepository
import elfak.mosis.tourguide.domain.repository.UsersRepository
import elfak.mosis.tourguide.domain.services.foreground.LocationTrackingService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val permissionHelper: PermissionHelper,
    private val locationHelper: LocationHelper,
    private val authRepository: AuthRepository,
    private val tourRepository: TourRepository,
    private val usersRepository: UsersRepository,
): ViewModel() {
    var uiState by mutableStateOf(SettingsScreenUiState())
        private set

    init {
        runBlocking {
            val userId = authRepository.getUserIdLocal()
            if (userId == null) {
                setUserId("")
                return@runBlocking
            }
            setUserId(userId)
        }
        if (uiState.userId.isNotBlank()) {
            // tours for dialog
            viewModelScope.launch {
                val tours = tourRepository.getAllTours(uiState.userId)
                setTours(tours.map { tour -> tour.toTourSelectionDisplay()})
            }
            // user's selected tour for notifications
            viewModelScope.launch {
                try {
                    val user = usersRepository.getUserData(uiState.userId)
                    val tour = tourRepository.getTour(user.tourNotify)
                    setSelectedTour(tour.toTourCard())
                }
                catch (ex: Exception) {
                    if (ex !is NoSuchFieldException)
                        ex.message?.let { setErrorMessage(it) }
                }
            }

        }
    }

    //region UiState Methods

    private fun setSelectedTour(tour: TourCard) {
        uiState = uiState.copy(tour = tour)
    }
    private fun setTours(tours: List<TourSelectionDisplay>) {
        uiState = uiState.copy(tours = tours)
    }
    private fun setUserId(id: String) {
        uiState = uiState.copy(userId = id)
    }
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
    fun isAuthenticated(): Boolean {
        return uiState.userId.isNotBlank()
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
    //endregion

    fun setNotificationForTour(tourId: String) {
        viewModelScope.launch {
            usersRepository.setTourNotify(uiState.userId, tourId)
            val tour = tourRepository.getTour(tourId)
            setSelectedTour(tour.toTourCard())
        }
    }

    fun removeTourFromNotification() {
        viewModelScope.launch {
            usersRepository.setTourNotify(uiState.userId, "")
            uiState = uiState.copy(tour = TourCard())
        }
    }




}
