package elfak.mosis.tourguide.domain.models

import com.google.android.gms.maps.model.LatLng

data class TourDetails(
    var title: String = "Title",
    var onTitleChanged: (String) -> Unit = { },

    var summary: String = "Summary",
    var onSummaryChanged: (String) -> Unit = { },

    var startLocation: Place = Place("","Start location",LatLng(0.0,0.0)),
    var onStartLocationChanged: (Place) -> Unit = { },

    var endLocation: Place = Place("","End location",LatLng(0.0,0.0)),
    var onEndLocationChanged: (Place) -> Unit = { },

    var distance: String = "15km",
    var onDistanceChanged: (String) -> Unit = { },

    var time: String = "90min",
    var onTimeChanged: (String) -> Unit = { },
) {
    fun clear(): TourDetails {
        title = ""
        summary = ""
        startLocation.clear()
        endLocation.clear()
        distance = ""
        time = ""
        onTitleChanged = { }
        onSummaryChanged = { }
        onStartLocationChanged = { }
        onEndLocationChanged = { }
        onDistanceChanged = { }
        onTimeChanged = { }
        return this
    }
}



