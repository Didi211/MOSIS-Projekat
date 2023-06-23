package elfak.mosis.tourguide.ui.components.maps


data class FriendMarker(
    val id: String = "",
    val fullname: String = "",
    val phoneNumber: String = "",
    val location: UserMarkerLocation = UserMarkerLocation(),
    val photoUrl: String = ""
)