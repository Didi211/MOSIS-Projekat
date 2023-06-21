package elfak.mosis.tourguide.domain.repository

import elfak.mosis.tourguide.data.models.UserModel

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

}