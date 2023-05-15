@file:OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class,
    ExperimentalAnimationApi::class
)

package elfak.mosis.tourguide.ui.screens.tourScreen

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.bottomsheet.TourDetails
import elfak.mosis.tourguide.ui.components.maps.LocationState
import elfak.mosis.tourguide.ui.components.maps.MyLocationButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideFloatingButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TourScreen(
    viewModel: TourScreenViewModel,
) {
    val uiState = viewModel.uiState
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
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(uiState.currentLocation, 10f, 0f, 0f)
    }
    val focusRequester = remember { FocusRequester() }


    if(bottomSheetScaffoldState.bottomSheetState.isExpanded) {
        viewModel.setSearchBarVisibility(false)
    }


    BottomSheetScaffold(
        sheetContent = { TourDetails(
            state = uiState.tourState,
            tourDetails = uiState.tourDetails,
            onSave = { viewModel.setTourState(TourState.VIEWING) },
            onEdit = { viewModel.setTourState(TourState.EDITING) },
            onCancel = { viewModel.setTourState(TourState.VIEWING) }
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
                    ListOfPlaces(viewModel = viewModel, cameraPositionState = cameraPositionState)

                    // my location button
                    MyLocationButton(uiState.locationState) {
                        locateMe(
                            viewModel,
                            permissionAlreadyRequested,
                            permissionsState,
                            context,
                            cameraPositionState
                        )
                    }
                }
                Spacer(Modifier.height(15.dp))

                // search button
                AnimatedContent(
                    targetState = uiState.showSearchBar,
                    //                    transitionSpec = {
                    //                        fadeIn(
                    //                            animationSpec = tween(400)
                    //                        ) with slideOutOfContainer(
                    //                            towards = AnimatedContentScope.SlideDirection.Left,
                    //                            animationSpec = tween(400)
                    //                        )
                    //                    }
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
                                //                                viewModel.setKeyboardVisibility(true)
                            },
                        )

                        true -> SearchField(
                            onSearch = {
                                Toasty.info(context, "Searching..").show()
                                viewModel.searchOnMap(cameraPositionState)

                            },
                            viewModel = viewModel,
                            focusRequester = focusRequester
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
            if ((cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE)
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
                onMapClick = {
                    viewModel.clearSearchBar()
                }

            ) {
                if (uiState.deviceSettings.gpsEnabled) {
                    Marker(
                        icon = viewModel.bitmapHelper.bitmapDescriptorFromVector(context,
                            R.drawable.my_location),
                        state = MarkerState(position = uiState.myLocation),
                    )
                }
                Marker(
                    state = MarkerState(position = uiState.searchedLocation),
                    visible = uiState.isSearching,
                )
            }
        }
    }
}


fun showDeniedPermissionMessage(context: Context, @StringRes message: Int) {
    Toasty.error(context, message, Toast.LENGTH_LONG).show()
}

@Composable
fun SearchField(onSearch: () -> Unit, viewModel: TourScreenViewModel,focusRequester: FocusRequester) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context: Context = LocalContext.current

    Column {
        BasicInputComponent(
            text = viewModel.uiState.searchValue,
            onTextChanged = {
                viewModel.changeSearchValue(it)
                viewModel.textInputJob?.cancel()
                viewModel.textInputJob = coroutineScope.launch {
                    delay(300)
                    viewModel.findPlacesFromInput(it)
                }
            },
            label = stringResource(id = R.string.search_here) + ":",
            inputType = InputTypes.Text,
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = false,
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (viewModel.locationAutofill.isEmpty()) {
                        Toasty.info(context, R.string.place_not_selected).show()
                        return@KeyboardActions
                    }
                    onSearch()
                    viewModel.clearSearchBar()
                    focusManager.clearFocus()
//                    viewModel.setKeyboardVisibility(false)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
//                .focusRequester(focusRequester)
//                .onGloballyPositioned {
//                    if (viewModel.uiState.showKeyboard) {
//                        focusRequester.requestFocus()
//                    }
//                }

        )
    }
}

@Composable
private fun ListOfPlaces(viewModel: TourScreenViewModel, cameraPositionState: CameraPositionState) {
    val focusManager = LocalFocusManager.current

    AnimatedVisibility(
        visible = viewModel.locationAutofill.isNotEmpty(),
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(999f)
    ) {
        Surface(shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(250.dp)
           ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(12.dp)) {
                items(
                    viewModel.locationAutofill
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clickable {
                                viewModel.chooseLocation(it)
                                viewModel.searchOnMap(cameraPositionState)
                                viewModel.clearSearchBar()
                                focusManager.clearFocus()
                            }
                    ) {
                        Column {
                            Text(it.address)
                            Divider(
                                color = Color.DarkGray,
                                modifier = Modifier
                                    .height(1.dp)
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                            )

                        }
                    }
                }
            }
        }
    }
}



private fun locateMe(
    viewModel: TourScreenViewModel,
    permissionAlreadyRequested: Boolean,
    permissionsState: MultiplePermissionsState,
    context: Context,
    cameraPositionState: CameraPositionState
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
    viewModel.onLocationChanged(cameraPositionState, true)
}