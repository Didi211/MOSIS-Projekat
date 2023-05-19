package elfak.mosis.tourguide.domain.models.google

import com.google.geo.type.Viewport

data class RouteRequest(
    val origin: Waypoint,
    val destination: Waypoint,
    val units: String = "METRIC",
)
