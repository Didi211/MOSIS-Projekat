package elfak.mosis.tourguide.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import elfak.mosis.tourguide.ui.screens.friendsScreen.FriendsScreen
import elfak.mosis.tourguide.ui.screens.tourScreen.TourScreen
import elfak.mosis.tourguide.ui.screens.homeScreen.HomeScreen
import elfak.mosis.tourguide.ui.screens.homeScreen.HomeScreenViewModel
import elfak.mosis.tourguide.ui.screens.notificationScreen.NotificationScreen
import elfak.mosis.tourguide.ui.screens.profileScreen.ProfileScreen
import elfak.mosis.tourguide.ui.screens.tourScreen.TourScreenViewModel

fun NavGraphBuilder.mainGraph(navController: NavController) {


    navigation(startDestination = Screen.HomeScreen.route, route = Screen.Main.route) {
        composable(Screen.HomeScreen.route) {
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(
                viewModel = viewModel,
                navigateToTour = { tourId: String? ->
                    if (tourId != null) {
                        navController.navigate(Screen.TourScreen.route + "?tourId=$tourId")
                    }
                    else {
                        navController.navigate(Screen.TourScreen.route)
                    }
                },
                navController = navController
            )
        }
        composable(Screen.TourScreen.route + "?tourId={tourId}",
            arguments = listOf(
                navArgument("tourId") {
                    nullable = true
                    type = NavType.StringType
                    defaultValue = null
                })
        ) {
            val viewModel = hiltViewModel<TourScreenViewModel>()
            TourScreen(
                viewModel = viewModel,
                navController = navController,
                tourId = it.arguments?.getString("tourId")
            )
        }
        composable(Screen.NotificationScreen.route) {
            NotificationScreen(

            )
        }
        composable(Screen.ProfileScreen.route){
            ProfileScreen(
                navController
            )
        }
        composable(Screen.FriendsScreen.route){
            FriendsScreen(

            )
        }
    }
}