package elfak.mosis.tourguide.data.respository

import android.util.Log
import elfak.mosis.tourguide.data.database.UsersDatabaseDao
import elfak.mosis.tourguide.data.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
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