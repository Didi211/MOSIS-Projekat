package elfak.mosis.tourguide.ui.screens.tourScreen

import android.Manifest
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import com.google.maps.android.compose.GoogleMap
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.MultiplePermissionsDialog
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideFloatingButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar

@Composable
fun TourScreen(
    viewModel: TourScreenViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()



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
            TourGuideFloatingButton(
                contentDescription = stringResource(id = R.string.add),
                icon = Icons.Rounded.Search,
                modifier = Modifier.border(color = MaterialTheme.colors.primary, width = 3.dp, shape = RoundedCornerShape(15)),
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.primary,
                shape = RoundedCornerShape(15),
                onClick = { /*TODO - Search location*/ }
            )
        }
    ) {
        MainContent(
            viewModel = viewModel,
            padding = it
        )

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContent(
    viewModel: TourScreenViewModel,
    padding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        val permissionsState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        MultiplePermissionsDialog(
            permissionsState = permissionsState,
            permissionTextOnDenied = stringResource(id = R.string.permission_not_enabled ),
            buttonText = stringResource(id = R.string.allow_permissions)
        )
        if (permissionsState.allPermissionsGranted) {
            GoogleMap() {

            }
        }
    }
}