package elfak.mosis.tourguide.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import elfak.mosis.tourguide.data.models.UserModel

@Database(entities = [UserModel::class], version = 1, exportSchema = false)
abstract class TourGuideDatabase: RoomDatabase() {
    abstract fun usersDao(): UsersDatabaseDao
}