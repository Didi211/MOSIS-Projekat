package elfak.mosis.tourguide.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.google.type.Date
import elfak.mosis.tourguide.domain.models.friends.FriendCard
import java.sql.Timestamp


data class UserModel(
    @DocumentId
    var id: String = "",
    var authId: String = "",
    val username: String = "",
    val fullname: String = "",
    val email: String = "",
//    val password: String = "",
    val phoneNumber: String = "",
    val profilePhotoUrl: String = "",
    val thumbnailPhotoUrl: String = "",
    val userLocation: UserLocation = UserLocation()
) {
    fun toFriendCard(): FriendCard {
        return FriendCard(
            id = id,
            photoUrl = profilePhotoUrl,
            fullname = fullname,
            username = username
        )
    }
}


data class UserLocation(
    val location: MyLatLng = MyLatLng(),
    @ServerTimestamp
    val date: Timestamp? = null
) {
    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "coordinates" to location.toMap(),
            "date" to  FieldValue.serverTimestamp()
        )
    }
}
