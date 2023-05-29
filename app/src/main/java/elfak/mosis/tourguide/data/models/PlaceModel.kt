package elfak.mosis.tourguide.data.models

import elfak.mosis.tourguide.domain.models.Place

data class PlaceModel(
    val id: String = "",
    val location: MyLatLng = MyLatLng(),
    val address: String = ""
)

fun PlaceModel.toPlace(): Place {
    return Place(
        id = id,
        location = location.toGoogleLatLng(),
        address = address
    )
}

