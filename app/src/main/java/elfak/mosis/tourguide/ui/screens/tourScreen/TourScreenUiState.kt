package elfak.mosis.tourguide.ui.screens.tourScreen

import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.domain.models.DeviceSettings
import elfak.mosis.tourguide.domain.models.TourDetails
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



//    var showKeyboard: Boolean = false,

    val tourDetails: TourDetails = TourDetails(),
    val tourState: TourState = TourState.VIEWING,

    val deviceSettings: DeviceSettings = DeviceSettings()

)



