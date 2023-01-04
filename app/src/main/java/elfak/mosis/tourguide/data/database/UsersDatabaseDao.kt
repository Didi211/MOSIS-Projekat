package elfak.mosis.tourguide.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import elfak.mosis.tourguide.data.models.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDatabaseDao {

    @Query("SELECT * from users")
    fun getUsers(): Flow<List<UserModel>>

    @Query("SELECT * from users where id =:id")
    suspend fun getUserById(id: String): UserModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(user: UserModel)

    @Delete
    suspend fun deleteUser(user: UserModel)
}
