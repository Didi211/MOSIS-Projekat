package elfak.mosis.tourguide.domain.models.google


data class RouteRequest(
    val origin: Waypoint,
    val destination: Waypoint,
    val units: String = "METRIC",
)
