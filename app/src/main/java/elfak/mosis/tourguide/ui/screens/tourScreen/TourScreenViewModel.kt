package elfak.mosis.tourguide.ui.screens.tourScreen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.business.wrapper.PermissionWrapper
import elfak.mosis.tourguide.ui.components.maps.LocationState
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class TourScreenViewModel @Inject constructor(
    private val permissionWrapper: PermissionWrapper
): ViewModel() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    var uiState by mutableStateOf(TourScreenUiState())
        private set

    fun changeLocationState(state: LocationState) {
        uiState = uiState.copy(locationState = state)
    }

    fun enableGps() {
        uiState = uiState.copy(gpsEnabled = true)
    }
    private fun disableGps() {
        uiState = uiState.copy(gpsEnabled = false)
    }

    fun createLocationPermissions(): List<String> {
        return permissionWrapper.createLocationPermissions()
    }

    private fun changeLocation(newLocation:LatLng) {
        uiState = uiState.copy(currentLocation = newLocation)
    }


    @SuppressLint("MissingPermission", "NewApi")
    @OptIn(ExperimentalPermissionsApi::class)
    suspend fun locateUser(permissionsState: MultiplePermissionsState, viewModel: ViewModel, context: Context): LatLng? {
        // TODO - move this in separate folder and make it reusable
        if(permissionsState.allPermissionsGranted) {
            if (this.uiState.gpsEnabled) {
                /* TODO - constantly update current location from gps device */
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                var locationResult = fusedLocationClient.lastLocation.await() //lastLocation.addOnSuccessListener { location: Location? ->
                if (locationResult != null && locationResult.isComplete) {
                    val location = locationResult
                    val latitude = location.latitude
                    val longitude = location.longitude
                    changeLocation(LatLng(latitude, longitude))
                    return this.uiState.currentLocation
                } else {
                    Toasty.info(
                        context,
                        R.string.location_needed,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            } else {
                /* TODO - turn on gps */

                Toasty.info(
                    context,
                    R.string.location_needed,
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        }
        else {
            if (permissionsState.shouldShowRationale) {
                Toasty.info(
                context,
                R.string.permission_not_enabled,
                Toast.LENGTH_LONG,
                true
                ).show()
            }
            else {
                permissionsState.launchMultiplePermissionRequest()
            }
        }
        return null
    }
}

