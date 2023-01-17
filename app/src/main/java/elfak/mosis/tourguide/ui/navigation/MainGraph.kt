package elfak.mosis.tourguide.ui.navigation

import android.provider.ContactsContract.Profile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import elfak.mosis.tourguide.ui.screens.friendsScreen.FriendsScreen
import elfak.mosis.tourguide.ui.screens.tourScreen.TourScreen
import elfak.mosis.tourguide.ui.screens.homeScreen.HomeScreen
import elfak.mosis.tourguide.ui.screens.homeScreen.HomeScreenViewModel
import elfak.mosis.tourguide.ui.screens.notificationScreen.NotificationScreen
import elfak.mosis.tourguide.ui.screens.profileScreen.ProfileScreen

fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(startDestination = Screen.HomeScreen.route, route = Screen.Main.route) {
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
                },
                navController = navController
            )
        }
        composable(Screen.TourScreen.route) {
            TourScreen(
                navigateToWelcome = {
                    navController.navigate(Screen.WelcomeScreen.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.NotificationScreen.route){
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