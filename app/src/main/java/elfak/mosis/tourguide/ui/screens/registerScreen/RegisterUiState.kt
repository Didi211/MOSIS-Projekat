package elfak.mosis.tourguide.ui.screens.registerScreen

data class RegisterUiState(
    var fullname: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var confirm_password: String = ""
)