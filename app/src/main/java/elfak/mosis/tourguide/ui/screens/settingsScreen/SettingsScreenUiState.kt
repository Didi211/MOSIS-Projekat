package elfak.mosis.tourguide.ui.screens.settingsScreen

import elfak.mosis.tourguide.domain.models.ToastData

data class SettingsScreenUiState(
    val isServiceEnabled: Boolean = false,
    val permissionsAllowed: Boolean = false,
    val gpsEnabled: Boolean = false,
    val toastData: ToastData = ToastData()
) {
}