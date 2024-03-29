package elfak.mosis.tourguide.ui.screens.settingsScreen

import elfak.mosis.tourguide.data.models.tour.TourNotify
import elfak.mosis.tourguide.domain.models.ToastData
import elfak.mosis.tourguide.domain.models.tour.TourCard
import elfak.mosis.tourguide.domain.models.tour.TourSelectionDisplay

data class SettingsScreenUiState(
    val isServiceEnabled: Boolean = false,
    val permissionsAllowed: Boolean = false,
    val gpsEnabled: Boolean = false,
    val toastData: ToastData = ToastData(),
    val tour: TourCard = TourCard(),
    val tours: List<TourSelectionDisplay> = emptyList(),
    val userId: String = "",
    val radius: Int = DefaultRadius, //meters
    val tourNotify: TourNotify = TourNotify(),

    val isMocking: Boolean = false,
    val mockStarted: Boolean = false
) {
    companion object {
        const val DefaultRadius:Int = 500

    }
}