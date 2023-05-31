package elfak.mosis.tourguide.domain.models

import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.data.models.MyLatLng
import elfak.mosis.tourguide.data.models.PlaceModel
import elfak.mosis.tourguide.data.models.toGoogleLatLng


data class Place(
    var id: String = "",
    var address: String = "",
    var location: LatLng = LatLng(0.0,0.0)
) {
    fun clear(): Place {
        return this.copy(
            id = "",
            address = "",
            location = LatLng(0.0,0.0)
        )
    }
    companion object {
        fun convert(place: PlaceModel): Place {
            return Place(
                id = place.id,
                address = place.address,
                location = place.location.toGoogleLatLng()
            )
        }
    }
}

fun Place.toPlaceModel(): PlaceModel {
    return PlaceModel(
        id = id,
        location = MyLatLng(location.latitude, location.longitude),
        address = address
    )
}

