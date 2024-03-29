package elfak.mosis.tourguide.data.respository

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import elfak.mosis.tourguide.data.models.UserLocation
import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.data.models.user.FriendsModel
import elfak.mosis.tourguide.domain.repository.NotificationRepository
import elfak.mosis.tourguide.domain.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val notificationRepository: NotificationRepository
): UsersRepository {
    private val usersRef = firestore.collection("Users")
    private val friendsRef = firestore.collection("Friends")


    override suspend fun deleteTestUsers(fullname: String) {
        // TODO - delete user from auth and delete its images
        try {
            val result = usersRef.whereEqualTo("fullname", fullname).get().await()
            val batch = firestore.batch()
            for (document in result.documents) {
                val documentRef = usersRef.document(document.id)
                batch.delete(documentRef)
            }
            batch.commit().await()
        }
        catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun getUserData(userId: String): UserModel {
        val user = usersRef.document(userId).get().await()
        if (!user.exists()) throw Exception("User not found in the database.")
        return user.toObject<UserModel>()!!
    }

    override suspend fun updateUserData(userId: String, user: UserModel) {
        usersRef.document(userId).get().await()
            .toObject<UserModel>()
            ?: throw Exception("User not found in the database.")
        usersRef.document(userId).update(
//            UserModel::email, user.email, // email cannot be changed
            UserModel::phoneNumber.name, user.phoneNumber,
            UserModel::fullname.name, user.fullname,
//            UserModel::username.name, user.username //same
        ).await()
    }

    override suspend fun updateUserPhotos(userId: String, photos: UserModel) {
        usersRef.document(userId).update(
            UserModel::profilePhotoUrl.name, photos.profilePhotoUrl,
            UserModel::thumbnailPhotoUrl.name, photos.thumbnailPhotoUrl).await()
    }

    override suspend fun getUserFriends(userId: String): List<UserModel> {
        val friends1: List<FriendsModel>
        val friends2: List<FriendsModel>
        withContext(Dispatchers.IO) {
            friends1 = async { getFriendsForField("userId1", userId) }.await()
            friends2 = async { getFriendsForField("userId2", userId) }.await()
        }

        val friends = friends1 + friends2
        if (friends.isEmpty()) {
            return emptyList()
        }

        val friendIds = mutableListOf<String>()
        for (friend in friends) {
            //deciding which of the property has userId
            if (friend.userId1 == userId) {
                friendIds.add(friend.userId2)
            }
            else {
                friendIds.add(friend.userId1)
            }
        }

        return usersRef.whereIn(FieldPath.documentId(), friendIds)
            .get().await()
            .toObjects(UserModel::class.java)
    }

    private suspend fun getFriendsForField(fieldName: String, userId: String): List<FriendsModel> {
        return friendsRef
            .whereEqualTo(fieldName, userId)
            .get().await()
            .toObjects(FriendsModel::class.java)
    }

    override suspend fun getUserFriendRequests(userId: String): List<UserModel> {
        val notifications = notificationRepository.getFriendRequestNotifications(userId)
        if (notifications.isEmpty())
            return emptyList()

        // collecting senders
        val senders: MutableSet<String> = mutableSetOf()
        for (notification in notifications) {
            senders.add(notification.notification.senderId)
        }

        // fetching senders data
        return usersRef.whereIn(FieldPath.documentId(), senders.toList())
            .get().await()
            .toObjects(UserModel::class.java)

    }

    override suspend fun searchFriends(userId: String, searchText: String): List<UserModel> {
        val users = usersRef
            .whereNotEqualTo(FieldPath.documentId(), userId)
            .get().await()
        val searchLowercase = searchText.lowercase()
        // expensive operation for larger number of users
        // in case of optimizing search another solution, eg Elasticsearch or Algolia
        return users.toObjects(UserModel::class.java)
            .filter { user ->
                user.fullname
                    .lowercase()
                    .contains(searchLowercase)
                || user.username
                    .lowercase()
                    .contains(searchLowercase)
                || user.email
                    .lowercase()
                    .contains(searchLowercase)
            }
    }

    override suspend fun addFriend(userId: String, friendId: String) {
        friendsRef.add(FriendsModel(
            userId1 = userId,
            userId2 = friendId
        )).await()
    }

    override suspend fun areFriends(userId: String, friendId: String): Boolean {
        val friends = findFriendShip(userId, friendId)
        return friends != null
    }

    override suspend fun removeFriend(userId: String, friendId: String) {
        val friends = findFriendShip(userId, friendId)
            ?: throw Exception("Can't remove. Users are not friends.")

        friendsRef.document(friends.id).delete().await()
    }

    override suspend fun updateUserLocation(userId: String, location: UserLocation) {
        usersRef.document(userId).update("location",location.toMap()).await()
    }



    private suspend fun findFriendShip(userId: String, friendId: String): FriendsModel? {
        val friendShip =  friendsRef
            .whereIn("userId1", listOf(userId, friendId))
            .whereIn("userId2", listOf(userId, friendId))
            .get().await()
            .toObjects(FriendsModel::class.java)
        return friendShip.firstOrNull()
    }

    override suspend fun getUsers(ids: List<String>): Flow<List<UserModel>> = callbackFlow {
        val snapshotListener = usersRef.whereIn(FieldPath.documentId(), ids)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val users = snapshot?.toObjects(UserModel::class.java) ?: emptyList()
                trySend(users)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }
}