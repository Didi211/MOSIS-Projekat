package elfak.mosis.tourguide.domain.repository

import elfak.mosis.tourguide.data.models.UserModel

interface AuthRepository {
//     suspend fun saveUserIdLocal(userId: String)
     suspend fun getUserAuthIdLocal(): String?
     suspend fun getUserIdLocal(): String?

    suspend fun login(email: String, password: String)
    suspend fun register(user: UserModel, password: String): String
    suspend fun tryRegister(username: String): Boolean
    suspend fun logout()
    suspend fun changePassword(password: String)
}