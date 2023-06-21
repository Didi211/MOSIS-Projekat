package elfak.mosis.tourguide.data.models.tour

import com.google.firebase.firestore.DocumentId

data class TourFriendModel(
    @DocumentId
    val id: String = "",
    val tourId: String = "",
    val userId: String = ""
)
