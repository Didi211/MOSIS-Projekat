@file:OptIn(ExperimentalPermissionsApi::class)

package elfak.mosis.tourguide.ui.screens.settingsScreen

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.ToastHandler
import elfak.mosis.tourguide.ui.components.scaffold.MenuViewModel
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import es.dmoral.toasty.Toasty

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
    navController: NavController,
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val menuViewModel = hiltViewModel<MenuViewModel>()

    val context = LocalContext.current
    var permissionAlreadyRequested by rememberSaveable {
        mutableStateOf(false)
    }
    val permissionsState = rememberMultiplePermissionsState(
        permissions = viewModel.getLocationPermissions(),
        onPermissionsResult = { result ->
            Log.i("PERMISSIONS", result.toString())
            if (!viewModel.checkPermissions()) {
                viewModel.setErrorMessage("Permissions denied. Enable them in settings.")
                return@rememberMultiplePermissionsState
            }
            if (!viewModel.checkGps()) {
                viewModel.setErrorMessage("Permissions denied. Enable them in settings.")
                return@rememberMultiplePermissionsState
            }
            viewModel.toggleService(true, context)
//            checkPermissionsAndGps(
//                viewModel.checkPermissions(),
//                permissionAlreadyRequested,
//                permissionsState,
//                viewModel.checkGps(),
//                context
//            )
            permissionAlreadyRequested = true
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

private fun checkPermissionsAndGps(
    permissionsAllowed: Boolean,
    permissionAlreadyRequested: Boolean,
    permissionsState: MultiplePermissionsState,
    gpsEnabled: Boolean,
    context: Context
):Boolean {
    // check permissions
    if (!permissionsAllowed) {
        // ask for permissions
        if (!permissionAlreadyRequested || permissionsState.shouldShowRationale) {
            permissionsState.launchMultiplePermissionRequest()
            return false
        }
        val message = context.getString(R.string.permissions_denied_twice)
        Toasty.error(context, message, Toast.LENGTH_LONG).show()
        return false
    }
    // check gps
    if (!gpsEnabled) {
        // ask for gps
        val message = context.getString(R.string.location_needed)
        Toasty.error(context, message).show()
        return false
    }
    return true
}