package elfak.mosis.tourguide.ui.screens.registerScreen

data class RegisterUiState(
    var fullname: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var confirm_password: String = "",
    var hasErrors: Boolean = false,
    var errorMessage: String = "",
    val phoneNumber: String = "",
    val hasPhoto: Boolean = false
) {
}
