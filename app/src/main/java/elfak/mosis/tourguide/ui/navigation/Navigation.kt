package elfak.mosis.tourguide.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import elfak.mosis.tourguide.ui.components.TravelersImage
import elfak.mosis.tourguide.ui.screens.homeScreen.HomeScreen
import elfak.mosis.tourguide.ui.screens.RegisterScreen
import elfak.mosis.tourguide.ui.screens.loginScreen.LoginScreen
import elfak.mosis.tourguide.ui.screens.loginScreen.LoginViewModel
import elfak.mosis.tourguide.ui.screens.resetPasswordScreen.ResetPasswordScreen
import elfak.mosis.tourguide.ui.screens.registerScreen.RegisterViewModel
import elfak.mosis.tourguide.ui.screens.welcomeScreen.WelcomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    Box {
        TravelersImage(modifier = Modifier.align(Alignment.BottomEnd))
    }
    //define routes here
    // TODO - navigation graphs
    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {
        composable(Screen.WelcomeScreen.route) {
            WelcomeScreen(
                navigateToLogin = { navController.navigate(Screen.LoginScreen.route) },
                navigateToRegister = { navController.navigate(Screen.RegisterScreen.route) }
            )
        }
        composable(Screen.LoginScreen.route) {
            val loginViewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(
                navigateBack = { navController.popBackStack() },
                navigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        // removes all from backstack so when user clicks back button it will close the app
                        popUpTo(Screen.WelcomeScreen.route) { inclusive = true }
                    }
                },
                navigateToResetPassword = { navController.navigate(Screen.ResetPasswordScreen.route) },
                viewModel = loginViewModel
            )
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen()
        }
        composable(Screen.ResetPasswordScreen.route) {
            ResetPasswordScreen()
        }
        composable(Screen.RegisterScreen.route){
            val registerViewModel = hiltViewModel<RegisterViewModel>()
            RegisterScreen(
                navigateBack = {navController.popBackStack() },
                navigateToHome = {navController.navigate(Screen.HomeScreen.route){
                    popUpTo(Screen.WelcomeScreen.route) { inclusive = true }

                    }
                },
                viewModel = registerViewModel
            )
        }
    }

}