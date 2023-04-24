package elfak.mosis.tourguide.ui.screens.tourScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import android.util.Log

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.business.helper.LocationHelper
import elfak.mosis.tourguide.ui.components.maps.LocationState
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")

class TourScreenViewModel @Inject constructor(
    val locationHelper: LocationHelper
): ViewModel() {

    var uiState by mutableStateOf(TourScreenUiState())
        private set
//    init {
//        uiState = uiState.copy(gpsEnabled = locationHelper.isGpsOn())
//    }

    fun changeLocationState(state: LocationState) {
        uiState = uiState.copy(locationState = state)
    }

    private fun enableGps() {
        uiState = uiState.copy(gpsEnabled = true)
    }
    private fun disableGps() {
        uiState = uiState.copy(gpsEnabled = false)
    }

    fun createLocationPermissions(): List<String> {
        return locationHelper.createLocationPermissions()
    }

    private fun changeLocation(newLocation:LatLng) {
        uiState = uiState.copy(currentLocation = newLocation)
    }

    fun startLocationUpdates() {
        try {
            // TODO - this need more work
            locationHelper.startLocationTracking()
//            uiState = uiState.copy(isTrackingLocation = true)
        }
        catch (e: Exception) {
            e.printStackTrace()
            Log.e("LocationERR",e.message!!)
        }
    }



    suspend fun onLocationChanged(cameraPositionState: CameraPositionState) {
//        val distance = locationHelper.distanceInMeter(
//            startLat = uiState.currentLocation.latitude,
//            startLon = uiState.currentLocation.longitude,
//            endLat = cameraPositionState.position.target.latitude,
//            endLon = cameraPositionState.position.target.longitude
//        )
////        val mapCameraTriggerInMeters = 15
//        if (distance > mapCameraTriggerInMeters) {
//        }
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(this.uiState.currentLocation, 18f)
                ),
                1500
            )
    }

    fun setLocationCallback() {
        val vm = this
        locationHelper.setOnLocationResultListener {
            viewModelScope.launch {
                vm.changeLocation(LatLng(it.latitude, it.longitude))
//                vm.onLocationChanged(cameraPositionState)
            }
        }
    }



    fun checkPermissions(): Boolean {
        val allowed = locationHelper.hasAllowedPermissions()
        uiState = uiState.copy(locationPermissionAllowed = allowed)
        return allowed
    }

    fun checkGps(): Boolean {
        val status = locationHelper.isGpsOn()
        uiState = uiState.copy(gpsEnabled = status)
        return status
    }



    fun checkLocationState() {
        // check permissions
        if (!locationHelper.hasAllowedPermissions()) {
            changeLocationState(LocationState.LocationOff)
            return
        }
        // check gps
        // check tracking mode
    }



}

