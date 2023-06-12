package elfak.mosis.tourguide.domain.repository

import elfak.mosis.tourguide.data.models.UserModel

interface AuthRepository {
//     suspend fun saveUserIdLocal(userId: String)
     suspend fun getUserIdLocal(): String?
//     suspend fun removeUserIdLocal()

    suspend fun login(username: String, password: String)
    suspend fun register(user: UserModel): Any
    suspend fun logout()
}