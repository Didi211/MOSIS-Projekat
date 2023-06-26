package elfak.mosis.tourguide.domain.repository

import elfak.mosis.tourguide.data.models.UserLocation
import elfak.mosis.tourguide.data.models.UserModel
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun deleteTestUsers(fullname: String)
    suspend fun getUserData(userId: String): UserModel
    suspend fun updateUserData(userId: String, user: UserModel)
    suspend fun updateUserPhotos(userId: String, photos: UserModel)
    suspend fun getUserFriends(userId: String): List<UserModel>
    suspend fun getUserFriendRequests(userId: String): List<UserModel>
    suspend fun searchFriends(userId: String, searchText: String): List<UserModel>
    suspend fun addFriend(userId: String, friendId: String)
    suspend fun areFriends(userId: String, friendId: String): Boolean
    suspend fun removeFriend(userId: String, friendId: String)
    suspend fun getUsers(ids: List<String>): Flow<List<UserModel>>
    suspend fun updateUserLocation(userId: String, location: UserLocation)
    suspend fun setTourNotify(userId: String, tourId: String)
}