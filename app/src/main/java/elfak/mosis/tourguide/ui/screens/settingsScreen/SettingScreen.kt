@file:OptIn(ExperimentalPermissionsApi::class)

package elfak.mosis.tourguide.ui.screens.settingsScreen

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.ToastHandler
import elfak.mosis.tourguide.ui.components.scaffold.MenuViewModel
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    navController: NavController,
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
        onPermissionsResult = { result ->
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
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 5.dp)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val locationServiceDescription =
                    if (viewModel.uiState.isServiceEnabled) R.string.location_tracking_service_disable
                    else R.string.location_tracking_service_enable
                Column(Modifier.fillMaxWidth(0.8f)) {
                    Text(
                        text = stringResource(id = R.string.location_tracking_service),
                        style = MaterialTheme.typography.h3,
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = stringResource(id = locationServiceDescription),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.primary
                    )
                }
                Switch(
                    checked = viewModel.uiState.isServiceEnabled,
                    onCheckedChange = { enabled ->
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
    }
}