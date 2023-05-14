package elfak.mosis.tourguide.ui.screens.tourScreen

import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.ui.components.maps.LocationState

data class TourScreenUiState(
    var locationState: LocationState = LocationState.LocationOff,
    var gpsEnabled: Boolean = false,

    val defaultLocation: LatLng = LatLng(43.32472, 21.90333),
    var currentLocation: LatLng = defaultLocation,
    var myLocation: LatLng = defaultLocation,
    var searchedLocation: LatLng = defaultLocation,

    var isSearching: Boolean = false,
    var searchValue: String = "",
    var showSearchBar: Boolean = false,

    var locationPermissionAllowed: Boolean = false,
//    var requestingLocationUpdates: Boolean = false, // LocationState.Located is the same as isTrackingLocation,
    val minimalDistanceInMeters: Int = 30, //between two sequential locations, for map move animation

//    var showKeyboard: Boolean = false,

    var tourTitle: String = "Title",
    var startLocation: String = "Centar, Nis",
    var endLocation: String = "Tvrdjava, Nis"
)