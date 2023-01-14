package elfak.mosis.tourguide.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import elfak.mosis.tourguide.ui.screens.registerScreen.RegisterScreen
import elfak.mosis.tourguide.ui.screens.loginScreen.LoginScreen
import elfak.mosis.tourguide.ui.screens.loginScreen.LoginViewModel
import elfak.mosis.tourguide.ui.screens.registerScreen.RegisterViewModel
import elfak.mosis.tourguide.ui.screens.resetPasswordScreen.ResetPasswordScreen
import elfak.mosis.tourguide.ui.screens.splashScreen.SplashScreen
import elfak.mosis.tourguide.ui.screens.splashScreen.SplashScreenViewModel
import elfak.mosis.tourguide.ui.screens.welcomeScreen.WelcomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()


    //define routes here
    // TODO - navigation graphs
    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
        composable(Screen.SplashScreen.route) {
            val viewModel = hiltViewModel<SplashScreenViewModel>()
            SplashScreen(
                viewModel = viewModel,
                navigateToWelcome = {
                    navController.navigate(Screen.WelcomeScreen.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.WelcomeScreen.route) {
            WelcomeScreen(
                navigateToLogin = { navController.navigate(Screen.LoginScreen.route) },
                navigateToRegister = { navController.navigate(Screen.RegisterScreen.route) }
            )
        }
        composable(Screen.LoginScreen.route) {
            val loginViewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(
                viewModel = loginViewModel,
                navigateBack = { navController.popBackStack() },
                navigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        // removes all from backstack so when user clicks back button it will close the app
                        popUpTo(Screen.WelcomeScreen.route) { inclusive = true }
                    }
                },
                navigateToResetPassword = { navController.navigate(Screen.ResetPasswordScreen.route) }
            )
        }
        composable(Screen.RegisterScreen.route){
            val registerViewModel = hiltViewModel<RegisterViewModel>()
            RegisterScreen(
                viewModel = registerViewModel,
                navigateBack = { navController.popBackStack() },
                navigateToHome = {
                    navController.navigate(Screen.Main.route){
                        popUpTo(Screen.WelcomeScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ResetPasswordScreen.route) {
            ResetPasswordScreen()
        }
        mainGraph(navController)

    }
}