package elfak.mosis.tourguide.domain.repository

import elfak.mosis.tourguide.data.models.notification.FriendRequestNotificationModel
import elfak.mosis.tourguide.data.models.notification.NotificationModel
import elfak.mosis.tourguide.data.models.notification.TourNotificationModel
import elfak.mosis.tourguide.data.respository.NotificationResponseType

interface NotificationRepository {
    suspend fun sendTourNotification(notification: TourNotificationModel)
    suspend fun sendTourNotificationResponse(notificationId: String, accepted: NotificationResponseType)
    suspend fun getTourNotifications(userId: String): List<TourNotificationModel>
    suspend fun getFriendRequestNotifications(userId: String): List<FriendRequestNotificationModel>
    suspend fun getBasicNotifications(userId: String): List<NotificationModel>
    suspend fun deleteTourNotification(notificationId: String)
    suspend fun isUserInvitedToTour(userId: String, tourId: String): Boolean
    suspend fun getTourNotification(notificationId: String): TourNotificationModel
    suspend fun sendFriendRequestNotification(notification: FriendRequestNotificationModel)
    suspend fun sendFriendRequestResponse(notificationId: String, accepted: NotificationResponseType)
    suspend fun getFriendRequestNotification(receiverId: String, friendId: String): FriendRequestNotificationModel
    suspend fun deleteFriendRequestNotification(notificationId: String)

}