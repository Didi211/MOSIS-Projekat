package elfak.mosis.tourguide.data.respository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import elfak.mosis.tourguide.data.models.notification.FriendRequestNotificationModel
import elfak.mosis.tourguide.data.models.notification.NotificationModel
import elfak.mosis.tourguide.data.models.notification.TourNotificationModel
import elfak.mosis.tourguide.data.models.notification.TourNotificationType
import elfak.mosis.tourguide.domain.repository.NotificationRepository
import elfak.mosis.tourguide.domain.repository.TourRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val tourRepository: TourRepository
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
        if (tourRepository.isFriendAdded(tourId,userId))
            return true
        val notification = tourNotificationsRef
            .whereEqualTo("notification.receiverId", userId)
            .whereEqualTo("tourId", tourId)
            .whereEqualTo("notificationType", TourNotificationType.Invite)
            .get().await()
            .toObjects(TourNotificationModel::class.java).firstOrNull()
            ?: return false // notification is deleted => user declined request, we can spam them hehe

        if (notification.status == NotificationResponseType.Waiting)
            return true
        return false //spaaam hehe
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

    override suspend fun deleteTourNotifications(tourId: String) {
        val notifications = tourNotificationsRef.whereEqualTo("tourId", tourId)
            .get().await()
            .toObjects(TourNotificationModel::class.java)
        withContext(Dispatchers.IO) {
            if (notifications.isNotEmpty()) {
                for(notification in notifications) {
                    launch { tourNotificationsRef.document(notification.notification.id).delete().await() }
                }
            }
        }

    }

    override suspend fun updatePhotoUrls(userId: String, photoUrl: String) {

        val tourNotifications = tourNotificationsRef
            .whereEqualTo("notification.senderId", userId)
            .get().await()
            .toObjects(TourNotificationModel::class.java)
        val friendRequests = friendRequestNotificationsRef
            .whereEqualTo("notification.senderId", userId)
            .get().await()
            .toObjects(FriendRequestNotificationModel::class.java)
        if (tourNotifications.isNotEmpty()) {
            for(notif in tourNotifications) {
                tourNotificationsRef.document(notif.notification.id)
                    .update("notification.photoUrl", photoUrl)
            }
        }
        if (friendRequests.isNotEmpty()) {
            for(notif in friendRequests) {
                friendRequestNotificationsRef.document(notif.notification.id)
                    .update("notification.photoUrl", photoUrl)
            }
        }

    }

    override suspend fun deleteTourNotificationForReceiver(tourId: String, userId: String) {

        val tourNotification = tourNotificationsRef
            .whereEqualTo("notification.receiverId", userId)
            .whereEqualTo("tourId", tourId)
            .get().await()
            .toObjects(TourNotificationModel::class.java).singleOrNull()
        withContext(Dispatchers.IO) {
            if (tourNotification != null ){
                tourNotificationsRef.document(tourNotification.notification.id).delete().await()
            }
        }
    }
}
enum class NotificationResponseType {
    Waiting,
    Accepted,
    Declined
}