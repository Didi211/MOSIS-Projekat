package elfak.mosis.tourguide.data.models

import com.google.firebase.firestore.DocumentId
import elfak.mosis.tourguide.domain.models.tour.TourCard
import elfak.mosis.tourguide.domain.models.tour.TourSelectionDisplay

data class TourModel(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val summary: String? = null,
    val origin: PlaceModel? = null,
    val destination: PlaceModel? = null,
    val createdBy: String = "",
//    val guests: List<String>? = null
) {
    fun toTourCard(): TourCard {
        return TourCard(
            id = id,
            title = title,
            summary = summary?: ""
        )
    }
    fun toTourSelectionDisplay(): TourSelectionDisplay {
        return TourSelectionDisplay(
            id = id,
            title = title,
            summary = summary ?: ""
        )
    }
}


