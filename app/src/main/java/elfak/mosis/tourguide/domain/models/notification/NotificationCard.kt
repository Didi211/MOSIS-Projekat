package elfak.mosis.tourguide.domain.models.notification

import com.google.firebase.firestore.DocumentId

data class NotificationCard(
    @DocumentId
    val id: String = "",
    val tourId: String? = null,
    val photoUrl: String? = null,
    val message: String = ""
)