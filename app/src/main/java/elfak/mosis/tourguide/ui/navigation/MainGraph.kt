package elfak.mosis.tourguide.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import elfak.mosis.tourguide.ui.screens.friendsScreen.FriendsScreen
import elfak.mosis.tourguide.ui.screens.friendsScreen.FriendsScreenViewModel
import elfak.mosis.tourguide.ui.screens.tourScreen.TourScreen
import elfak.mosis.tourguide.ui.screens.homeScreen.HomeScreen
import elfak.mosis.tourguide.ui.screens.homeScreen.HomeScreenViewModel
import elfak.mosis.tourguide.ui.screens.notificationScreen.NotificationScreen
import elfak.mosis.tourguide.ui.screens.notificationScreen.NotificationScreenViewModel
import elfak.mosis.tourguide.ui.screens.profileScreen.ProfileScreen
import elfak.mosis.tourguide.ui.screens.profileScreen.ProfileScreenViewModel
import elfak.mosis.tourguide.ui.screens.settingsScreen.SettingsScreen
import elfak.mosis.tourguide.ui.screens.settingsScreen.SettingsScreenViewModel
import elfak.mosis.tourguide.ui.screens.tourScreen.TourScreenViewModel

fun NavGraphBuilder.mainGraph(navController: NavController) {

    navigation(startDestination = Screen.HomeScreen.route, route = Screen.Main.route) {
        composable(Screen.HomeScreen.route) {
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(
                viewModel = viewModel,
                navigateToTour = { tourId: String?, editMode: Boolean ->
                    if (tourId != null) {
                        navController.navigate(Screen.TourScreen.route + "?tourId=$tourId&editMode=$editMode")
                    }
                    else {
                        navController.navigate(Screen.TourScreen.route)
                    }
                },
                navController = navController
            )
        }
        composable(Screen.TourScreen.route + "?tourId={tourId}&editMode={editMode}",
            deepLinks = listOf(navDeepLink { uriPattern = "${NavigationConstants.TourUri}/{tourId}" }),
            arguments = listOf(
                navArgument("tourId") {
                    nullable = true
                    type = NavType.StringType
                    defaultValue = null
                },navArgument("editMode") {
                    type = NavType.BoolType
                    defaultValue = false
                },
            )
        ) {
            val viewModel = hiltViewModel<TourScreenViewModel>()
            TourScreen(
                viewModel = viewModel,
                navController = navController,
                navigateToFriendProfile = { userId ->
                    navController.navigate(Screen.ProfileScreen.route + "?userId=$userId")
                },
                onAuthenticationFailed = { onAuthenticationFailed(navController) }
            )
        }
        composable(Screen.NotificationScreen.route) {
            val viewModel = hiltViewModel<NotificationScreenViewModel>()
            NotificationScreen(
                navController = navController,
                viewModel = viewModel,
                navigate = { path ->
                    navController.navigate(path)
                }
            )
        }
        composable(Screen.ProfileScreen.route + "?userId={userId}",
            arguments = listOf(
                navArgument("userId") {
                    nullable = true
                    type = NavType.StringType
                    defaultValue = null
                }
            )
        ){
            val viewModel = hiltViewModel<ProfileScreenViewModel>()
            ProfileScreen(
                viewModel,
                navController
            )
        }
        composable(Screen.FriendsScreen.route) {
            val viewModel = hiltViewModel<FriendsScreenViewModel>()
            FriendsScreen(
                navController = navController,
                viewModel = viewModel,
                onCardClick = { friendId ->
                    navController.navigate(Screen.ProfileScreen.route + "?userId=$friendId")
                }
            )
        }
        composable(Screen.SettingScreen.route,
            deepLinks = listOf(navDeepLink { uriPattern = NavigationConstants.SettingsUri })
        ){
            val viewModel = hiltViewModel<SettingsScreenViewModel>()
            SettingsScreen(
                navController = navController,
                viewModel = viewModel,
                onAuthenticationFailed = { onAuthenticationFailed(navController) }
            )
        }
    }
}

private fun onAuthenticationFailed(navController: NavController) {
    navController.navigate(Screen.WelcomeScreen.route) {
        popUpTo(Screen.Main.route) { inclusive = true }
    }
}