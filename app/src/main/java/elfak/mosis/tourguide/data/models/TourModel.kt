package elfak.mosis.tourguide.data.models

import com.google.firebase.firestore.DocumentId

data class TourModel(
    @DocumentId
    val id: String = "",
    val title: String? = null,
    val summary: String? = null,
    val origin: PlaceModel? = null,
    val destination: PlaceModel? = null,
)
