package elfak.mosis.tourguide.domain.repository

import elfak.mosis.tourguide.data.models.TourModel

interface TourRepository {
    suspend fun getTour(tourId: String): TourModel
    suspend fun getAllTours(userId: String): List<TourModel>
    suspend fun createTour(tour: TourModel)
    suspend fun updateTour(tourId: String, tour: TourModel)
    suspend fun deleteTour(tourId: String)
}