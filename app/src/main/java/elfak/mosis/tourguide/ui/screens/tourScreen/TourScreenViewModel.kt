package elfak.mosis.tourguide.ui.screens.tourScreen

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
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
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.MyLatLng
import elfak.mosis.tourguide.data.models.PlaceAutocompleteResult
import elfak.mosis.tourguide.data.models.PlaceDetails
import elfak.mosis.tourguide.data.models.UserLocation
import elfak.mosis.tourguide.data.models.tour.category.NearbyPlaceResult
import elfak.mosis.tourguide.domain.api.TourGuideApiWrapper
import elfak.mosis.tourguide.domain.helper.GoogleMapHelper
import elfak.mosis.tourguide.domain.helper.LocationHelper
import elfak.mosis.tourguide.domain.helper.PermissionHelper
import elfak.mosis.tourguide.domain.helper.SessionTokenSingleton
import elfak.mosis.tourguide.domain.helper.UnitConvertor
import elfak.mosis.tourguide.domain.helper.ValidationHelper
import elfak.mosis.tourguide.domain.models.TourGuideLocationListener
import elfak.mosis.tourguide.domain.models.google.RouteResponse
import elfak.mosis.tourguide.domain.models.google.Viewport
import elfak.mosis.tourguide.domain.models.google.toPlaceLatLng
import elfak.mosis.tourguide.domain.models.tour.CategoryMarker
import elfak.mosis.tourguide.domain.models.tour.LocationType
import elfak.mosis.tourguide.domain.models.tour.toTourModel
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.TourRepository
import elfak.mosis.tourguide.domain.repository.UsersRepository
import elfak.mosis.tourguide.ui.components.maps.FriendMarker
import elfak.mosis.tourguide.ui.components.maps.LocationState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.burnoutcrew.reorderable.ItemPosition
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
    private val usersRepository: UsersRepository,
    private val validationHelper: ValidationHelper,
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
        runBlocking {
            val userId = authRepository.getUserIdLocal()
            if (userId == null) {
                setUserId("")
                return@runBlocking
            }
            setUserId(userId)
        }
        if (uiState.userId.isNotBlank()) {
            //region get tour details

            val editMode: Boolean = savedStateHandle["editMode"]!!
            if (savedStateHandle.contains("tourId")) {
                setTourId(savedStateHandle["tourId"])
            }
            viewModelScope.launch {
                try {
                    if (uiState.tourId != null) {
                        val tour = tourRepository.getTour(uiState.tourId!!)
                        setTourDetails(uiState.tourDetails.update(tour))
                        if (uiState.tourDetails.shouldRedrawRoute) {
                            uiState.tourDetails.onRouteRedraw(true)
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

            //region get friends markers

            if (uiState.tourId != null) {
                viewModelScope.launch {
                    val ids = tourRepository.getFriendsIds(uiState.tourId!!, uiState.userId)
                    if (ids.isEmpty())
                        return@launch
                    val usersFlow = usersRepository.getUsers(ids)
                    usersFlow.collect { users ->
                        setFriends(users.map { user -> user.toFriendMarker() })
                    }
                }
            }

            //endregion

            //region tourdetails callbacks
            uiState.tourDetails.onTitleChanged = { setTitle(it) }
            uiState.tourDetails.onSummaryChanged = { setSummary(it) }
            uiState.tourDetails.onOriginChanged = { place ->
                if (uiState.tourDetails.destination == place) {
                    throw Exception("Place is already added as a destination.")
                }
                if (isAddedToWaypoint(place)) {
                    throw Exception("Place is already added as a waypoint.")
                }
                setOrigin(place)
            }
            uiState.tourDetails.onDestinationChanged = { place ->
                if (uiState.tourDetails.origin == place) {
                    throw Exception("Place is already added as a origin.")
                }
                if (isAddedToWaypoint(place)) {
                    throw Exception("Place is already added as a waypoint.")
                }
                setDestination(place)
            }
            uiState.tourDetails.onDistanceChanged = { setDistance(it) }
            uiState.tourDetails.onWaypointAdded = { place ->
                if (uiState.tourDetails.origin == place) {
                    throw Exception("Place is already added as a origin.")
                }
                if (uiState.tourDetails.destination == place) {
                    throw Exception("Place is already added as a destination.")
                }
                if (uiState.tourDetails.waypoints.count() == 5) {
                    throw Exception ("Max 5 stops can be added.")
                }
                if (isAddedToWaypoint(place)) {
                    throw Exception ("Place is already added.")
                }
                addWaypointToList(place)
            }
            uiState.tourDetails.onWaypointRemoved = {
                removeWaypointFromList(it)
                if (isRouteRedrawNeeded()) {
                    uiState.tourDetails.onRouteRedraw(true)
                }
            }
            uiState.tourDetails.onTimeChanged = { setTime(it) }
            uiState.tourDetails.onRouteRedraw = { redraw ->
                setShouldRedrawRoute(redraw)
                if (redraw) {
                    viewModelScope.launch {
                        try {
//                            setPolylinePoints(emptyList()) // clearing old route
                            if (uiState.tourDetails.origin.id != uiState.tourDetails.destination.id) {
                                setLocationsLatLng()
                            }
                            val originId = uiState.tourDetails.origin.id
                            val destinationId = uiState.tourDetails.destination.id
                            if(originId == destinationId) {
                                throw Exception("Origin and destination can't be the same for creating a tour!")
                            }

                            val waypoints = uiState.tourDetails.waypoints.map { p -> p.id }
                            val result: RouteResponse? = tourGuideApiWrapper.getRoute(originId, destinationId, waypoints)
                            // null checking if error has happened
                            if(result?.routes == null) {
                                throw Exception("Couldn't find route.")
                            }

                            val route = result.routes[0]
                            decodePolyline(route.polyline.encodedPolyline)
                            if (isLocated()) {
                                changeLocationState(LocationState.LocationOn)
                            }
                            moveCameraWithViewport(route.viewport)

                            setRouteChanged(true)
                            setDistance(convertor.formatDistance(route.distanceMeters))
                            setTime(convertor.formatTime(route.duration))
                        }
                        catch(ex: Exception) {
                            handleError(ex)
                            setShouldRedrawRoute(false)
                        }

                    }
                }
            }
            uiState.tourDetails.onBothLocationsSet = {
                setBothLocationsSet(it)
                if (it && uiState.tourDetails.shouldRedrawRoute) {
                    uiState.tourDetails.onRouteRedraw(true)
                }
            }
            //endregion
        }
    }



    suspend fun isAuthenticated(): Boolean {
        return authRepository.getUserIdLocal() != null
    }

    override fun onCleared() {
        super.onCleared()
        if (isListenerRegistered) {
            locationHelper.unregisterListener(this.name)
        }
    }

    //region TOUR DETAILS
    private fun setShouldRedrawRoute(redraw: Boolean) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(shouldRedrawRoute = redraw))
    }
    private fun setFriends(friends: List<FriendMarker>) {
        uiState = uiState.copy(friends = friends)
    }
    private fun setUserId(id: String) {
        uiState = uiState.copy(userId = id)
    }
    fun setTourState(state: TourState) {
        uiState = uiState.copy(tourState = state)
    }
    private fun setTitle(title: String) {
        if (title.length > 25) {
            setErrorMessage("Title can't be longer than 25 characters.")
            return
        }
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
    private fun addWaypointToList(place: elfak.mosis.tourguide.domain.models.Place) {
        // setting waypoints before new change
//        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(
//            previousWaypoints = uiState.tourDetails.waypoints
//        ))
//
//        //changing current waypoints
//        val newList = uiState.tourDetails.waypoints + place
//        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(
//            waypoints = newList
//        ))
//        val newList = uiState.tourDetails.waypoints + place
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(
            previousWaypoints = uiState.tourDetails.waypoints,
            waypoints = uiState.tourDetails.waypoints + place,
        ))
    }
    private fun removeWaypointFromList(place: elfak.mosis.tourguide.domain.models.Place) {
//        uiState.tourDetails.waypoints.remove(place)
        val newList = uiState.tourDetails.waypoints.filter { p -> p.id != place.id }
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(
            previousWaypoints = uiState.tourDetails.waypoints,
            waypoints = newList
        ))
    }

    private fun isAddedToWaypoint(place: elfak.mosis.tourguide.domain.models.Place): Boolean {
        val found = uiState.tourDetails.waypoints
            .find { p -> p.id == place.id }
        return found != null
    }


    private fun decodePolyline(encodedPolyline: String) {
        val decodedPolyline = googleMapHelper.decodePolyline(encodedPolyline)
        setPolylinePoints(decodedPolyline)
    }
    private fun setPolylinePoints(polylinePoints: List<LatLng>) {
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(polylinePoints = polylinePoints))
    }

    fun swapWaypointPlaces(from: ItemPosition, to: ItemPosition) {
        val reorderedList = uiState.tourDetails.waypoints.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        uiState = uiState.copy(tourDetails = uiState.tourDetails.copy(
            waypoints = reorderedList.toList()
        ))
        uiState.tourDetails.onRouteRedraw(true)
    }
    //endregion

    // region UISTATE METHODS

    private fun setCategoryResults(results: List<NearbyPlaceResult>) {
        uiState = uiState.copy(categorySearchResult = results)
    }
    private fun setTourId(tourId: String?) {
        uiState = uiState.copy(tourId = tourId)
    }
    private fun setShowFriends(value: Boolean) {
        uiState = uiState.copy(showFriends = value)
    }
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
            uiState.myLocation,
            LatLng(currentCameraPosition.latitude, currentCameraPosition.longitude)
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
    private fun moveCameraWithViewport(viewport: Viewport) {
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
    private fun moveCameraWithBounds(bounds: LatLngBounds) {
        viewModelScope.launch {
            try {
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




    fun getPOIDetailsFromLatLng(latLng: LatLng) {
        // Another way to show poi when id is not provided
        viewModelScope.launch {
            val place = tourGuideApiWrapper.getPlaceId(latLng.toPlaceLatLng()) ?: return@launch
//            getPOIDetails(place.placeId)
            getPlaceDetails(place.placeId) { response ->
//                changePlaceDetails(place.placeId, response.place)
                changeSearchedLocation(response.place.latLng!!)
                setSearchFlag(true)
            }
        }
    }
    fun getPOIDetailsFromId(placeId: String) {
        viewModelScope.launch {
            getPlaceDetails(placeId) { response ->
//                changePlaceDetails(placeId, response.place)
                changeSearchedLocation(response.place.latLng!!)
                setSearchFlag(true)
            }
        }
    }

    private fun getPlaceDetails(placeId: String, onSuccess: (FetchPlaceResponse) -> Unit ) {
        val request = FetchPlaceRequest.builder(placeId, placeFields)
            .setSessionToken(sessionTokenSingleton.token)
            .build()
        // find coordinates based on placeId
        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                if (it != null) {
                    changePlaceDetails(placeId, it.place)
                    onSuccess(it)
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
                        moveCameraWithViewport(Viewport(it.place.viewport!!.southwest, it.place.viewport!!.northeast))
                    }
                    sessionTokenSingleton.invalidateToken()
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private suspend fun setLocationsLatLng() {
        var originLatLng = LatLng(0.0,0.0)
        var destinationLatLng = LatLng(0.0,0.0)
        val invalidLocation = LatLng(0.0,0.0)

        val job = listOf(
            viewModelScope.launch {
                var result: LatLng? = uiState.tourDetails.origin.location
                if (uiState.tourDetails.origin.location == invalidLocation) {
                    result = findLocationLatLng(uiState.tourDetails.origin.id) ?: return@launch
                }
                originLatLng = result!!
            },
            viewModelScope.launch {
                var result: LatLng? = uiState.tourDetails.destination.location
                if (uiState.tourDetails.destination.location == invalidLocation)
                    result = findLocationLatLng(uiState.tourDetails.destination.id) ?: return@launch
                destinationLatLng = result!!
            }
        )
        job.joinAll()

        if (originLatLng == invalidLocation || destinationLatLng == invalidLocation) return

        var place = elfak.mosis.tourguide.domain.models.Place(
            uiState.tourDetails.origin.id,
            uiState.tourDetails.origin.address,
            originLatLng
        )
        setOrigin(place)
        place = elfak.mosis.tourguide.domain.models.Place(
            uiState.tourDetails.destination.id,
            uiState.tourDetails.destination.address,
            destinationLatLng
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
    private fun changePlaceDetailsFromNearbyResult(place: NearbyPlaceResult) {
        uiState = uiState.copy(placeDetails = place.toPlaceDetails())
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
                    val tourId = tourRepository.createTour(uiState.tourDetails.toTourModel(userId))
                    tourRepository.addFriendToTour(tourId, userId)
                    setTourId(tourId)
                }
                setTourState(TourState.VIEWING)
                setSuccessMessage("Tour successfully $successMessage")
            }
            catch (ex: Exception) { handleError(ex) }

        }
    }

    fun addPlaceToTour(place: elfak.mosis.tourguide.domain.models.Place, locationType: LocationType): Boolean {
        try {
            when (locationType) {
                LocationType.Origin -> { uiState.tourDetails.onOriginChanged(place) }
                LocationType.Destination -> { uiState.tourDetails.onDestinationChanged(place) }
                LocationType.Waypoint -> { uiState.tourDetails.onWaypointAdded(place) }
            }
            if (isRouteRedrawNeeded()) {
                uiState.tourDetails.onRouteRedraw(true)
            }
            return true
        }
        catch(ex: Exception) {
            ex.message?.let { setErrorMessage(it) }
            return false
        }
    }

    private fun isRouteRedrawNeeded(): Boolean {
        val bothLocationsSet = uiState.tourDetails.origin.isSet() && uiState.tourDetails.destination.isSet()
//        var waypointsChanged = uiState.tourDetails.previousWaypoints.count() != uiState.tourDetails.waypoints.count()
        if (bothLocationsSet)
            return true
//        if (waypointsChanged)
//            return true
//        waypointsChanged = uiState.tourDetails.previousWaypoints == uiState.tourDetails.waypoints
//        if (waypointsChanged)
//            return true
        return false
    }

    //endregion

    //region MESSAGE HANDLER
    fun clearErrorMessage() {
        uiState = uiState.copy(toastData = uiState.toastData.copy(hasErrors = false))
    }
    fun setErrorMessage(message: String) {
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
        // also update user location since gps is on
        viewModelScope.launch {
            val userId = async { authRepository.getUserIdLocal()!! }.await()
            usersRepository.updateUserLocation(userId, UserLocation(
                coordinates = MyLatLng(location.latitude, location.longitude)
            ))
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

    // region Friends
    fun toggleShowFriends() {
        setShowFriends(!uiState.showFriends)
        if (uiState.friends.isEmpty() || !uiState.showFriends)
            return

        // animate to see all friends
        val builder = googleMapHelper.createLatLngBounds(uiState.friends.map { friend ->
            friend.location.coordinates.toGoogleLatLng()
        })
        if (uiState.deviceSettings.gpsEnabled) {
            builder.include(uiState.myLocation)
        }

        val bounds = builder.build()
        moveCameraWithBounds(bounds)
        if (uiState.locationState == LocationState.Located)
            changeLocationState(LocationState.LocationOn)
    }

    fun callFriend(context: Context, phone: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phone")
        context.startActivity(intent)
    }
    //endregion

    // Category Search
    private fun mockSearchResult(): List<CategoryMarker> {
        return listOf(
            CategoryMarker(location = LatLng(43.317274, 21.904856)),
            CategoryMarker(location = LatLng(43.320582, 21.911785)),
            CategoryMarker(location = LatLng(43.317326, 21.886451)),
            CategoryMarker(location = LatLng(43.316539, 21.921167)),
            CategoryMarker(location = LatLng(43.318178, 21.903233)),
        )
    }

    fun searchByCategory(category: String, radius: Int) {
        val latLng = uiState.cameraPositionState.position.target.toPlaceLatLng()
        viewModelScope.launch {
            val results = tourGuideApiWrapper.getNearbyPlaces(latLng, radius,category)
            setCategoryResults(results)
            if (uiState.categorySearchResult.isEmpty()) {
                setErrorMessage("No places found with desired filers.")
                return@launch
            }
            val builder = googleMapHelper
                .createLatLngBounds(uiState.categorySearchResult.map { place ->
                    place.location.toLatLng()
            })
            val bounds = builder.build()
            moveCameraWithBounds(bounds)

        }
    }

    fun validateCategoryFilter(category: String, radius: String): Boolean {
        try {
            validationHelper.validateCategoryFilter(category, radius)
            return true
        }
        catch (ex: Exception) {
            ex.message?.let { setErrorMessage(it) }
            return false
        }
    }

    fun getCategoryResultDetails(place: NearbyPlaceResult) {
        changePlaceDetailsFromNearbyResult(place)
        setTourScreenState(TourScreenState.PLACE_DETAILS)
    }

    fun selectMarker(place: NearbyPlaceResult) {
        val newList = uiState.categorySearchResult.map { marker ->
            if (marker.location == place.location) {
                marker.copy(selected = true)
            }
            else marker.copy(selected = false)
        }
        setCategoryResults(results = newList)
    }

    //endregion
}