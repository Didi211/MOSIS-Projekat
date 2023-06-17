package elfak.mosis.tourguide.data.models

import com.google.firebase.firestore.DocumentId


data class UserModel(
    @DocumentId
    var id: String = "",
    var authId: String = "",
    val username: String = "",
    val fullname: String = "",
    val email: String = "",
//    val password: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
)
