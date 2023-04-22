package elfak.mosis.tourguide.ui.screens.tourScreen

import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.ui.components.maps.LocationState

data class TourScreenUiState(
    var locationState: LocationState = LocationState.LocationOff,
    var gpsEnabled: Boolean = true,
    var currentLocation: LatLng = LatLng(43.32472, 21.90333)
)