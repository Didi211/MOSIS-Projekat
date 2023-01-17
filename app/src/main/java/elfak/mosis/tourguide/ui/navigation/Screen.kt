package elfak.mosis.tourguide.ui.navigation

sealed class Screen(val route: String)  {
    //auth graph
    object SplashScreen: Screen("splash_screen")
    object WelcomeScreen: Screen("welcome_screen")
    object LoginScreen: Screen("login_screen")
    object ResetPasswordScreen: Screen("reset_password_screen")
    object RegisterScreen: Screen("register_screen")

    //main graph
    object Main: Screen("main")
    object HomeScreen: Screen("home_screen")
    object TourScreen: Screen("tour_screen")
    object NotificationScreen: Screen("notification_screen")
    object ProfileScreen: Screen("profile_screen")
    object FriendsScreen: Screen("friends_screen")


    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
