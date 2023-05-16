@file:OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class,
    ExperimentalAnimationApi::class
)

package elfak.mosis.tourguide.ui.screens.tourScreen

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.models.Place
import elfak.mosis.tourguide.ui.components.bottomsheet.TourDetails
import elfak.mosis.tourguide.ui.components.maps.*
import elfak.mosis.tourguide.ui.components.scaffold.*
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@Composable
fun TourScreen(
    viewModel: TourScreenViewModel,
) {

    val context = LocalContext.current
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Expanded),
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

    // testing how to prepare data for certain state
    LaunchedEffect(viewModel.uiState.tourState) {
        when(viewModel.uiState.tourState) {
            TourState.VIEWING -> viewModel.setDistance("15km")
            TourState.EDITING -> viewModel.setDistance("40km")
            TourState.CREATING -> viewModel.resetTourDetails()
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            TourDetails(
                state = viewModel.uiState.tourState,
                tourDetails = viewModel.uiState.tourDetails,
                onSave = { viewModel.setTourState(TourState.VIEWING) },
                onEdit = { viewModel.setTourState(TourState.EDITING) },
                onCancel = { viewModel.setTourState(TourState.VIEWING) },
                placesList = viewModel.locationAutofillDialog,
                searchForPlaces = { query ->
                    viewModel.findPlacesFromInput(query, true)
                },

        ) },
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
                coroutineScope = coroutineScope,
                scaffoldState = bottomSheetScaffoldState
                // menuItems
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
                onMapClick = {
                    viewModel.clearSearchBar()
                }

            ) {
                // my location
                Marker(
                    icon = viewModel.bitmapHelper.bitmapDescriptorFromVector(
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
                    Polyline(
                        points = listOf(
                            viewModel.uiState.tourDetails.startLocation.location,
                            viewModel.uiState.tourDetails.endLocation.location
                        )
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
    viewModel.onLocationChanged(true)
}