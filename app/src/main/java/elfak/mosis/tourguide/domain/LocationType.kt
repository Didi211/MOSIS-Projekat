package elfak.mosis.tourguide.domain

//enum class LocationType {
//    Origin,
//    Waypoint,
//    Destination,
//}

sealed class LocationType(val name: String, val description: String) {
    companion object {
        fun values(): List<LocationType> = listOf(Origin, Waypoint, Destination)
    }

    object Origin : LocationType("Origin","Origin location.")
    object Waypoint : LocationType("Waypoint","Location to stop in the meantime.")
    object Destination : LocationType("Destination","Destination location.")
}