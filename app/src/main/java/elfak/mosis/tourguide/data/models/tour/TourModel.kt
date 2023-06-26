package elfak.mosis.tourguide.data.models.tour

import com.google.firebase.firestore.DocumentId
import elfak.mosis.tourguide.data.models.PlaceModel
import elfak.mosis.tourguide.domain.models.tour.TourCard
import elfak.mosis.tourguide.domain.models.tour.TourSelectionDisplay

data class TourModel(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val summary: String? = null,
    val origin: PlaceModel? = null,
    val destination: PlaceModel? = null,
    val waypoints: List<PlaceModel>? = null,
    val createdBy: String = "",
) {
    fun toTourCard(): TourCard {
        return TourCard(
            id = id,
            title = title,
            summary = summary?: "",
            createdBy = createdBy
        )
    }
    fun toTourSelectionDisplay(): TourSelectionDisplay {
        return TourSelectionDisplay(
            id = id,
            title = title,
            summary = summary ?: ""
        )
    }
    fun toUpdateMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "summary" to summary,
            "origin" to origin?.toMap(),
            "destination" to destination?.toMap(),
            "waypoints" to waypoints?.map { waypoint -> waypoint.toMap()}
        )
    }
}


