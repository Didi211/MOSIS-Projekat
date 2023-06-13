package elfak.mosis.tourguide.domain.repository

interface UsersRepository {
    suspend fun deleteTestUsers(fullname: String)
}