package elfak.mosis.tourguide.data.respository

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import elfak.mosis.tourguide.data.models.tour.TourFriendModel
import elfak.mosis.tourguide.data.models.tour.TourFriendsModel
import elfak.mosis.tourguide.data.models.tour.TourModel
import elfak.mosis.tourguide.domain.repository.TourRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TourRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): TourRepository {
    private val toursRef = firestore.collection("Tours")
    private val tourFriendsRef = firestore.collection("TourFriends")

    override suspend fun getTour(tourId: String): TourModel {
        val tour = toursRef.document(tourId).get().await()
            ?: throw Exception("Tour not found in the database.")
        return tour.toObject<TourModel>()!!
    }

    override suspend fun getAllTours(userId: String): List<TourModel> {
//        val tourIds = tourFriendsRef.whereEqualTo("userId",userId)
//            .get().await()
//            .toObjects(TourFriendModel::class.java).map { tour -> tour.tourId }
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

//        var toursUserBelongsIn: List<TourModel> = emptyList()
//        val toursCreatedByUser: List<TourModel>
//        withContext(Dispatchers.IO) {
//            if (tourIds.isNotEmpty()) {
//                toursUserBelongsIn = async {
//                    toursRef.whereIn(FieldPath.documentId(), tourIds)
//                        .get().await()
//                        .toObjects(TourModel::class.java)
//                }.await()
//            }
//
//            toursCreatedByUser = async {
//                toursRef.whereEqualTo("createdBy", userId)
//                    .get().await()
//                    .toObjects(TourModel::class.java)
//            }.await()
//        }
//        return toursCreatedByUser + toursUserBelongsIn
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

    private suspend fun getTourFromTourFriends(tourId: String): TourFriendsModel? {
        return tourFriendsRef.whereEqualTo("tourId", tourId).get().await()
            .toObjects(TourFriendsModel::class.java)
            .singleOrNull()
    }

    override suspend fun addFriendToTour(tourId: String, friendId: String) {
//        val tourFriends = tourFriendsRef.(tourId).get().await()
//            .toObject<TourFriendsModel>()

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