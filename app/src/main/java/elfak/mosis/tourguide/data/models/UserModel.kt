package elfak.mosis.tourguide.data.models

import com.google.firebase.firestore.DocumentId
import elfak.mosis.tourguide.domain.models.friends.FriendCard


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
    val thumbnailPhotoUrl: String = ""
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
