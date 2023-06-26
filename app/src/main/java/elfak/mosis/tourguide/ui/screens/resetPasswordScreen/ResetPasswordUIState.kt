package elfak.mosis.tourguide.ui.screens.resetPasswordScreen;

import elfak.mosis.tourguide.domain.models.ToastData;
data class ResetPasswordUIState(
    val userId: String = "",
    val email: String = "",
    var hasErrors: Boolean = false,
    var errorMessage: String = "",
    val toastData: ToastData = ToastData()
)
