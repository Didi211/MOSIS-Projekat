@file:OptIn(ExperimentalPermissionsApi::class)

package elfak.mosis.tourguide.ui.screens.tourScreen

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TourScreen(
    viewModel: TourScreenViewModel,
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
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
                        // check permissions
                        if(!viewModel.checkPermissions()) {
                            viewModel.changeLocationState(LocationState.LocationOff)
                            // ask for permissions
                            if(!permissionAlreadyRequested || permissionsState.shouldShowRationale) {
                                permissionsState.launchMultiplePermissionRequest()
                                return@launch
                            }
                            showDeniedPermissionMessage(context, R.string.permission_denied_twice)
                            return@launch
                        }
                        // check gps
                        if(!viewModel.checkGps()) {
                            // ask for gps
                            Toasty.error(context, R.string.location_needed).show()
                            viewModel.changeLocationState(LocationState.LocationOff)
                            return@launch
                        }
                        // turn on gps tracking
                        viewModel.startLocationUpdates()
                        // change mode to LOCATED
                        viewModel.changeLocationState(LocationState.Located)
                        // move camera
                        viewModel.onLocationChanged(cameraPositionState)
                    }
                }
                Spacer(Modifier.height(15.dp))

                // search button
                TourGuideFloatingButton(
                    contentDescription = stringResource(id = R.string.add),
                    icon = Icons.Rounded.Search,
                    onClick = {
                        /* TODO - Search locations */
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

        if((cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) && viewModel.uiState.requestingLocationUpdates) {
            viewModel.changeLocationState(LocationState.LocationOn)
            viewModel.setRequestingLocationUpdates(false)
        }

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
                viewModel.setLocationCallbacks(cameraPositionState)
                if (!viewModel.checkPermissions()) {
                    return@GoogleMap
                }
                if (!viewModel.checkGps()) {
                    return@GoogleMap
                }
                viewModel.startLocationUpdates()
                viewModel.changeLocationState(LocationState.Located)
            },

        ) {
            if (viewModel.uiState.gpsEnabled) {
                Marker(
                    state = MarkerState(position = viewModel.uiState.currentLocation),
                    title = "My address - " +
                            "${viewModel.uiState.currentLocation.latitude} - " +
                            "${viewModel.uiState.currentLocation.longitude}",
                )
            }
        }
    }
}

fun showDeniedPermissionMessage(context: Context, @StringRes message: Int) {
    Toasty.error(context,message, Toast.LENGTH_LONG).show()
}