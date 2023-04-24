@file:OptIn(ExperimentalPermissionsApi::class)

package elfak.mosis.tourguide.ui.screens.tourScreen

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.maps.LocationState
import elfak.mosis.tourguide.ui.components.maps.MyLocationButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideFloatingButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TourScreen(
    viewModel: TourScreenViewModel,
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val permissionsState = rememberMultiplePermissionsState(
        permissions = viewModel.createLocationPermissions()
    )
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(viewModel.uiState.currentLocation,10f,0f,0f)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        // top navigation bar with menu button
        topBar = {
            TourGuideTopAppBar(
                title = stringResource(id = R.string.tour),
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState,
            )
        },
        // menu content
        drawerContent = {
            TourGuideNavigationDrawer(
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState
                // menuItems
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        floatingActionButton = {
            Column {
                // my location button
                MyLocationButton(viewModel.uiState.locationState) {
                    coroutineScope.launch {
                        if (permissionsState.allPermissionsGranted) {
                            if (viewModel.uiState.gpsEnabled) {
                                locateAndRepositionCamera(viewModel, cameraPositionState)
                            }
                            else {
                                // TODO - launch gps request
                                val t = 3
                            }
                        }
                        else {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    }
                }
                Spacer(Modifier.height(15.dp))

                // search button
                TourGuideFloatingButton(
                    contentDescription = stringResource(id = R.string.add),
                    icon = Icons.Rounded.Search,
                    onClick = {
                        /* TODO - Search locations */
                        viewModel.enableGps()
                    }
                )
            }

        }
    ) {
        MainContent(
            viewModel = viewModel,
            padding = it,
            permissionsState = permissionsState,
            cameraPositionState = cameraPositionState,
        )
    }
}


@Composable
fun MainContent(
    viewModel: TourScreenViewModel,
    padding: PaddingValues,
    permissionsState: MultiplePermissionsState,
    cameraPositionState: CameraPositionState,
) {
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {

        // Location icon setting
        LocationButtonState(
            viewModel = viewModel,
            permissionsState = permissionsState,
            cameraPositionState = cameraPositionState
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
            ),
            properties = MapProperties(
                mapType = MapType.NORMAL,
            ),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                viewModel.setLocationCallback()
                coroutineScope.launch {
                    if (viewModel.uiState.gpsEnabled) {
                        viewModel.onLocationChanged(cameraPositionState)
                    }
                }
            }

        ) {
            if (viewModel.uiState.gpsEnabled) {
                Marker(
                    state = MarkerState(position = viewModel.uiState.currentLocation),
                    title = "My address - " +
                            "${viewModel.uiState.currentLocation.latitude} - " +
                            "${viewModel.uiState.currentLocation.longitude}",
    //                draggable = true
                )
            }
        }
    }
}

@Composable
fun LocationButtonState(viewModel: TourScreenViewModel, permissionsState: MultiplePermissionsState, cameraPositionState: CameraPositionState) {
    if (permissionsState.allPermissionsGranted && viewModel.uiState.gpsEnabled) {
        if (!cameraPositionState.isMoving) {
            val distance = viewModel.locationHelper.distanceInMeter(
                startLat = viewModel.uiState.currentLocation.latitude,
                startLon = viewModel.uiState.currentLocation.longitude,
                endLat = cameraPositionState.position.target.latitude,
                endLon = cameraPositionState.position.target.longitude
            )
            if (distance <= 1) {
                viewModel.changeLocationState(LocationState.Located)
            }
        }
        else {
            viewModel.changeLocationState(LocationState.LocationOn)
        }
    }
    else {
        viewModel.changeLocationState(LocationState.LocationOff)
    }
}

suspend fun locateAndRepositionCamera(
    viewModel: TourScreenViewModel,
    cameraPositionState: CameraPositionState,
) {
    if (!viewModel.uiState.isTrackingLocation) {
        viewModel.startLocationUpdates()
        delay(1000)
    }
    viewModel.onLocationChanged(cameraPositionState)
}





