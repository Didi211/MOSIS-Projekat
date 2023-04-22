package elfak.mosis.tourguide.ui.screens.tourScreen

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.business.wrapper.PermissionWrapper
import elfak.mosis.tourguide.ui.components.maps.LocationState
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")

class TourScreenViewModel @Inject constructor(
    private val permissionWrapper: PermissionWrapper,
    private val fusedLocationClient: FusedLocationProviderClient
): ViewModel() {

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult ?: return
            for (location in locationResult.locations) {
                changeLocation(LatLng(location.latitude,location.longitude))
            }
        }
    }

    fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,3000).build()
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun onLocationChanged(cameraPositionState: CameraPositionState) {
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(this.uiState.currentLocation, 18f)
            ),
            1500
        )
    }

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


//    @SuppressLint("MissingPermission", "NewApi")
//    @OptIn(ExperimentalPermissionsApi::class)
//    suspend fun locateUser(permissionsState: MultiplePermissionsState, viewModel: ViewModel, context: Context, showGpsDisabledMessage: Boolean = true): LatLng? {
//        // TODO - move this in separate folder and make it reusable
//        if(permissionsState.allPermissionsGranted) {
//            if (this.uiState.gpsEnabled) {
//                /* TODO - constantly update current location from gps device */
//                fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//                var locationResult = fusedLocationClient.lastLocation.await() //lastLocation.addOnSuccessListener { location: Location? ->
//                if (locationResult != null && locationResult.isComplete) {
//                    val location = locationResult
//                    val latitude = location.latitude
//                    val longitude = location.longitude
//                    changeLocation(LatLng(latitude, longitude))
//                    return this.uiState.currentLocation
//                } else if (showGpsDisabledMessage){
//                    Toasty.info(
//                        context,
//                        R.string.location_needed,
//                        Toast.LENGTH_SHORT,
//                        true
//                    ).show()
//                }
//            } else {
//                /* TODO - turn on gps */
//
//                Toasty.info(
//                    context,
//                    R.string.location_needed,
//                    Toast.LENGTH_SHORT,
//                    true
//                ).show()
//            }
//        }
//        else {
//            if (permissionsState.shouldShowRationale) {
//                Toasty.info(
//                context,
//                R.string.permission_not_enabled,
//                Toast.LENGTH_LONG,
//                true
//                ).show()
//            }
//            else {
//                permissionsState.launchMultiplePermissionRequest()
//            }
//        }
//        return null
//    }


}

