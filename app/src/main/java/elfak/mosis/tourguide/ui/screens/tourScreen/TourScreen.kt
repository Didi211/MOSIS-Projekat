package elfak.mosis.tourguide.ui.screens.tourScreen

import android.content.Context
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()

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
                    composableScope.launch {
                        locateAndRepositionCamera(viewModel, cameraPositionState)
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
            context = context
        )
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContent(
    viewModel: TourScreenViewModel,
    padding: PaddingValues,
    permissionsState: MultiplePermissionsState,
    cameraPositionState: CameraPositionState,
    context: Context
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {

        Text(text = "Is moving - ${cameraPositionState.isMoving}")

        // Location icon setting
        if (permissionsState.allPermissionsGranted && viewModel.uiState.gpsEnabled) {
            if (!cameraPositionState.isMoving) {
                val distance = distanceInMeter(
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

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
//                myLocationButtonEnabled = true
            ),
            properties = MapProperties(
                mapType = MapType.NORMAL,
//                isMyLocationEnabled = true
            ),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                coroutineScope.launch {
                    locateAndRepositionCamera(viewModel, cameraPositionState)
                    // TODO - check is gps enabled and switch flag if it is on
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

suspend fun locateAndRepositionCamera(
    viewModel: TourScreenViewModel,
    cameraPositionState: CameraPositionState,
) {
    viewModel.startLocationUpdates()
    viewModel.onLocationChanged(cameraPositionState)
}

private fun distanceInMeter(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Float {
    var results = FloatArray(1)
    Location.distanceBetween(startLat,startLon,endLat,endLon,results)
    return results[0]
}


