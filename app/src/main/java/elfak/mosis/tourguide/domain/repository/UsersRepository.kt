package elfak.mosis.tourguide.domain.repository

import elfak.mosis.tourguide.data.models.UserModel

interface UsersRepository {
    suspend fun deleteTestUsers(fullname: String)
    suspend fun getUserData(userId: String): UserModel
    suspend fun updateUserData(userId: String, user: UserModel)
    suspend fun updateUserPhotos(userId: String, photos: UserModel)
}