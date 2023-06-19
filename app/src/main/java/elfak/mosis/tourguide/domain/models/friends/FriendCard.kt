package elfak.mosis.tourguide.domain.models.friends

import elfak.mosis.tourguide.domain.models.Photo

data class FriendCard(
    val id: String = "",
    val photoUrl: String? = null,
    val fullname: String = "",
    val username: String = ""
)