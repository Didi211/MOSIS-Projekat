package elfak.mosis.tourguide.data.respository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import elfak.mosis.tourguide.data.models.tour.TourFriendModel
import elfak.mosis.tourguide.data.models.tour.TourModel
import elfak.mosis.tourguide.domain.repository.TourRepository
import kotlinx.coroutines.tasks.await
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
        val queryResult = toursRef.whereEqualTo("createdBy",userId).get().await()
        return queryResult.toObjects(TourModel::class.java)
    }

    override suspend fun createTour(tour: TourModel) {
        toursRef.add(tour).await()
    }

    override suspend fun updateTour(tourId: String, tour: TourModel) {
        toursRef.document(tourId).set(tour).await()
    }

    override suspend fun deleteTour(tourId: String) {
        toursRef.document(tourId).delete().await()
    }

    override suspend fun addFriendToTour(tourId: String, friendId: String) {
        tourFriendsRef.add(TourFriendModel(tourId, friendId)).await()
    }
}