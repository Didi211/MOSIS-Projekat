package elfak.mosis.tourguide.ui.screens.tourScreen

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.PlaceAutocompleteResult
import elfak.mosis.tourguide.data.models.PlaceDetails
import elfak.mosis.tourguide.domain.api.TourGuideApiWrapper
import elfak.mosis.tourguide.domain.helper.GoogleMapHelper
import elfak.mosis.tourguide.domain.helper.LocationHelper
import elfak.mosis.tourguide.domain.helper.PermissionHelper
import elfak.mosis.tourguide.domain.helper.SessionTokenSingleton
import elfak.mosis.tourguide.domain.helper.UnitConvertor
import elfak.mosis.tourguide.domain.models.TourGuideLocationListener
import elfak.mosis.tourguide.domain.models.google.PlaceLatLng
import elfak.mosis.tourguide.domain.models.google.RouteResponse
import elfak.mosis.tourguide.domain.models.google.Viewport
import elfak.mosis.tourguide.domain.models.tour.toTourModel
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.TourRepository
import elfak.mosis.tourguide.ui.components.maps.LocationState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import elfak.mosis.tourguide.domain.models.tour.TourDetails as TourDetails1

@HiltViewModel
class TourScreenViewModel @Inject constructor(
    private val locationHelper: LocationHelper,
    private val placesClient: PlacesClient,
    private val tourGuideApiWrapper: TourGuideApiWrapper,
    private val convertor: UnitConvertor,
    private val sessionTokenSingleton: SessionTokenSingleton,
    private val tourRepository: TourRepository,
    private val authRepository: AuthRepository,
    private val googleMapHelper: GoogleMapHelper,
    private val permissionHelper: PermissionHelper,
    savedStateHandle: SavedStateHandle,
): ViewModel(), TourGuideLocationListener {
    var uiState by mutableStateOf(TourScreenUiState())
        private set

    private var isListenerRegistered by mutableStateOf(false)
    override val name: String
        get() = this::class.simpleName.toString()



    val locationAutofill = mutableStateListOf<PlaceAutocompleteResult>()
    val locationAutofillDialog = mutableStateListOf<PlaceAutocompleteResult>()
    private val placeFields = listOf(
        Place.Field.LAT_LNG,
        Place.Field.ADDRESS,
        Place.Field.NAME,
        Place.Field.TYPES,
        Place.Field.RATING,
        Place.Field.ICON_URL,
        Place.Field.VIEWPORT
    )

    private var job: Job? = null
    private var textInputJob: Job? = null

    init {
        //region get tours
        val editMode: Boolean = savedStateHandle["editMode"]!!
        if (savedStateHandle.contains("tourId")) {
            setTourId(savedStateHandle["tourId"])
        }
        viewModelScope.launch {
            try {
                if (uiState.tourId != null) {
                    val tour = tourRepository.getTour(uiState.tourId!!)
                    setTourDetails(uiState.tourDetails.update(tour))
                    if (uiState.tourDetails.bothLocationsSet) {
                        uiState.tourDetails.onBothLocationsSet(true)
                    }
                    if (editMode) { // user clicked option edit from dropdown
                        setTourState(TourState.CREATING)
                    }
                    else { // user clicked on card
                        setTourState(TourState.VIEWING)
                    }
                }
                else {
                    setTourState(TourState.CREATING)
                }
            }
            catch (ex: Exception) {
                handleError(ex)
            }
        }
        //endregion

        //region tourdetails callbacks
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
                    try {
                        setPolylinePoints(emptyList()) // clearing old route
                        if (uiState.tourDetails.origin.id != uiState.tourDetails.destination.id) {
                            setLocationsLatLng()
                        }
                        val originId = uiState.tourDetails.origin.id
                        val destinationId = uiState.tourDetails.destination.id
                        if(originId == destinationId) {
                            throw Exception("Origin and destination can't be the same for creating a tour!")
                        }

                        val result: RouteResponse? = tourGuideApiWrapper.getRoute(originId, destinationId)
                        // null checking if error has happened
                        if(result?.routes == null) {
                            throw Exception("Couldn't find route.")
                        }

                        val route = result.routes[0]
                        decodePolyline(route.polyline.encodedPolyline)
                        if (isLocated()) {
                            changeLocationState(LocationState.LocationOn)
                        }
                        moveCameraWithBounds(route.viewport)

                        setRouteChanged(true)
                        setDistance(convertor.formatDistance(route.distanceMeters))
                        setTime(convertor.formatTime(route.duration))
                    }
                    catch(ex: Exception) {
                        handleError(ex)
                    }

                }
            }
        }
        //endregion
    }

    private fun setTourId(tourId: String?) {
        uiState = uiState.copy(tourId = tourId)
    }

    override fun onCleared() {
        super.onCleared()
        if (isListenerRegistered) {
            locationHelper.unregisterListener(this.name)
        }
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
    fun setOrigin(origin: elfak.mosis.tourguide.domain.models.Place) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(origin = origin))
    }
    fun setDestination(destination: elfak.mosis.tourguide.domain.models.Place) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(destination = destination))
    }
    private fun setDistance(distance: String) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(distance = distance))
    }
    private fun setTime(time: String) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(time = time))
    }
    private fun setBothLocationsSet(value: Boolean) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(bothLocationsSet = value))
    }
    private fun setTourDetails(tourDetails: TourDetails1) {
        uiState = uiState.copy(tourDetails = tourDetails)
    }



    private fun decodePolyline(encodedPolyline: String) {
        val decodedPolyline = googleMapHelper.decodePolyline(encodedPolyline)
        setPolylinePoints(decodedPolyline)
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

    //region LOCATION HELPER

    fun startLocationUpdates() {
        if(isLocated() || isListenerRegistered) {
           return
        }
        locationHelper.registerListener(this)
        isListenerRegistered = true
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
        val distance = googleMapHelper.distanceInMeter(
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
    private fun moveCameraWithBounds(viewport: Viewport) {
        viewModelScope.launch {
            try {
                val southwest = LatLng(viewport.low.latitude, viewport.low.longitude)
                val northeast = LatLng(viewport.high.latitude, viewport.high.longitude)
                val bounds = LatLngBounds(southwest, northeast)
                uiState.cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds,150),1500
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
    fun findLocationId(latLng: LatLng) {
        // Another way to show poi when id is not provided
        viewModelScope.launch {
            val place = tourGuideApiWrapper.getPlaceId(PlaceLatLng.toLatLng(latLng)) ?: return@launch
            getPOIDetails(place.placeId)
        }
    }
    fun getPOIDetails(placeId: String) {
        val request = FetchPlaceRequest.builder(placeId, placeFields)
            .setSessionToken(sessionTokenSingleton.token)
            .build()
        // find coordinates based on placeId
        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                if (it != null) {
                    changePlaceDetails(placeId, it.place)
                    changeSearchedLocation(it.place.latLng!!)
                    setSearchFlag(true)
                    setTourScreenState(TourScreenState.PLACE_DETAILS)
                    sessionTokenSingleton.invalidateToken()
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun onSearchPlaceCLick(place: PlaceAutocompleteResult) {
        changeSearchValue(place.address)
        searchOnMap(place.placeId)
        setTourScreenState(TourScreenState.PLACE_DETAILS)
        clearSearchBar()
    }

    fun setTourScreenState(state: TourScreenState) {
        uiState = uiState.copy(tourScreenState = state)
    }

    fun findPlacesFromInput(query: String, showInDialog: Boolean = false) {
        if (query.length < 3) return
        textInputJob?.cancel()
        textInputJob = viewModelScope.launch {
            delay(500)
            job?.cancel()
            val targetAutofill = if (showInDialog) locationAutofillDialog else locationAutofill
            targetAutofill.clear()
            job = viewModelScope.launch {
                launchSearchRequest(query, showInDialog)
            }
        }
    }

    private fun launchSearchRequest(query: String, showInDialog: Boolean) {
        val request = FindAutocompletePredictionsRequest
            .builder()
            .setQuery(query)
            .setSessionToken(sessionTokenSingleton.token)
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
        locationAutofill += response.autocompletePredictions.map {
            PlaceAutocompleteResult(
                address = it.getFullText(null).toString(),
                placeId = it.placeId
            )
        }
    }
    private fun onDialogSearchSuccess(response: FindAutocompletePredictionsResponse) {
        locationAutofillDialog += response.autocompletePredictions.map {
            PlaceAutocompleteResult(
                address = it.getFullText(null).toString(),
                placeId = it.placeId
            )
        }
    }

    fun searchOnMap(placeId: String? = null) {
        if (placeId == null) return
        val request = FetchPlaceRequest.builder(placeId, placeFields)
            .setSessionToken(sessionTokenSingleton.token)
            .build()
        // find coordinates based on placeId
        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                if (it != null) {
                    changeSearchedLocation(it.place.latLng!!)
                    changePlaceDetails(placeId, it.place)
                    if (isLocated()) {
                        changeLocationState(LocationState.LocationOn)
                    }
                    setSearchFlag(true)
                    // call animation
                    viewModelScope.launch {
                        moveCameraWithBounds(Viewport(it.place.viewport!!.southwest, it.place.viewport!!.northeast))
                    }
                    sessionTokenSingleton.invalidateToken()
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

        if (startLocationLatLng == invalidLocation || endLocationLatLng == invalidLocation) return

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
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        val response = placesClient.fetchPlace(request).await() ?: return null
        return response.place.latLng!!
    }

    fun clearSearchBar() {
        changeSearchValue("")
        locationAutofill.clear()
        setSearchBarVisibility(false)
    }
    //endregion

    //region DEVICE SETTINGS
    private fun setGps(gpsEnabled: Boolean): Boolean {
        uiState = uiState.copy(deviceSettings = uiState.deviceSettings.copy(gpsEnabled = gpsEnabled))
        return gpsEnabled
    }

    private fun setLocationPermissionStatus(status: Boolean): Boolean {
        uiState = uiState.copy(deviceSettings = uiState.deviceSettings.copy(locationPermissionAllowed = status))
        return status
    }
    fun createLocationPermissions(): List<String> {
        return permissionHelper.createLocationPermissions()
    }
    fun checkGps(): Boolean {
        return setGps(locationHelper.isGpsOn())
    }

    fun checkPermissions(): Boolean {
        return setLocationPermissionStatus(permissionHelper.hasAllowedLocationPermissions())
    }
    //endregion

    //region PLACE DETAILS
    private fun changePlaceDetails(id: String, place: Place) {
        uiState = uiState.copy(placeDetails = PlaceDetails.convert(place))
        uiState = uiState.copy(placeDetails = uiState.placeDetails.copy(id = id))
    }

    //endregion

    //region TOUR REPOSITORY
    fun onSave() {
        // validation
        if (uiState.tourDetails.title.isBlank()) {
            setErrorMessage("Title cannot be empty.")
            return
        }

        viewModelScope.launch {
            try {
                val userId = authRepository.getUserIdLocal()!!
                val successMessage = if (uiState.tourId != null) "updated" else "created"
                if (uiState.tourId != null) {
                    tourRepository.updateTour(uiState.tourId!!, uiState.tourDetails.toTourModel(userId))
                }
                else {
                    tourRepository.createTour(uiState.tourDetails.toTourModel(userId))
                }
                setTourState(TourState.VIEWING)
                setSuccessMessage("Tour successfully $successMessage")
            }
            catch (ex: Exception) { handleError(ex) }

        }
    }
    //endregion

    //region MESSAGE HANDLER
    fun clearErrorMessage() {
        uiState = uiState.copy(toastData = uiState.toastData.copy(hasErrors = false))
    }
    private fun setErrorMessage(message: String) {
        uiState = uiState.copy(toastData = uiState.toastData.copy(errorMessage = message, hasErrors = true))
    }
    private fun setSuccessMessage(message: String) {
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


    // region LocationListener
    override fun onLocationResult(location: Location) {
        viewModelScope.launch {
            changeMyLocation(LatLng(location.latitude, location.longitude))
            if (!isLocated()) return@launch //skip animation
            onLocationChanged()
        }
    }

    override fun onLocationAvailability(available: Boolean) {
        setGps(available)
        if (available) {
            startLocationUpdates()
            changeLocationState(LocationState.Located)
        }
        else {
            changeLocationState(LocationState.LocationOff)
        }
    }
    //endregion
}