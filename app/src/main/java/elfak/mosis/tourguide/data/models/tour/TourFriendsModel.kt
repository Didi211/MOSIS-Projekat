package elfak.mosis.tourguide.data.models.tour

import com.google.firebase.firestore.DocumentId

data class TourFriendsModel(
    @DocumentId
    val id: String = "",
    val tourId: String = "",
    val users: List<String> = emptyList()
)