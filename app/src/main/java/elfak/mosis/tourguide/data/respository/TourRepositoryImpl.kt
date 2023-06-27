package elfak.mosis.tourguide.data.respository

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import elfak.mosis.tourguide.data.models.tour.TourFriendsModel
import elfak.mosis.tourguide.data.models.tour.TourModel
import elfak.mosis.tourguide.data.models.tour.TourNotify
import elfak.mosis.tourguide.domain.repository.TourRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TourRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore
): TourRepository {
    private val toursRef = firestore.collection("Tours")
    private val tourFriendsRef = firestore.collection("TourFriends")
    private val tourNotifyRef = firestore.collection("TourNotify")

    override suspend fun getTour(tourId: String): TourModel {
        if (tourId.isBlank())
            throw NoSuchFieldException("Tour not found in the database.")
        val tour = toursRef.document(tourId).get().await()
            ?: throw Exception("Tour not found in the database.")
        return tour.toObject<TourModel>()!!
    }

    override suspend fun getAllTours(userId: String): List<TourModel> {
        val tourIds = tourFriendsRef.whereArrayContains("users", userId)
            .get().await()
            .toObjects(TourFriendsModel::class.java).map { tour -> tour.tourId }

        var tours: MutableList<TourModel> = mutableListOf()
        if (tourIds.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                tours = toursRef.whereIn(FieldPath.documentId(), tourIds)
                    .get().await()
                    .toObjects(TourModel::class.java)
            }
        }
        return tours
    }

    override suspend fun createTour(tour: TourModel):String {
        return toursRef.add(tour).await().id
    }

    override suspend fun updateTour(tourId: String, tour: TourModel) {
        toursRef.document(tourId).update(tour.toUpdateMap()).await()
    }

    override suspend fun deleteTour(tourId: String) {
        toursRef.document(tourId).delete().await()

    }
    override suspend fun deleteTourFriends(tourId: String) {
        val tour = getTourFromTourFriends(tourId)
        tour?.let { tourFriendsRef.document(it.id).delete().await() }
    }

    override suspend fun getFriendsIds(tourId: String, userId: String): List<String> {
        val tour = getTourFromTourFriends(tourId) ?: throw Exception("Tour not found.")
        return tour.users.filter { id -> id != userId  }
    }

    override suspend fun leaveTour(tourId: String, userId: String) {
        val tour = tourFriendsRef.whereEqualTo("tourId", tourId).get().await()
            .toObjects(TourFriendsModel::class.java)
            .singleOrNull()
            ?: throw Exception("Tour not found.")

        val newUsers = tour.users.filter { x -> x != userId }
        tourFriendsRef.document(tour.id).update("users", newUsers).await()
    }

    override suspend fun getTourNotify(userId: String): TourNotify? {
        return tourNotifyRef
            .whereEqualTo("userId", userId)
            .get().await()
            .toObjects(TourNotify::class.java)
            .singleOrNull()
    }

    override suspend fun addTourNotify(tourNotify: TourNotify): TourNotify {
        val tourNotify = tourNotifyRef.add(tourNotify).await().get().await().toObject<TourNotify>()
        return tourNotify!!
    }

    override suspend fun updateTourNotify(id: String, tourNotify: TourNotify): TourNotify {
        tourNotifyRef.document(id).set(tourNotify).await()
        val tourNotify = tourNotifyRef.document(id).get().await().toObject<TourNotify>()
        return tourNotify!!
    }

    override suspend fun removeTourNotify(id: String) {
        tourNotifyRef.document(id).delete().await()
    }

    private suspend fun getTourFromTourFriends(tourId: String): TourFriendsModel? {
        return tourFriendsRef.whereEqualTo("tourId", tourId).get().await()
            .toObjects(TourFriendsModel::class.java)
            .singleOrNull()
    }

    override suspend fun addFriendToTour(tourId: String, friendId: String) {
        val tourFriends = getTourFromTourFriends(tourId)
        if (tourFriends == null) {
            tourFriendsRef.add(TourFriendsModel(tourId = tourId, users = listOf(friendId))).await()
        }
        else {
            tourFriendsRef.document(tourFriends.id).update("users", FieldValue.arrayUnion(friendId)).await()
        }
    }

    override suspend fun isFriendAdded(tourId: String, friendId: String): Boolean {
        val friendFound = tourFriendsRef
            .whereEqualTo("tourId", tourId)
            .whereArrayContains("users", friendId)
            .get().await()
            .toObjects(TourFriendsModel::class.java)
            .singleOrNull()
        return friendFound != null
    }

    override suspend fun canDelete(tourId: String, userId: String): Boolean {
        return getTour(tourId).createdBy == userId
    }
}