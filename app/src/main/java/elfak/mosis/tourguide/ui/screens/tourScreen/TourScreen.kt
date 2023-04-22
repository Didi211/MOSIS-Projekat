package elfak.mosis.tourguide.ui.screens.tourScreen

import android.content.Context
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.maps.LocationState
import elfak.mosis.tourguide.ui.components.maps.MyLocationButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideFloatingButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
                        locateAndRepositionCamera(viewModel, permissionsState, context, cameraPositionState)
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



        // Location icon setting
        if (permissionsState.allPermissionsGranted) {
            if (viewModel.uiState.gpsEnabled) {
                viewModel.changeLocationState(LocationState.Located)
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
                if (viewModel.uiState.gpsEnabled) {
                    coroutineScope.launch {
                        locateAndRepositionCamera(viewModel, permissionsState, context, cameraPositionState, false)
                    }
                }
            }

        ) {
            if (viewModel.uiState.gpsEnabled) {
                Marker(
                    state = MarkerState(position = viewModel.uiState.currentLocation),
                    title = "I am here",
    //                draggable = true
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
suspend fun locateAndRepositionCamera(
    viewModel: TourScreenViewModel,
    permissionsState: MultiplePermissionsState,
    context: Context,
    cameraPositionState: CameraPositionState,
    showDisabledGpsMessage: Boolean = true
) {
    val currentLocation =  viewModel.locateUser(permissionsState, viewModel, context, showDisabledGpsMessage)
    if (currentLocation != null) {

        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(currentLocation, 18f)
            ),
            1500
        )
    }
}
