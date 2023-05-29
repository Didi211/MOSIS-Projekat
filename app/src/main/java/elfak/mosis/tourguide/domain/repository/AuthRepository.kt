package elfak.mosis.tourguide.domain.repository

interface AuthRepository {
//     suspend fun saveUserIdLocal(userId: String)
     suspend fun getUserIdLocal(): String?
//     suspend fun removeUserIdLocal()

    suspend fun login(username: String, password: String)
    suspend fun register(username: String, password: String, email: String, fullname: String): Any
    suspend fun logout()
}