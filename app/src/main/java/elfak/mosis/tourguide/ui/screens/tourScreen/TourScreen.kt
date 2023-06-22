@file:OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class,
    ExperimentalAnimationApi::class
)

package elfak.mosis.tourguide.ui.screens.tourScreen

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PersonPinCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.helper.BitmapHelper
import elfak.mosis.tourguide.domain.models.tour.LocationType
import elfak.mosis.tourguide.ui.components.ToastHandler
import elfak.mosis.tourguide.ui.components.bottomsheet.PlaceDetails
import elfak.mosis.tourguide.ui.components.bottomsheet.TourDetails
import elfak.mosis.tourguide.ui.components.maps.ListOfPlaces
import elfak.mosis.tourguide.ui.components.maps.LocationState
import elfak.mosis.tourguide.ui.components.maps.MyLocationButton
import elfak.mosis.tourguide.ui.components.maps.SearchField
import elfak.mosis.tourguide.ui.components.scaffold.MenuViewModel
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideFloatingButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import elfak.mosis.tourguide.ui.theme.RouteBlue
import elfak.mosis.tourguide.ui.theme.RouteBorderBlue
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@Composable
fun TourScreen(
    viewModel: TourScreenViewModel,
    navController: NavController
) {
    val menuViewModel = hiltViewModel<MenuViewModel>()
    val context = LocalContext.current
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed),
    )
    val coroutineScope = rememberCoroutineScope()
    var permissionAlreadyRequested by rememberSaveable {
        mutableStateOf(false)
    }
    val permissionsState = rememberMultiplePermissionsState(
        permissions = viewModel.createLocationPermissions(),
        onPermissionsResult = {
            viewModel.checkPermissions()
            permissionAlreadyRequested = true
        }
    )

    val focusManager = LocalFocusManager.current

    if(bottomSheetScaffoldState.bottomSheetState.isExpanded) {
        viewModel.setSearchBarVisibility(false)
    }

    ToastHandler(
        toastData = viewModel.uiState.toastData,
        clearErrorMessage = viewModel::clearErrorMessage,
        clearSuccessMessage = viewModel::clearSuccessMessage
    )


    BottomSheetScaffold(
        sheetContent = {
            AnimatedContent(targetState = viewModel.uiState.tourScreenState) { state ->
                when (state) {
                    TourScreenState.TOUR_DETAILS -> TourDetails(
                        state = viewModel.uiState.tourState,
                        tourDetails = viewModel.uiState.tourDetails,
                        onSave = { viewModel.onSave() },
                        onEdit = { viewModel.setTourState(TourState.EDITING) },
                        onCancel = { viewModel.setTourState(TourState.VIEWING) },
                        placesList = viewModel.locationAutofillDialog,
                        searchForPlaces = { query ->
                            viewModel.findPlacesFromInput(query, true)
                        },
                    )
                    TourScreenState.PLACE_DETAILS -> PlaceDetails(
                        tourState = viewModel.uiState.tourState,
                        placeDetails = viewModel.uiState.placeDetails,
                        onCancel = {
                            viewModel.setTourScreenState(TourScreenState.TOUR_DETAILS)
                            viewModel.setSearchFlag(false)
                        },
                        onAddToTour = { place, locationType ->
                            when (locationType) {
                                LocationType.Origin -> { viewModel.setOrigin(place) }
                                LocationType.Destination -> { viewModel.setDestination(place) }
                                LocationType.Waypoint -> { Toasty.info(context,"feature_under_development").show() }
                            }
                            viewModel.setTourScreenState(TourScreenState.TOUR_DETAILS)
                            viewModel.setSearchFlag(false)
                            if(viewModel.uiState.tourDetails.origin.id.isNotBlank()
                                && viewModel.uiState.tourDetails.destination.id.isNotBlank()) {
                                viewModel.uiState.tourDetails.onBothLocationsSet(true)
                            }

                        }
                    )

                }
            }
        },
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        scaffoldState = bottomSheetScaffoldState,
//        sheetBackgroundColor = MaterialTheme.colors.background,
        // top navigation bar with menu button
        topBar = {
            TourGuideTopAppBar(
                title = stringResource(id = R.string.tour),
                coroutineScope = coroutineScope,
                scaffoldState = bottomSheetScaffoldState,
            )
        },
        // menu content
        drawerContent = {
            TourGuideNavigationDrawer(
                navController = navController,
                menuViewModel = menuViewModel
            )
        },
        drawerGesturesEnabled = bottomSheetScaffoldState.drawerState.isOpen,
    ) {
        /** MAIN CONTENT */


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            if ((viewModel.uiState.cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE)
                && (viewModel.isLocated())
            ) {
                // change icon if user moved map
                viewModel.changeLocationState(LocationState.LocationOn)
            }

            // buttons
            val columnArrangement = if (viewModel.uiState.friends.isEmpty()) {
                Arrangement.Bottom
            } else {
                Arrangement.SpaceBetween
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .zIndex(100f),
                verticalArrangement = columnArrangement,
                horizontalAlignment = Alignment.End
            ) {
                if (viewModel.uiState.friends.isNotEmpty()) {
                    //show friends button
                    AnimatedContent(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(start = 30.dp, top = 10.dp, end = 10.dp),
                        targetState = viewModel.uiState.showFriends
                    ) { showFriends ->
                        val contentColor: Color = when(showFriends) {
                            true -> MaterialTheme.colors.primary
                            false -> Color.LightGray
                        }
                        TourGuideFloatingButton(
                            icon = Icons.Rounded.PersonPinCircle,
                            contentDescription = stringResource(id = R.string.show_friends),
                            backgroundColor = Color.White,
                            contentColor = contentColor,
                        ) {
                            viewModel.toggleShowFriends()
                        }
                    }
                }
                // location and search button
                Column(
                    modifier = Modifier.padding(start = 30.dp, bottom = 10.dp, end = 10.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        ListOfPlaces(
                            placesList = viewModel.locationAutofill,
                            onPlaceClick = { place ->
                                viewModel.onSearchPlaceCLick(place)
                                focusManager.clearFocus()
                            },
                        )

                        // my location button
                        MyLocationButton(viewModel.uiState.locationState) {
                            coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.collapse() }
                            locateMe(
                                viewModel,
                                permissionAlreadyRequested,
                                permissionsState,
                                context,
                            )
                        }
                    }
                    Spacer(Modifier.height(15.dp))

                    // search button
                    AnimatedContent(
                        targetState = viewModel.uiState.showSearchBar,
                    ) { showBar ->
                        when (showBar) {
                            false -> TourGuideFloatingButton(
                                contentDescription = stringResource(id = R.string.search),
                                icon = Icons.Rounded.Search,
                                onClick = {
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                        viewModel.setSearchBarVisibility(true)
                                    }
                                },
                            )
                            true -> SearchField(
                                onSearch = {
                                    viewModel.searchOnMap()
                                },
                                text = viewModel.uiState.searchValue,
                                onTextChanged = { searchValue ->
                                    viewModel.changeSearchValue(searchValue)
                                    viewModel.findPlacesFromInput(searchValue)
                                },
                                label = stringResource(id = R.string.search_here) + ":"
                            )
                        }
                    }
                }
            }



            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                ),
                properties = MapProperties(
                    mapType = MapType.NORMAL,
                ),
                cameraPositionState = viewModel.uiState.cameraPositionState,
                onMapLoaded = {
                    if (!viewModel.checkPermissions()) {
                        return@GoogleMap
                    }
                    if (!viewModel.checkGps()) {
                        return@GoogleMap
                    }
                    viewModel.startLocationUpdates()
                    if (viewModel.uiState.tourDetails.bothLocationsSet) {
                        viewModel.changeLocationState(LocationState.LocationOn)
                        return@GoogleMap
                    }
                    viewModel.changeLocationState(LocationState.Located)
                },
                onMapClick = { latLng ->
                    viewModel.clearSearchBar()
                    viewModel.findLocationId(latLng)
                },
                onPOIClick = { poi ->
                    viewModel.clearSearchBar()
                    viewModel.getPOIDetails(poi.placeId)
                },
            ) {
                // my location
                Marker(
                    icon = BitmapHelper.bitmapDescriptorFromVector(
                        context,
                        R.drawable.my_location
                    ),
                    state = MarkerState(position = viewModel.uiState.myLocation),
                    visible = viewModel.uiState.deviceSettings.gpsEnabled,
                    onClick = { marker ->
                        viewModel.findLocationId(marker.position)
                        true
                    }
                )
                // point of interest
                Marker(
                    state = MarkerState(position = viewModel.uiState.searchedLocation),
                    visible = viewModel.uiState.isSearching,
                )
                // route
                if(viewModel.uiState.tourDetails.bothLocationsSet) {
                    LaunchedEffect(viewModel.uiState.routeChanged) {
                        bottomSheetScaffoldState.bottomSheetState.collapse()
                        viewModel.setRouteChanged(false)
                    }
//                    val pattern = listOf(Dot(), Gap(10f)) // pattern for walking mode
                    //border of the route
                    Polyline(
                        points = viewModel.uiState.tourDetails.polylinePoints,
//                        pattern = pattern
                        color = RouteBorderBlue,
                        width = 25f,
                        startCap = RoundCap(),
                        endCap = RoundCap()
                    )
                    //route
                    Polyline(
                        points = viewModel.uiState.tourDetails.polylinePoints,
//                        pattern = pattern
                        color = RouteBlue,
                        width = 15f,
                        startCap = RoundCap(),
                        endCap = RoundCap()
                    )
                    Marker(
                        state = MarkerState(position = viewModel.uiState.tourDetails.destination.location),
                    )
                 }
                if (viewModel.uiState.friends.isNotEmpty()) {
                    for (friend in viewModel.uiState.friends) {
                        val latLng = LatLng(friend.location.coordinates.latitude, friend.location.coordinates.longitude)
                        Marker(
                            MarkerState(position = latLng),
                            visible = viewModel.uiState.showFriends,
                            title =  "Current location: ${friend.location.coordinates}",
                            icon = BitmapDescriptorFactory.defaultMarker(87f)
                        )
                    }
                }
            }
        }
    }
}

fun showDeniedPermissionMessage(context: Context, @StringRes message: Int) {
    Toasty.error(context, message, Toast.LENGTH_LONG).show()
}


private fun locateMe(
    viewModel: TourScreenViewModel,
    permissionAlreadyRequested: Boolean,
    permissionsState: MultiplePermissionsState,
    context: Context,
) {
    // check permissions
    if (!viewModel.checkPermissions()) {
        viewModel.changeLocationState(LocationState.LocationOff)
        // ask for permissions
        if (!permissionAlreadyRequested || permissionsState.shouldShowRationale) {
            permissionsState.launchMultiplePermissionRequest()
            return
        }
        showDeniedPermissionMessage(context, R.string.permissions_denied_twice)
        return
    }
    // check gps
    if (!viewModel.checkGps()) {
        // ask for gps
        Toasty.error(context, R.string.location_needed).show()
        viewModel.changeLocationState(LocationState.LocationOff)
        return
    }
    // turn on gps tracking
    viewModel.startLocationUpdates()
    // change mode to LOCATED
    viewModel.changeLocationState(LocationState.Located)
    // move camera
    viewModel.onLocationChanged()
}