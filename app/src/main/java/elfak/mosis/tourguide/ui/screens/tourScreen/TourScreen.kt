@file:OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class,
    ExperimentalAnimationApi::class
)

package elfak.mosis.tourguide.ui.screens.tourScreen

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.*
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.helper.BitmapHelper
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
    navController: NavController,
    tourId: String?
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

    if (viewModel.uiState.hasErrors) {
        Toasty.error(LocalContext.current, viewModel.uiState.errorMessage, Toast.LENGTH_LONG, true).show()
        viewModel.clearErrorMessage()
    }


    BottomSheetScaffold(
        sheetContent = {
            AnimatedContent(targetState = viewModel.uiState.tourScreenState) { state ->
                when (state) {
                    TourScreenState.TOUR_DETAILS -> TourDetails(
                        state = viewModel.uiState.tourState,
                        tourDetails = viewModel.uiState.tourDetails,
                        onSave = { viewModel.setTourState(TourState.VIEWING) },
                        onEdit = { viewModel.setTourState(TourState.EDITING) },
                        onCancel = { viewModel.setTourState(TourState.VIEWING) },
                        placesList = viewModel.locationAutofillDialog,
                        searchForPlaces = { query ->
                            viewModel.findPlacesFromInput(query, true)
                        },
                    )
                    TourScreenState.PLACE_DETAILS -> PlaceDetails(
                        placeDetails = viewModel.uiState.placeDetails,
                        onCancel = {
                            viewModel.setTourScreenState(TourScreenState.TOUR_DETAILS)
                            viewModel.setSearchFlag(false)
                        },
                        onAddToTour = { place ->
                            viewModel.setDestination(place)
                            viewModel.setTourScreenState(TourScreenState.TOUR_DETAILS)
                            viewModel.setSearchFlag(false)
                            if(viewModel.uiState.tourDetails.origin.id.isNotBlank()) {
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
        floatingActionButton = {
            Column(
                modifier = Modifier.padding(start = 30.dp),
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
                            onTextChanged = {
                                viewModel.changeSearchValue(it)
                                viewModel.findPlacesFromInput(it)
                            },
                            label = stringResource(id = R.string.search_here) + ":"
                        )
                    }
                }
            }

        }
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
                viewModel.changeLocationState(LocationState.LocationOn)
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
                    viewModel.setLocationCallbacks()
                    if (!viewModel.checkPermissions()) {
                        return@GoogleMap
                    }
                    if (!viewModel.checkGps()) {
                        return@GoogleMap
                    }
                    viewModel.startLocationUpdates()
                    viewModel.changeLocationState(LocationState.Located)
                },
                onMapClick = { latlng ->
                    viewModel.clearSearchBar()
                    viewModel.findLocationId(latlng)
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
                    visible = viewModel.uiState.deviceSettings.gpsEnabled
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
        showDeniedPermissionMessage(context, R.string.permission_denied_twice)
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