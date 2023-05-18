package elfak.mosis.tourguide.domain.models.google

data class Route(
    val distanceMeters: Int,
    val duration: String, //in seconds
    val polyline: Polyline,
    val viewport: Viewport

)