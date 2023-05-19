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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.PlaceAutocompleteResult
import elfak.mosis.tourguide.domain.api.RoutesApiWrapper
import elfak.mosis.tourguide.domain.helper.LocationHelper
import elfak.mosis.tourguide.domain.helper.UnitConvertor
import elfak.mosis.tourguide.domain.models.google.Viewport
import elfak.mosis.tourguide.ui.components.maps.LocationState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")

class TourScreenViewModel @Inject constructor(
    private val locationHelper: LocationHelper,
    private val placesClient: PlacesClient,
    private val routesApiWrapper: RoutesApiWrapper,
    private val convertor: UnitConvertor
): ViewModel() {
    var uiState by mutableStateOf(TourScreenUiState())
        private set

    private var chosenLocation by mutableStateOf(PlaceAutocompleteResult("",""))
    val locationAutofill = mutableStateListOf<PlaceAutocompleteResult>()
    val locationAutofillDialog = mutableStateListOf<PlaceAutocompleteResult>()

    private var job: Job? = null
    private var textInputJob: Job? = null
    init {
        viewModelScope.launch {
            routesApiWrapper.testApi()
        }
        uiState.tourDetails.onTitleChanged = { setTitle(it) }
        uiState.tourDetails.onSummaryChanged = { setSummary(it) }
        uiState.tourDetails.onOriginChanged = { setOrigin(it) }
        uiState.tourDetails.onDestinationChanged = { setDestination(it) }
        uiState.tourDetails.onDistanceChanged = { setDistance(it) }
        uiState.tourDetails.onTimeChanged = { setTime(it) }
        uiState.tourDetails.onBothLocationsSet = {
            setBothLocationsSet(it)
            if (it) {
                viewModelScope.launch {
                    setLocationsLatLng()
                    val origin = uiState.tourDetails.origin.id
                    val destination = uiState.tourDetails.destination.id
                    // TODO -  handle errors
                    if(origin == destination) return@launch // notify user that it is the same location
                    val result = routesApiWrapper.getRoute(origin, destination)
                    // null checking if error has happened
                    if(result?.routes == null) return@launch
                    val route = result.routes[0]
                    decodePolyline(route.polyline.encodedPolyline)
                    if (isLocated()) {
                        changeLocationState(LocationState.LocationOn)
                    }
                    setRouteChanged(true)
                    moveCameraForRoute(route.viewport)
                    formatDistance(route.distanceMeters)
                    formatTime(route.duration)

                }
            }
        }
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
    private fun setOrigin(origin: elfak.mosis.tourguide.domain.models.Place) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(origin = origin))
    }
    private fun setDestination(destination: elfak.mosis.tourguide.domain.models.Place) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(destination = destination))
    }
    fun setDistance(distance: String) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(distance = distance))
    }
    fun setTime(time: String) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(time = time))
    }
    fun setBothLocationsSet(value: Boolean) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(bothLocationsSet = value))
    }
    fun resetTourDetails() {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.clear())
    }

    private fun formatDistance(distance: Int) {
        if(distance > 1000) {
            val distanceKm = convertor.metersToKilometers(distance)
            setDistance("$distanceKm km")
        }
        else {
            setDistance("$distance m")
        }
    }
    private fun formatTime(time: String) {
        val timeExtracted = convertor.extractIntTimeFromString(time)
        if (timeExtracted < 3600) {
            val timeConverted = convertor.secondsToMinutes(timeExtracted)
            setTime("$timeConverted min")
        }
        else if(timeExtracted == 3600) {
            setTime("1 hr")
        }
        else {
            val timeConverted = convertor.secondsToMinutesAndHours(timeExtracted)
            setTime("${timeConverted.first} hr ${timeConverted.second} min")
        }
    }

    private fun decodePolyline(encodedPolyline: String) {
        val decodedPolyline = locationHelper.decodePolyline(encodedPolyline)
        val pointsLatLng: List<LatLng> = decodedPolyline.map { LatLng(it.first, it.second) }
        setPolylinePoints(pointsLatLng)
    }
    private fun setPolylinePoints(polylinePoints: List<LatLng>) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(polylinePoints = polylinePoints))
    }

    //endregion

    // region UISTATE METHODS
    fun setRouteChanged(value: Boolean) {
        uiState = uiState.copy(routeChanged = value)
    }
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
        //used for detecting if the device's location is being tracked
        return uiState.locationState == LocationState.Located
    }

    //endregion

    //region LOCATION HELPER WRAPPER
    fun setLocationCallbacks() {
        locationHelper.setOnLocationResultListener {
            viewModelScope.launch {
                changeMyLocation(LatLng(it.latitude, it.longitude))
                if (!isLocated()) return@launch //skip animation
                onLocationChanged()
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
        val minimalDistanceInMeters = 30 //between two sequential locations, for map move animation
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
    private fun moveCameraForRoute(viewport: Viewport) {
        viewModelScope.launch {
            try {
                val southwest = LatLng(viewport.low.latitude, viewport.low.longitude)
                val northeast = LatLng(viewport.high.latitude, viewport.high.longitude)
                val bounds = LatLngBounds(southwest, northeast)
                uiState.cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds,50),1500
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
    fun onSearchPlaceCLick(place: PlaceAutocompleteResult) {
        chooseLocation(place)
        searchOnMap()
        clearSearchBar()
    }

    fun findPlacesFromInput(query: String, showInDialog: Boolean = false) {
        textInputJob?.cancel()
        textInputJob = viewModelScope.launch {
            delay(300)
            job?.cancel()
            if (query.length < 3) return@launch
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
            PlaceAutocompleteResult(
                address = it.getFullText(null).toString(),
                placeId = it.placeId
            )
        }
    }
    private fun onDialogSearchSuccess(response: FindAutocompletePredictionsResponse) {
        // if got any, populate location list
        locationAutofillDialog += response.autocompletePredictions.map {
            PlaceAutocompleteResult(
                address = it.getFullText(null).toString(),
                placeId = it.placeId
            )
        }
    }

    // choose location from given list
    private fun chooseLocation(location: PlaceAutocompleteResult) {
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

    private suspend fun setLocationsLatLng() {
        var startLocationLatLng = LatLng(0.0,0.0)
        var endLocationLatLng = LatLng(0.0,0.0)
        val invalidLocation = LatLng(0.0,0.0)

        val job = listOf(
            viewModelScope.launch {
                val result = findLocationLatLng(uiState.tourDetails.origin.id) ?: return@launch
                startLocationLatLng = result
            },
            viewModelScope.launch {
                val result = findLocationLatLng(uiState.tourDetails.destination.id) ?: return@launch
                endLocationLatLng = result
            }
        )
        job.joinAll()

        if (startLocationLatLng == invalidLocation) return
        if (endLocationLatLng == invalidLocation) return

        var place = elfak.mosis.tourguide.domain.models.Place(
            uiState.tourDetails.origin.id,
            uiState.tourDetails.origin.address,
            startLocationLatLng
        )
        setOrigin(place)
        place = elfak.mosis.tourguide.domain.models.Place(
            uiState.tourDetails.destination.id,
            uiState.tourDetails.destination.address,
            endLocationLatLng
        )
        setDestination(place)
    }

    private suspend fun findLocationLatLng(placeId: String): LatLng? {
        var placeLatLng: LatLng? = null

        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        try {
            val response = placesClient.fetchPlace(request).await() ?: return null
            placeLatLng = response.place.latLng!!
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return placeLatLng
    }



    fun clearSearchBar() {
        changeSearchValue("")
        locationAutofill.clear()
        setSearchBarVisibility(false)
    }

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