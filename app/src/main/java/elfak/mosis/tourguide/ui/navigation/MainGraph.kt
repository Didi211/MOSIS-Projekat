package elfak.mosis.tourguide.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import elfak.mosis.tourguide.ui.screens.homeScreen.HomeScreen
import elfak.mosis.tourguide.ui.screens.homeScreen.HomeScreenViewModel
import elfak.mosis.tourguide.ui.screens.tourScreen.TourScreen
import elfak.mosis.tourguide.ui.screens.tourScreen.TourScreenViewModel

fun NavGraphBuilder.mainGraph(navController: NavController) {


    navigation(startDestination = Screen.TourScreen.route, route = Screen.Main.route) {
        composable(Screen.HomeScreen.route) {
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(
                viewModel = viewModel,
                navigateToWelcome = {
                    navController.navigate(Screen.WelcomeScreen.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                navigateToTour = {
                    navController.navigate(Screen.TourScreen.route)
                }
            )
        }
        composable(Screen.TourScreen.route) {
            val viewModel = hiltViewModel<TourScreenViewModel>()
            TourScreen(
                viewModel = viewModel,
            )
        }
    }
}