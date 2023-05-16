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
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
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
    var uiState by mutableStateOf(TourScreenUiState())
        private set

    private val minimalDistanceInMeters: Int = 30 //between two sequential locations, for map move animation
    private var chosenLocation by mutableStateOf(AutocompleteResult("",""))
    val locationAutofill = mutableStateListOf<AutocompleteResult>()
    val locationAutofillDialog = mutableStateListOf<AutocompleteResult>()

    private var job: Job? = null
    var textInputJob: Job? = null
    init {
        uiState.tourDetails.onTitleChanged = { setTitle(it) }
        uiState.tourDetails.onSummaryChanged = { setSummary(it) }
        uiState.tourDetails.onStartLocationChanged = { setStartLocation(it) }
        uiState.tourDetails.onEndLocationChanged = { setEndLocation(it) }
        uiState.tourDetails.onDistanceChanged = { setDistance(it) }
        uiState.tourDetails.onTimeChanged = { setTime(it) }
    }

    override fun onCleared() {
        super.onCleared()
        this.stopLocationUpdates()
    }

    //region TOUR DETAILS
    fun setTourState(state: TourState) {
        uiState = uiState.copy(tourState = state)
    }

    private fun setTitle(title: String) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(title = title))
    }
    private fun setSummary(summary: String) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(summary = summary))
    }
    private fun setStartLocation(startLocation: elfak.mosis.tourguide.domain.models.Place) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(startLocation = startLocation))
    }
    private fun setEndLocation(endLocation: elfak.mosis.tourguide.domain.models.Place) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(endLocation = endLocation))
    }
    fun setDistance(distance: String) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(distance = distance))
    }
    fun setTime(time: String) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(time = time))
    }
    fun resetTourDetails() {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.clear())
    }
    //endregion

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
    fun setLocationCallbacks() {
        val vm = this
        locationHelper.setOnLocationResultListener {
            viewModelScope.launch {
                Log.d("LOCATION", "New location: LAT: ${it.latitude}; LON: ${it.longitude}; ")
                vm.changeMyLocation(LatLng(it.latitude, it.longitude))
                if (!isLocated()) return@launch //skip animation
                vm.onLocationChanged()
            }
        }
        locationHelper.setonLocationAvailabilityListener { gpsEnabled ->
            setGps(gpsEnabled)
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
        setLocationPermissionStatus(allowed)
        return allowed
    }

    fun checkGps(): Boolean {
        val status = locationHelper.isGpsOn()
        setGps(status)
        return status
    }

    //endregion

    //region CAMERA ANIMATION

    fun onLocationChanged(mustMove: Boolean = true) {
        if (isLocated()) {
            changeLocation(uiState.myLocation)
            // mustMove - user is requesting repositioning
            if (!mustMove || !isMovingCameraNecessary(uiState.cameraPositionState.position.target)) {
                return
            }
        }
        else {
            changeLocation(uiState.searchedLocation)
        }
        moveCamera()
    }

    private fun isMovingCameraNecessary(currentCameraPosition: LatLng ): Boolean {
        // calculates if previous location is close to the new one so the camera is basically positioned
        val distance = locationHelper.distanceInMeter(
            startLat = uiState.myLocation.latitude,
            startLon = uiState.myLocation.longitude,
            endLat = currentCameraPosition.latitude,
            endLon = currentCameraPosition.longitude
        )
        return distance > minimalDistanceInMeters
    }

    private fun moveCamera() {
        viewModelScope.launch {
            try {
                // keeping the zoom level the same if it is zoomed enough
                val zoom = if (uiState.cameraPositionState.position.zoom < 12) 14f else uiState.cameraPositionState.position.zoom
                uiState.cameraPositionState.animate(
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

    //region SEARCH LOCATION
    fun onSearchPlaceCLick(place: AutocompleteResult) {
        chooseLocation(place)
        searchOnMap()
        clearSearchBar()
    }

    fun findPlacesFromInput(query: String, showInDialog: Boolean = false) {
        textInputJob?.cancel()
        textInputJob = viewModelScope.launch {
            job?.cancel()
            if(showInDialog) {
                locationAutofillDialog.clear()
            }
            else {
                locationAutofill.clear()
            }
            job = viewModelScope.launch {
                launchSearchRequest(query, showInDialog)
            }
        }
    }

    private fun launchSearchRequest(query: String, showInDialog: Boolean) {
        val request = FindAutocompletePredictionsRequest
            .builder()
            .setQuery(query)
            .build()

        // call api to find places
        try {
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    if (showInDialog) {
                        onDialogSearchSuccess(response)
                    }
                    else {
                        onSearchSuccess(response)
                    }
                }
        }
        catch(ex: Exception) {
            Log.e("PLACE API", ex.message!!)
        }
    }

    private fun onSearchSuccess(response: FindAutocompletePredictionsResponse) {
        // if got any, populate location list
        locationAutofill += response.autocompletePredictions.map {
            AutocompleteResult(
                address = it.getFullText(null).toString(),
                placeId = it.placeId
            )
        }
    }
    private fun onDialogSearchSuccess(response: FindAutocompletePredictionsResponse) {
        // if got any, populate location list
        locationAutofillDialog += response.autocompletePredictions.map {
            AutocompleteResult(
                address = it.getFullText(null).toString(),
                placeId = it.placeId
            )
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

    fun searchOnMap() {
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
                        onLocationChanged()
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

    //region DEVICE SETTINGS
    private fun setGps(gpsEnabled: Boolean) {
        uiState = uiState.copy(deviceSettings = uiState.deviceSettings.copy(gpsEnabled = gpsEnabled))
    }

    private fun setLocationPermissionStatus(status: Boolean) {
        uiState = uiState.copy(deviceSettings = uiState.deviceSettings.copy(locationPermissionAllowed = status))
    }
    //endregion


}