package elfak.mosis.tourguide.data.respository

import elfak.mosis.tourguide.data.database.UsersDatabaseDao
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val usersDatabaseDao: UsersDatabaseDao
) {
//    suspend fun createUser(user: UserModel) {
//        usersDatabaseDao.insert(user)
//        Log.i("ok", user.username)
//    }
//    fun getAllUsers(): Flow<List<UserModel>>
//        = usersDatabaseDao
//            .getUsers()
//            .flowOn(Dispatchers.IO)
//            .conflate()
}