package elfak.mosis.tourguide.data.respository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import elfak.mosis.tourguide.data.models.notification.FriendRequestNotificationModel
import elfak.mosis.tourguide.data.models.notification.NotificationModel
import elfak.mosis.tourguide.data.models.notification.TourNotificationModel
import elfak.mosis.tourguide.domain.repository.NotificationRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore
): NotificationRepository {

    private val tourNotificationsRef = firestore.collection("TourNotifications")
    private val friendRequestNotificationsRef = firestore.collection("FriendRequestNotifications")
    private val basicNotificationsRef = firestore.collection("BasicNotifications")


    override suspend fun sendTourNotification(notification: TourNotificationModel) {
        tourNotificationsRef.add(notification).await()
    }

    override suspend fun sendTourNotificationResponse(notificationId: String, accepted: NotificationResponseType) {
        tourNotificationsRef.document(notificationId)
            .update("status", accepted)
            .await()
    }

    override suspend fun getTourNotifications(userId: String): List<TourNotificationModel> {
        val notifications = tourNotificationsRef
            .whereEqualTo("notification.receiverId", userId)
            .get().await()
        return notifications.toObjects(TourNotificationModel::class.java)
    }

    override suspend fun getFriendRequestNotifications(userId: String): List<FriendRequestNotificationModel> {
        return friendRequestNotificationsRef.whereEqualTo(
            "notification.receiverId", userId)
            .get().await()
            .toObjects(FriendRequestNotificationModel::class.java)
    }

    override suspend fun getBasicNotifications(userId: String): List<NotificationModel> {
        return emptyList()
    }

    override suspend fun deleteTourNotification(notificationId: String) {
        tourNotificationsRef.document(notificationId).delete().await()
    }

    override suspend fun isUserInvitedToTour(userId: String, tourId: String): Boolean {
//        val notification =tourNotificationsRef.whereEqualTo(
//            "notification.receiverId", userId,
//            )
//            .whereEqualTo("tourId", tourId)
//            .get().await()
//            .toObjects(TourNotificationModel::class.java)[0]
//        return notification.notification.status != NotificationResponseType.Declined
        return false
    }

    override suspend fun getTourNotification(notificationId: String): TourNotificationModel {
        return tourNotificationsRef.document(notificationId).get().await()
            .toObject<TourNotificationModel>()
            ?: throw Exception("Tour Notification not found.")
    }

    override suspend fun sendFriendRequestNotification(notification: FriendRequestNotificationModel) {
        friendRequestNotificationsRef.add(notification).await()
    }

    override suspend fun sendFriendRequestResponse(notificationId: String, accepted: NotificationResponseType) {
        friendRequestNotificationsRef.document(notificationId)
            .update("status", accepted)
            .await()
    }

    override suspend fun getFriendRequestNotification(receiverId: String, friendId: String): FriendRequestNotificationModel {
        return friendRequestNotificationsRef
            .whereEqualTo("notification.receiverId", receiverId)
            .whereEqualTo("notification.senderId",friendId)
            .get().await()
            .toObjects(FriendRequestNotificationModel::class.java)[0]
    }

    override suspend fun deleteFriendRequestNotification(notificationId: String) {
        friendRequestNotificationsRef.document(notificationId).delete().await()
    }
}

enum class NotificationResponseType {
    Waiting,
    Accepted,
    Declined
}