package elfak.mosis.tourguide.domain.models.notification

import com.google.firebase.firestore.DocumentId
import elfak.mosis.tourguide.data.models.notification.TourNotificationType
import elfak.mosis.tourguide.data.respository.NotificationResponseType

data class NotificationCard(
    @DocumentId
    val id: String = "",
    val tourId: String? = null,
    val photoUrl: String? = null,
    val message: String = "",
    val tourNotificationType: TourNotificationType,
    var status: NotificationResponseType = NotificationResponseType.Waiting
)