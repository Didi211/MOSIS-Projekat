package elfak.mosis.tourguide.data.respository

import com.google.firebase.firestore.FirebaseFirestore
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
            .update("notification.status", accepted)
            .await()
    }

    override suspend fun getTourNotifications(userId: String): List<TourNotificationModel> {
        val notifications = tourNotificationsRef
            .whereEqualTo("notification.receiverId", userId)
            .get().await()
        return notifications.toObjects(TourNotificationModel::class.java)
    }

    override suspend fun getFriendRequestNotifications(userId: String): List<FriendRequestNotificationModel> {
        return emptyList()
    }

    override suspend fun getBasicNotifications(userId: String): List<NotificationModel> {
        return emptyList()
    }

    override suspend fun deleteTourNotification(notificationId: String) {
        tourNotificationsRef.document(notificationId).delete().await()
    }

}

enum class NotificationResponseType {
    Waiting,
    Accepted,
    Declined
}