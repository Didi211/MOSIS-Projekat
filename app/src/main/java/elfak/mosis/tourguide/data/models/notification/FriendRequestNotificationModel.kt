package elfak.mosis.tourguide.data.models.notification

import elfak.mosis.tourguide.data.respository.NotificationResponseType


data class FriendRequestNotificationModel(
    val notification: NotificationModel = NotificationModel(),
    val status: NotificationResponseType = NotificationResponseType.Waiting
)