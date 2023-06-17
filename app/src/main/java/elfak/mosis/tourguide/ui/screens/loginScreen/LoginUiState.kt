package elfak.mosis.tourguide.ui.screens.loginScreen

import elfak.mosis.tourguide.domain.models.validation.LoginCredentials

data class LoginUiState(
    var email: String = "",
    var password: String = "",
    var hasErrors: Boolean = false,
    var errorMessage: String = ""
) {
    fun toValidationModel(): LoginCredentials {
        return LoginCredentials(
            email = email,
            password = password
        )
    }
}

