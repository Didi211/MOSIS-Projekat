package elfak.mosis.tourguide.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import elfak.mosis.tourguide.ui.screens.LoginScreen
import elfak.mosis.tourguide.ui.screens.WelcomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    //define routes here
    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {
        composable(Screen.WelcomeScreen.route) {
            WelcomeScreen(
                navigateToLogin = { navController.navigate(Screen.LoginScreen.route) }
            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}