package elfak.mosis.tourguide.ui.screens.tourScreen

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.business.helper.BitmapHelper
import elfak.mosis.tourguide.business.helper.LocationHelper
import elfak.mosis.tourguide.ui.components.maps.LocationState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")

class TourScreenViewModel @Inject constructor(
    val locationHelper: LocationHelper,
    val bitmapHelper: BitmapHelper
): ViewModel() {

    var uiState by mutableStateOf(TourScreenUiState())
        private set

    fun changeLocationState(state: LocationState) {
        uiState = uiState.copy(locationState = state)
    }

    fun createLocationPermissions(): List<String> {
        return locationHelper.createLocationPermissions()
    }

    private fun changeLocation(newLocation:LatLng) {
        uiState = uiState.copy(currentLocation = newLocation)
    }

    fun startLocationUpdates() {
        try {
            if(uiState.requestingLocationUpdates) {
               return
            }
            locationHelper.startLocationTracking()
            uiState = uiState.copy(requestingLocationUpdates = true)
        }
        catch (e: Exception) {
            e.printStackTrace()
            Log.e("LocationERR",e.message!!)
        }
    }

    fun stopLocationUpdates() {
        locationHelper.stopLocationTracking()
        uiState = uiState.copy(requestingLocationUpdates = false)
    }

    fun setRequestingLocationUpdates(value: Boolean) {
        uiState = uiState.copy(requestingLocationUpdates = value)
    }

    suspend fun onLocationChanged(cameraPositionState: CameraPositionState) {
        if(!uiState.requestingLocationUpdates) {
            return
        }
        val distance = locationHelper.distanceInMeter(
            startLat = uiState.currentLocation.latitude,
            startLon = uiState.currentLocation.longitude,
            endLat = cameraPositionState.position.target.latitude,
            endLon = cameraPositionState.position.target.longitude
        )
        if (distance <= uiState.minimalDistanceInMeters) {
            return
        }
        try {
            // keeping the zoom level the same if it is zoomed enough
            val zoom = if (cameraPositionState.position.zoom < 12) 14f else cameraPositionState.position.zoom
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(this.uiState.currentLocation, zoom)
                ),
                1500
            )

        }
        catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
        }
    }

    fun setLocationCallbacks(cameraPositionState: CameraPositionState) {
        val vm = this
        locationHelper.setOnLocationResultListener {
            viewModelScope.launch {
                vm.changeLocation(LatLng(it.latitude, it.longitude))
                vm.onLocationChanged(cameraPositionState)
            }
        }
        locationHelper.setonLocationAvailabilityListener { gpsEnabled ->
            uiState = uiState.copy(gpsEnabled = gpsEnabled)
            if (gpsEnabled) {
                startLocationUpdates()
                changeLocationState(LocationState.Located)
            }
            else {
//                stopLocationUpdates()
                uiState = uiState.copy(requestingLocationUpdates = false)
                changeLocationState(LocationState.LocationOff)
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

    override fun onCleared() {
        super.onCleared()
        this.stopLocationUpdates()
    }
}