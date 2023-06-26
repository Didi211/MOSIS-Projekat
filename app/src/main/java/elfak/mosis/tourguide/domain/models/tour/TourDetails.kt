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

    var waypoints: List<Place> = emptyList(),
    var previousWaypoints: List<Place> = emptyList(),
    var onWaypointRemoved: (Place) -> Unit = { },
    var onWaypointAdded: (Place) -> Unit = { },

    var distance: String = "",
    var onDistanceChanged: (String) -> Unit = { },

    var time: String = "",
    var onTimeChanged: (String) -> Unit = { },

    var bothLocationsSet:Boolean = false,
    var onBothLocationsSet: (Boolean) -> Unit = { },

    var shouldRedrawRoute: Boolean = false,
    var onRouteRedraw: (Boolean) -> Unit = { },

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
            polylinePoints = emptyList(),
            shouldRedrawRoute = false,
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
            polylinePoints = emptyList(),
            shouldRedrawRoute = areLocationSet(tour.origin, tour.destination),
            waypoints = if(tour.waypoints != null) tour.waypoints.map { waypoint -> waypoint.toPlace()} else emptyList()

        )
    }
    private fun areLocationSet(origin: PlaceModel?, destination: PlaceModel?): Boolean {
        if (origin == null) return false
        if (destination == null) return false
        if (origin.id.isBlank()) return false
        if (destination.id.isBlank()) return false
        return true
    }
}

fun TourDetails.toTourModel(createdBy: String? = null): TourModel {
    return TourModel(
        title = title,
        summary = summary,
        origin = origin.toPlaceModel(),
        destination = destination.toPlaceModel(),
        waypoints = waypoints.map { waypoint -> waypoint.toPlaceModel() },
        createdBy = createdBy ?: ""
    )
}





private fun mockWaypoints(): List<Place> {
    var list = mutableListOf<Place>()
    for (i in 1..5) {
        list.add(Place(id = i.toString(), address = "Text $i"))
    }
    return list.toList()
}
