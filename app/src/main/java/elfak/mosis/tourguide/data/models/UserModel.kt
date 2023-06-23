package elfak.mosis.tourguide.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import elfak.mosis.tourguide.domain.models.friends.FriendCard
import elfak.mosis.tourguide.ui.components.maps.FriendMarker
import elfak.mosis.tourguide.ui.components.maps.UserMarkerLocation
import java.util.Date


data class UserModel(
    @DocumentId
    var id: String = "",
    var authId: String = "",
    val username: String = "",
    val fullname: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profilePhotoUrl: String = "",
    val thumbnailPhotoUrl: String = "",
    val location: UserLocation = UserLocation()
) {
    fun toFriendCard(): FriendCard {
        return FriendCard(
            id = id,
            photoUrl = profilePhotoUrl,
            fullname = fullname,
            username = username,
        )
    }

    fun toFriendMarker(): FriendMarker {
        return FriendMarker(
            id = id,
            fullname = fullname,
            phoneNumber = phoneNumber,
            location = location.toUserMarkerLocation(),
            photoUrl = thumbnailPhotoUrl
        )
    }
}


data class UserLocation(
    val coordinates: MyLatLng = MyLatLng(),
    @ServerTimestamp
    val date: Date = Date()
) {
    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "coordinates" to coordinates.toMap(),
            "date" to  FieldValue.serverTimestamp()
        )
    }
    fun toUserMarkerLocation(): UserMarkerLocation {
        return UserMarkerLocation(
            coordinates = coordinates,
            date = date
        )
    }
}
