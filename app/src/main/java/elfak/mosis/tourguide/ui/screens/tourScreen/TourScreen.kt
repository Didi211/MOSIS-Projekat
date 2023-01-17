package elfak.mosis.tourguide.ui.screens.tourScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R
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

    }


}