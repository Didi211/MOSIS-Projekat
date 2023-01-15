package elfak.mosis.tourguide.ui.screens.loginScreen

data class LoginUiState(
    var email: String = "",
    var password: String = "",
    var hasErrors: Boolean = false,
    var errorMessage: String = ""
)

