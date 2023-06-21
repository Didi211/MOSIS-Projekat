package elfak.mosis.tourguide.data.models.user

import com.google.firebase.firestore.DocumentId

data class FriendsModel(
    @DocumentId
    val id: String = "",
    val userId1: String = "",
    val userId2: String = ""
)