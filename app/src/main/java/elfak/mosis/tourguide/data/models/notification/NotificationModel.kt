package elfak.mosis.tourguide.data.models.notification

import com.google.firebase.firestore.DocumentId
import elfak.mosis.tourguide.data.respository.NotificationResponseType

data class NotificationModel(
    @DocumentId
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val photoUrl: String = "",
)