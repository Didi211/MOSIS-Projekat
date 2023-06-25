package elfak.mosis.tourguide.domain.models

import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.data.models.MyLatLng
import elfak.mosis.tourguide.data.models.PlaceModel



data class Place(
    var id: String = "",
    var address: String = "",
    var location: LatLng = LatLng(0.0,0.0),
    var name: String = ""
) {
    fun clear(): Place {
        return this.copy(
            id = "",
            address = "",
            location = LatLng(0.0,0.0),
            name = ""
        )
    }
}

fun Place.toPlaceModel(): PlaceModel {
    return PlaceModel(
        id = id,
        location = MyLatLng(location.latitude, location.longitude),
        address = address
    )
}

