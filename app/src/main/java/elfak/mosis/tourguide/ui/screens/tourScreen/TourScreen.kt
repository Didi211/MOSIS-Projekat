package elfak.mosis.tourguide.ui.screens.tourScreen

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
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
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.maps.LocationState
import elfak.mosis.tourguide.ui.components.maps.MyLocationButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideFloatingButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import es.dmoral.toasty.Toasty

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TourScreen(
    viewModel: TourScreenViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val permissionsState = rememberMultiplePermissionsState(
        permissions = createPermissions()
    )
    val context = LocalContext.current

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
                    checkPermissions(permissionsState, viewModel, context)
                }
                Spacer(Modifier.height(15.dp))

                // search button
                TourGuideFloatingButton(
                    contentDescription = stringResource(id = R.string.add),
                    icon = Icons.Rounded.Search,
                    onClick = { /*TODO - Search location*/ }
                )
            }

        }
    ) {
        MainContent(
            viewModel = viewModel,
            padding = it,
            permissionsState = permissionsState,
        )

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContent(
    viewModel: TourScreenViewModel,
    padding: PaddingValues,
    permissionsState: MultiplePermissionsState,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {

        // Location icon setting
        if (permissionsState.allPermissionsGranted) {
            if (viewModel.uiState.gpsEnabled) {
                viewModel.changeLocationState(LocationState.Located)
                // TODO -  find exact coordinates
            }
            else {
                viewModel.changeLocationState(LocationState.LocationOn)
            }
        }
        else {
            viewModel.changeLocationState(LocationState.LocationOff)
        }



        val currentLocation = LatLng(1.35, 103.87)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentLocation, 16f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {

        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun checkPermissions(permissionsState: MultiplePermissionsState, viewModel: TourScreenViewModel, context: Context) {

    if(permissionsState.allPermissionsGranted) {
        /* TODO - turn on/off gps */
        viewModel.toggleGps(!viewModel.uiState.gpsEnabled)
        val text = if (viewModel.uiState.gpsEnabled) "on" else "off"
        Toasty.info(
            context,
            "gps - $text",
            Toast.LENGTH_SHORT,
            true
        ).show()
    }
    else {
        if (permissionsState.shouldShowRationale) {
            Toasty.info(
                context,
                R.string.permission_not_enabled,
                Toast.LENGTH_LONG,
                true
            ).show()
        }
        else {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
}

private fun createPermissions() : List<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }
}

