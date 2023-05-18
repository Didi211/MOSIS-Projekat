package elfak.mosis.tourguide.domain.models

import com.google.android.gms.maps.model.LatLng

data class TourDetails(
    var title: String = "",
    var onTitleChanged: (String) -> Unit = { },

    var summary: String = "",
    var onSummaryChanged: (String) -> Unit = { },

    var startLocation: Place = Place("","Start location",LatLng(0.0,0.0)),
    var onStartLocationChanged: (Place) -> Unit = { },

    var endLocation: Place = Place("","End location",LatLng(0.0,0.0)),
    var onEndLocationChanged: (Place) -> Unit = { },

    var distance: String = "",
    var onDistanceChanged: (String) -> Unit = { },

    var time: String = "",
    var onTimeChanged: (String) -> Unit = { },

    var bothLocationsSet:Boolean = false,
    var onBothLocationsSet: (Boolean) -> Unit = { },

    var polylinePoints: List<LatLng> = emptyList()
) {

    fun clear(): TourDetails {
        return this.copy(
            title = "",
            summary = "",
            startLocation = startLocation.clear(),
            endLocation = endLocation.clear(),
            distance = "",
            time = "",
            bothLocationsSet = false,
            polylinePoints = emptyList()
        )
    }

}




