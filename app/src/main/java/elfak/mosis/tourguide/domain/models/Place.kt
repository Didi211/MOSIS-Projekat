package elfak.mosis.tourguide.domain.models

import com.google.android.gms.maps.model.LatLng


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
}

