package elfak.mosis.tourguide.domain.models.tour

import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.data.models.PlaceModel
import elfak.mosis.tourguide.data.models.tour.TourModel
import elfak.mosis.tourguide.data.models.toPlace
import elfak.mosis.tourguide.domain.models.Place
import elfak.mosis.tourguide.domain.models.toPlaceModel

data class TourDetails(
    var title: String = "",
    var onTitleChanged: (String) -> Unit = { },

    var summary: String = "",
    var onSummaryChanged: (String) -> Unit = { },

    var origin: Place = Place("","",LatLng(0.0,0.0)),
    var onOriginChanged: (Place) -> Unit = { },

    var destination: Place = Place("","",LatLng(0.0,0.0)),
    var onDestinationChanged: (Place) -> Unit = { },

    var waypoints: List<Place> = mockWaypoints(),
    var onWaypointRemoved: (Place) -> Unit = { },

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
            origin = origin.clear(),
            destination = destination.clear(),
            distance = "",
            time = "",
            bothLocationsSet = false,
            polylinePoints = emptyList()
        )
    }
    fun update(tour: TourModel): TourDetails {
        return this.copy(
            title = tour.title,
            summary = tour.summary ?: "",
            origin = if(tour.origin != null) tour.origin.toPlace() else Place(),
            destination = if(tour.destination != null) tour.destination.toPlace() else Place(),
            distance = "",
            time = "",
            bothLocationsSet = areLocationSet(tour.origin, tour.destination),
            polylinePoints = emptyList()
        )
    }
}

fun TourDetails.toTourModel(createdBy: String? = null): TourModel {
    return TourModel(
        title = title,
        summary = summary,
        origin = origin.toPlaceModel(),
        destination = destination.toPlaceModel(),
        createdBy = createdBy ?: ""
    )
}

private fun areLocationSet(origin: PlaceModel?,  destination: PlaceModel?): Boolean {
    if (origin == null) return false
    if (destination == null) return false
    if (origin.id.isBlank()) return false
    if (destination.id.isBlank()) return false
    return true
}



private fun mockWaypoints(): List<Place> {
    var list = mutableListOf<Place>()
    for (i in 1..5) {
        list.add(Place(id = i.toString(), address = "Text $i"))
    }
    return list.toList()
}
