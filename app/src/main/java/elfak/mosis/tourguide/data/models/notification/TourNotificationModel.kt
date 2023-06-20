package elfak.mosis.tourguide.data.models.notification

import elfak.mosis.tourguide.data.respository.NotificationResponseType
import elfak.mosis.tourguide.domain.models.notification.NotificationCard

data class TourNotificationModel(
    val notification: NotificationModel = NotificationModel(),
    val notificationType: TourNotificationType = TourNotificationType.Invite,
    val tourId: String = "",
    val status: NotificationResponseType = NotificationResponseType.Waiting
) {
    fun toNotificationCard(): NotificationCard {
        return NotificationCard(
            id = notification.id,
            tourId = tourId,
            photoUrl = notification.photoUrl,
            message = notification.message,
            tourNotificationType = notificationType,
            answered = status
        )
    }

}