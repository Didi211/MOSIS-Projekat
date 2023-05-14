package elfak.mosis.tourguide.ui.screens.tourScreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.business.helper.BitmapHelper
import elfak.mosis.tourguide.business.helper.LocationHelper
import elfak.mosis.tourguide.data.models.AutocompleteResult
import elfak.mosis.tourguide.ui.components.maps.LocationState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")

class TourScreenViewModel @Inject constructor(
    private val locationHelper: LocationHelper,
    val bitmapHelper: BitmapHelper,
    private val placesClient: PlacesClient
): ViewModel() {

    val locationAutofill = mutableStateListOf<AutocompleteResult>()
    private var chosenLocation by mutableStateOf(AutocompleteResult("",""))
    var uiState by mutableStateOf(TourScreenUiState())
        private set

    private var job: Job? = null
    var textInputJob: Job? = null

    // region UISTATE METHODS
    fun setSearchBarVisibility(value: Boolean) {
        uiState = uiState.copy(showSearchBar = value)
    }

    fun setSearchFlag(isSearching: Boolean) {
        uiState = uiState.copy(isSearching = isSearching)
    }
    fun changeLocationState(state: LocationState) {
        uiState = uiState.copy(locationState = state)
    }

    private fun changeLocation(newLocation:LatLng) {
        uiState = uiState.copy(currentLocation = newLocation)
    }

    private fun changeMyLocation(newLocation: LatLng) {
        uiState = uiState.copy(myLocation = newLocation)
    }

    private fun changeSearchedLocation(newLocation: LatLng) {
        uiState = uiState.copy(searchedLocation = newLocation)
    }

    fun changeSearchValue(value: String) {
        uiState = uiState.copy(searchValue = value)
    }

    fun isLocated(): Boolean {
        return uiState.locationState == LocationState.Located
    }

    //endregion

    //region LOCATION HELPER WRAPPER
    fun setLocationCallbacks(cameraPositionState: CameraPositionState) {
        val vm = this
        locationHelper.setOnLocationResultListener {
            viewModelScope.launch {
                Log.d("LOCATION", "New location: LAT: ${it.latitude}; LON: ${it.longitude}; ")
                vm.changeMyLocation(LatLng(it.latitude, it.longitude))
                if (!isLocated()) return@launch //skip animation
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
                changeLocationState(LocationState.LocationOff)
            }
        }
    }

    fun createLocationPermissions(): List<String> {
        return locationHelper.createLocationPermissions()
    }


    fun startLocationUpdates() {
        try {
            if(isLocated()) {
               return
            }
            locationHelper.startLocationTracking()
//            uiState = uiState.copy(requestingLocationUpdates = true)
        }
        catch (e: Exception) {
            e.printStackTrace()
            Log.e("LocationERR",e.message!!)
        }
    }

    private fun stopLocationUpdates() {
        locationHelper.stopLocationTracking()
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

    //endregion

    //region CAMERA ANIMATION

    fun onLocationChanged(cameraPositionState: CameraPositionState, mustMove: Boolean = true) {
        if (isLocated()) {
            changeLocation(uiState.myLocation)
            // mustMove - user is requesting repositioning
            if (!mustMove || !isMovingCameraNecessary(cameraPositionState.position.target)) {
                return
            }
        }
        else {
            changeLocation(uiState.searchedLocation)
        }
        moveCamera(cameraPositionState)
    }

    private fun isMovingCameraNecessary(currentCameraPosition: LatLng ): Boolean {
        // calculates if previous location is close to the new one so the camera is basically positioned
        val distance = locationHelper.distanceInMeter(
            startLat = uiState.myLocation.latitude,
            startLon = uiState.myLocation.longitude,
            endLat = currentCameraPosition.latitude,
            endLon = currentCameraPosition.longitude
        )
        return distance > uiState.minimalDistanceInMeters
    }

    private fun moveCamera(cameraPositionState: CameraPositionState) {
        viewModelScope.launch {
            try {
                // keeping the zoom level the same if it is zoomed enough
                val zoom = if (cameraPositionState.position.zoom < 12) 14f else cameraPositionState.position.zoom
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(uiState.currentLocation, zoom)
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
    }

    //endregion


    override fun onCleared() {
        super.onCleared()
        this.stopLocationUpdates()
    }


    //region SEARCH LOCATION


    fun findPlacesFromInput(query: String) {
        job?.cancel()
        locationAutofill.clear()
        job = viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest
                .builder()
                .setQuery(query)
                .build()

            // call api to find places
            try {
                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        // if got any, populate location list
                        locationAutofill += response.autocompletePredictions.map {
                            AutocompleteResult(
                                address = it.getFullText(null).toString(),
                                placeId = it.placeId
                            )
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        println(it.cause)
                        println(it.message)
                    }
            }
            catch(ex: Exception) {
                Log.e("PLACE API", ex.message!!)
            }
        }
    }

    // choose location from given list
    fun chooseLocation(location: AutocompleteResult) {
        chosenLocation =  chosenLocation.copy(
            address = location.address,
            placeId = location.placeId
        )
        locationAutofill.clear()
        changeSearchValue(location.address)
    }

    fun searchOnMap(cameraPositionState: CameraPositionState) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(this.chosenLocation.placeId, placeFields)
        // find coordinates based on placeId
        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                if (it != null) {
                    changeSearchedLocation(it.place.latLng!!)
                    if (isLocated()) {
                        changeLocationState(LocationState.LocationOn)
                    }
                    setSearchFlag(true)
                    // call animation
                    viewModelScope.launch {
                        onLocationChanged(cameraPositionState)
                    }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }



    fun clearSearchBar() {
        changeSearchValue("")
        locationAutofill.clear()
        setSearchBarVisibility(false)
//        setKeyboardVisibility(false)

    }

//    fun setKeyboardVisibility(visible: Boolean) {
//        uiState = uiState.copy(showKeyboard = visible)
//    }

    //endregion

    //region TOUR DETAILS
    fun changeTitle(title: String) {
        uiState = uiState.copy(tourTitle = title)
    }
    fun changeStartLocation(start: String) {
        uiState = uiState.copy(startLocation = start)
    }
    fun changeEndLocation(end: String) {
        uiState = uiState.copy(endLocation = end)
    }



}