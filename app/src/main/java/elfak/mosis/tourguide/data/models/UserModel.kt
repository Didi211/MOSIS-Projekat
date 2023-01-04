package elfak.mosis.tourguide.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserModel(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
//    @ColumnInfo(name = "username")
    val username: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val loggedIn: Boolean
)
