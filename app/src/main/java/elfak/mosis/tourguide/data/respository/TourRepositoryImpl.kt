package elfak.mosis.tourguide.data.respository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import elfak.mosis.tourguide.data.models.TourModel
import elfak.mosis.tourguide.domain.repository.TourRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TourRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): TourRepository {
    private val collectionRef = firestore.collection("Tours")

    override suspend fun getTour(tourId: String): TourModel {
        val tour = collectionRef.document(tourId).get().await()
        if (tour == null) {
            throw Exception("Tour not found in the database.")
        }
        return tour.toObject<TourModel>()!!
    }

    override suspend fun getAllTours(): List<TourModel> {
        TODO("Not yet implemented")
    }

    override suspend fun createTour(tour: TourModel) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTour(tourId: String, tour: TourModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTour(tourId: String) {
        TODO("Not yet implemented")
    }
}