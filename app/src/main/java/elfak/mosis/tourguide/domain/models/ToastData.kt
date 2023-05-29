package elfak.mosis.tourguide.domain.models

data class ToastData(
    val hasErrors: Boolean = false,
    val errorMessage: String = "",
    val hasSuccessMessage: Boolean = false,
    val successMessage: String = ""
)