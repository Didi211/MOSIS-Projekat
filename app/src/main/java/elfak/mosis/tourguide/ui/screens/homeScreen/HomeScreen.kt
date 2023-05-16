package elfak.mosis.tourguide.ui.screens.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.scaffold.MenuViewModel
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideFloatingButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    navigateToWelcome: () -> Unit,
    navigateToTour: () -> Unit,
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val menuViewModel = hiltViewModel<MenuViewModel>()
    Scaffold(
        scaffoldState = scaffoldState,
        // top navigation bar with menu button
        topBar = {
            TourGuideTopAppBar(
                title = stringResource(id = R.string.home),
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState,
            )
        },
        // menu content
        drawerContent = {
            TourGuideNavigationDrawer(
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState,
                navController = navController,
                menuViewModel = menuViewModel
            )
        },
        floatingActionButton = {
            TourGuideFloatingButton(
                contentDescription = stringResource(id = R.string.add),
                icon = Icons.Rounded.Add,
//                modifier = Modifier.size(36.dp),
                onClick = navigateToTour
            )
        }
    ) {
        MainContent(
            viewModel = viewModel,
            navigateToWelcome =  navigateToWelcome,
            padding = it
        )

    }
}

@Composable
private fun MainContent(
    viewModel: HomeScreenViewModel,
    navigateToWelcome: () -> Unit,
    padding: PaddingValues
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//        Button(
//            onClick = {
//                viewModel.logout()
//                navigateToWelcome()
//
//            }
//        ) {
//            Text(text = "Logout")
//        }
    }


}


