package elfak.mosis.tourguide.ui.screens.tourScreen

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import elfak.mosis.tourguide.data.models.PlaceDetails
import elfak.mosis.tourguide.data.models.tour.category.NearbyPlaceResult
import elfak.mosis.tourguide.domain.models.DeviceSettings
import elfak.mosis.tourguide.domain.models.ToastData
import elfak.mosis.tourguide.domain.models.tour.TourDetails
import elfak.mosis.tourguide.ui.components.maps.FriendMarker
import elfak.mosis.tourguide.ui.components.maps.LocationState

data class TourScreenUiState(
    var locationState: LocationState = LocationState.LocationOff,

    val defaultLocation: LatLng = LatLng(43.32472, 21.90333),
    var currentLocation: LatLng = defaultLocation,
    var myLocation: LatLng = defaultLocation,
    var searchedLocation: LatLng = defaultLocation,

    var isSearching: Boolean = false,
    var searchValue: String = "",
    var showSearchBar: Boolean = false,

    var cameraPositionState: CameraPositionState = CameraPositionState(
        position = CameraPosition(currentLocation, 10f, 0f, 0f)
    ),

    var routeChanged: Boolean = false,
    val tourId: String? = null,
    val tourDetails: TourDetails = TourDetails(),
    val tourScreenState: TourScreenState = TourScreenState.TOUR_DETAILS,
    val tourState: TourState = TourState.VIEWING,

    val placeDetails: PlaceDetails = PlaceDetails(),

    val categorySearchResult: List<NearbyPlaceResult> = emptyList(),

    val showFriends: Boolean = false,
    val friends: List<FriendMarker> = emptyList(),

    val toastData: ToastData = ToastData(),
    val deviceSettings: DeviceSettings = DeviceSettings(),

    val userId: String = "",
)