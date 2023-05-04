@file:OptIn(ExperimentalPermissionsApi::class)

package elfak.mosis.tourguide.ui.screens.tourScreen

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.ButtonComponent
import elfak.mosis.tourguide.ui.components.maps.LocationState
import elfak.mosis.tourguide.ui.components.maps.MyLocationButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideFloatingButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import elfak.mosis.tourguide.ui.theme.BlueBorder
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TourScreen(
    viewModel: TourScreenViewModel,
) {
    var showSearchDialog by remember {
        mutableStateOf(false)
    }
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
                        showSearchDialog = true
                    }
                )
            }

        }
    ) {

        /** MAIN CONTENT */


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            if((cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) && viewModel.uiState.requestingLocationUpdates) {
                viewModel.changeLocationState(LocationState.LocationOn)
                viewModel.setRequestingLocationUpdates(false)
            }

            AnimatedVisibility(visible = showSearchDialog) {
                SearchDialog(
                    onDismiss = {
                        coroutineScope.launch {
                            // simulating clearing the form behind the scenes
                            delay(500)
                            viewModel.changePlaceName("")
                        }
                        showSearchDialog = false
                    },
                    onSearch = {
                        Toasty.info(context, "Searching..").show()
                        viewModel.searchPlace()
                    },
                    viewModel = viewModel
                )
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
                        icon = viewModel.bitmapHelper.bitmapDescriptorFromVector(context, R.drawable.my_location),
                        state = MarkerState(position = viewModel.uiState.currentLocation),
                        title = "My address - " +
                                "${viewModel.uiState.currentLocation.latitude} - " +
                                "${viewModel.uiState.currentLocation.longitude}",
                    )
                }
            }
        }
    }
}

fun showDeniedPermissionMessage(context: Context, @StringRes message: Int) {
    Toasty.error(context,message, Toast.LENGTH_LONG).show()
}

@Composable fun SearchDialog(onDismiss: () -> Unit, onSearch: () -> Unit, viewModel: TourScreenViewModel) {
    Dialog(onDismissRequest = { onDismiss() } )
    {
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            elevation = 10.dp,
            border = BorderStroke(width = 3.dp, color = BlueBorder)

        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary,
                    text = stringResource(id = R.string.add_checkpoint)
                )
                Spacer(modifier = Modifier.height(15.dp))

                BasicInputComponent(
                    text = viewModel.uiState.placeName,
                    onTextChanged =  { viewModel.changePlaceName(it) },
                    label = stringResource(id = R.string.place_name),
                    inputType = InputTypes.Text,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Search,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSearch() }
                    ),
                    inputColors = searchColors()
                )
                Spacer(modifier = Modifier.height(35.dp))

                ButtonComponent(
                    text = stringResource(id = R.string.search),
                    width = 180.dp,
                    onClick = { onSearch() }
                )


            }
        }
    }
}

@Composable
fun searchColors(): TextFieldColors {
    val colors = MaterialTheme.colors
    return TextFieldDefaults.outlinedTextFieldColors(
        backgroundColor = colors.background,
        textColor = colors.onSecondary,
        cursorColor = colors.onSecondary,
        focusedBorderColor = BlueBorder,
        unfocusedBorderColor = Color.Transparent,
        unfocusedLabelColor = colors.onPrimary,
        focusedLabelColor = colors.primary,
    )
}