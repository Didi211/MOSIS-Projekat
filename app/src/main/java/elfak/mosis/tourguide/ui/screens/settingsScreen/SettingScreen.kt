@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)

package elfak.mosis.tourguide.ui.screens.settingsScreen

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.models.menu.MenuData
import elfak.mosis.tourguide.ui.components.ToastHandler
import elfak.mosis.tourguide.ui.components.dialogs.ChooseTourDialog
import elfak.mosis.tourguide.ui.components.scaffold.MenuViewModel
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import elfak.mosis.tourguide.ui.screens.homeScreen.TourCard

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    navController: NavController,
    onAuthenticationFailed: () -> Unit = { }
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val menuViewModel = hiltViewModel<MenuViewModel>()

    val context = LocalContext.current
    val key = "permissionAlreadyRequestedKey"
    val sharedPreferences = context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
    var permissionAlreadyRequested by remember {
        mutableStateOf(sharedPreferences.getBoolean(key, false))
    }
    val permissionsState = rememberMultiplePermissionsState(
        permissions = viewModel.getLocationPermissions(),
        onPermissionsResult = {
            if (!viewModel.checkPermissions()) {
                if (permissionAlreadyRequested) {
                    // show rationale message
                    viewModel.setErrorMessage((context.getString(R.string.permissions_denied_twice)))
                }
                permissionAlreadyRequested = true
                sharedPreferences.edit().putBoolean(key, true).apply()
                return@rememberMultiplePermissionsState
            }
            permissionAlreadyRequested = true
            sharedPreferences.edit().putBoolean(key, true).apply()
            if (!viewModel.checkGps()) {
                viewModel.setErrorMessage(context.getString(R.string.location_needed))
                return@rememberMultiplePermissionsState
            }
            viewModel.toggleService(true, context)
        }
    )

    LaunchedEffect(true) {
        //authenticate user
        if (!viewModel.isAuthenticated()) {
            viewModel.setErrorMessage("User not authenticated. Please login.")
            onAuthenticationFailed()
            return@LaunchedEffect
        }

        val isRunning = viewModel.isServiceRunning(context)
        viewModel.setEnabledService(isRunning)
        viewModel.checkGps()
        viewModel.checkPermissions()
    }

    ToastHandler(
        toastData = viewModel.uiState.toastData,
        clearErrorMessage = viewModel::clearErrorMessage,
        clearSuccessMessage = viewModel::clearSuccessMessage
    )

    Scaffold(
        scaffoldState = scaffoldState,
        // top navigation bar with menu button
        topBar = {
            TourGuideTopAppBar(
                title = stringResource(id = R.string.settings),
                scaffoldState = scaffoldState,
                coroutineScope = coroutineScope
            )
        },
        // menu content
        drawerContent = {
            TourGuideNavigationDrawer(
                navController = navController,
                menuViewModel = menuViewModel
            )

        }
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            // location tracking service
            Column {
//                Row(
//                    Modifier
//                        .fillMaxWidth()
//                        .height(100.dp)
//                        .padding(top = 5.dp)
//                        .padding(horizontal = 10.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    val locationServiceDescription =
//                        if (viewModel.uiState.isServiceEnabled) R.string.location_tracking_service_disable
//                        else R.string.location_tracking_service_enable
//                    Column(Modifier.fillMaxWidth(0.8f)) {
//                        Text(
//                            text = stringResource(id = R.string.location_tracking_service),
//                            style = MaterialTheme.typography.h3,
//                            color = MaterialTheme.colors.primary
//                        )
//                        Spacer(Modifier.height(5.dp))
//                        Text(
//                            text = stringResource(id = locationServiceDescription),
//                            style = MaterialTheme.typography.body1,
//                            color = MaterialTheme.colors.primary
//                        )
//                    }
//                    Switch(
//                        checked = viewModel.uiState.isServiceEnabled,
//                        onCheckedChange = { enabled ->
//                            if (enabled) {
//                                permissionsState.launchMultiplePermissionRequest()
//                            }
//                            else {
//                                viewModel.toggleService(false, context)
//                            }
//                        }
//                    )
//                }
                SettingRowContainer {
                    val locationServiceDescription =
                        if (viewModel.uiState.isServiceEnabled) R.string.location_tracking_service_disable
                        else R.string.location_tracking_service_enable
                    SettingItem(
                        text = stringResource(id = R.string.location_tracking_service),
                        description = stringResource(id = locationServiceDescription),
                        switchState = viewModel.uiState.isServiceEnabled,
                        onSwitchToggle = { enabled ->
                            if (enabled) {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                            else {
                                viewModel.toggleService(false, context)
                            }
                        }
                    )
                }
                Divider()
            }

            // tour for notification
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Near me notifications:",
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.h2)
                AnimatedContent(viewModel.uiState.tour.id.isNotBlank()) { hasTour ->
                    when(hasTour) {
                        true -> {
                            Column(Modifier.padding(10.dp)) {
                                TourCard(tour = viewModel.uiState.tour, menuItems = listOf(
                                    MenuData(
                                        Icons.Rounded.Delete,
                                        "Remove tour",
                                        onClick = { viewModel.removeTourFromNotification() })
                                ))
                            }
                        }
                        false -> {
                            var showChooseTourDialog by remember { mutableStateOf(false) }
                            if (showChooseTourDialog) {
                                ChooseTourDialog(
                                    tours = viewModel.uiState.tours,
                                    onDismiss = { showChooseTourDialog = false },
                                    onOkButtonClick = { tourId ->
                                        viewModel.setNotificationForTour(tourId)
                                    }
                                )
                            }
                            Column(Modifier.padding(10.dp)) {
                                Button(onClick = {
                                    showChooseTourDialog = true
                                }) {
                                    Text(
                                        "Select tour",
                                        color = MaterialTheme.colors.onPrimary,
                                        style = MaterialTheme.typography.button
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun SettingRowContainer(content: @Composable RowScope.() -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(top = 5.dp)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        content()
    }
}

@Composable
fun SettingItem(
    text: String,
    description: String,
    switchState: Boolean,
    onSwitchToggle: (Boolean) -> Unit,
) {
    Column(Modifier.fillMaxWidth(0.8f)) {
        Text(
            text = text,
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.primary
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.primary
        )
    }
    Switch(
        checked = switchState,
        onCheckedChange = onSwitchToggle
    )
}