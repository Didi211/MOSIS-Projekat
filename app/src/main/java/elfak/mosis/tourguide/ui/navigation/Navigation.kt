package elfak.mosis.tourguide.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import elfak.mosis.tourguide.ui.components.TravelersImage
import elfak.mosis.tourguide.ui.screens.loginScreen.LoginScreen
import elfak.mosis.tourguide.ui.screens.loginScreen.LoginViewModel
import elfak.mosis.tourguide.ui.screens.welcomeScreen.WelcomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    Box {
        TravelersImage(modifier = Modifier.align(Alignment.BottomEnd))
    }
    //define routes here
    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {
        composable(Screen.WelcomeScreen.route) {
            WelcomeScreen(
                navigateToLogin = { navController.navigate(Screen.LoginScreen.route) }
            )
        }
        composable(Screen.LoginScreen.route) {
            val loginViewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(
                navigateBack = { navController.popBackStack() },
                viewModel = loginViewModel
            )
        }
    }

}