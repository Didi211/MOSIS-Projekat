package elfak.mosis.tourguide.domain.services.foreground

data class LocationServiceState(
    val userId: String = "",
    val isListenerRegistered: Boolean = false,
)
