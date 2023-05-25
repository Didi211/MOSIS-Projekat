package elfak.mosis.tourguide.domain.models

import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.data.models.TourModel

data class TourDetails(
    var title: String = "",
    var onTitleChanged: (String) -> Unit = { },

    var summary: String = "",
    var onSummaryChanged: (String) -> Unit = { },

    var origin: Place = Place("","",LatLng(0.0,0.0)),
    var onOriginChanged: (Place) -> Unit = { },

    var destination: Place = Place("","",LatLng(0.0,0.0)),
    var onDestinationChanged: (Place) -> Unit = { },

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
            title = tour.title ?: "",
            summary = tour.summary ?: "",
            origin = if(tour.origin != null) Place.convert(tour.origin) else Place(),
            destination = if(tour.destination != null) Place.convert(tour.destination) else Place(),
            distance = "",
            time = "",
            bothLocationsSet = tour.origin != null && tour.destination != null,
            polylinePoints = emptyList()
        )
    }
    companion object {
        fun convert(tour: TourModel): TourDetails {
            return TourDetails(
                title = tour.title ?: "",
                summary = tour.summary ?: "",
                origin = if(tour.origin != null) Place.convert(tour.origin) else Place(),
                destination = if(tour.destination != null) Place.convert(tour.destination) else Place(),
                distance = "",
                time = "",
                bothLocationsSet = tour.origin != null && tour.destination != null,
                polylinePoints = emptyList()
            )
        }
    }
}




