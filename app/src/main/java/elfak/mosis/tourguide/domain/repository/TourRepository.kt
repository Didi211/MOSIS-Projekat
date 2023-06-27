package elfak.mosis.tourguide.domain.repository

import elfak.mosis.tourguide.data.models.tour.TourModel
import elfak.mosis.tourguide.data.models.tour.TourNotify

interface TourRepository {
    suspend fun getTour(tourId: String): TourModel
    suspend fun getAllTours(userId: String): List<TourModel>
    suspend fun createTour(tour: TourModel): String
    suspend fun updateTour(tourId: String, tour: TourModel)
    suspend fun deleteTour(tourId: String)
    suspend fun addFriendToTour(tourId: String, friendId: String)
    suspend fun isFriendAdded(tourId: String, friendId: String): Boolean
    suspend fun canDelete(tourId: String, userId: String): Boolean
    suspend fun deleteTourFriends(tourId: String)
    suspend fun getFriendsIds(tourId: String, userId: String): List<String>
    suspend fun leaveTour(tourId: String, userId: String)
    suspend fun getTourNotify(userId: String): TourNotify?
    suspend fun addTourNotify(tourNotify: TourNotify): TourNotify
    suspend fun updateTourNotify(id: String, tourNotify: TourNotify): TourNotify
    suspend fun removeTourNotify(id: String)
}