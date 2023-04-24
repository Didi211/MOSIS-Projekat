package elfak.mosis.tourguide.ui.screens.tourScreen

import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.ui.components.maps.LocationState

data class TourScreenUiState(
    var locationState: LocationState = LocationState.LocationOff,
    var gpsEnabled: Boolean = false,
    val defaultLocation: LatLng = LatLng(43.32472, 21.90333),
    var currentLocation: LatLng = defaultLocation,
    var newLocation: LatLng = defaultLocation,
    var newLocationArrived: Boolean = false,
    var isTrackingLocation: Boolean = false
)