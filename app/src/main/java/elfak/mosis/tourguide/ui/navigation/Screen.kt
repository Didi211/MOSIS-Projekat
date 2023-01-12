package elfak.mosis.tourguide.ui.navigation

sealed class Screen(val route: String)  {
    object WelcomeScreen: Screen("welcome_screen")
    object LoginScreen: Screen("login_screen")
    object HomeScreen: Screen("home_screen")
    object ResetPasswordScreen: Screen("reset_password_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
