package elfak.mosis.tourguide.data.models.tour

import com.google.firebase.firestore.DocumentId

data class TourNotify(
    @DocumentId
    val id: String  = "",
    val userId: String = "",
    val tourId: String = "",
    val radius: Int = 500
)