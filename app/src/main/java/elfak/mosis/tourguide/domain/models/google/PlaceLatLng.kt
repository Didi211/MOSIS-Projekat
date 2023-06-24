package elfak.mosis.tourguide.domain.models.google

import com.google.android.gms.maps.model.LatLng


data class PlaceLatLng(
    val lat: String = "",
    val lng: String = "",
) {
    fun toLatLng(): LatLng {
        return LatLng(lat.toDouble(),lng.toDouble())
    }
}

fun LatLng.toPlaceLatLng(): PlaceLatLng {
    return PlaceLatLng(
        lat = this.latitude.toString(),
        lng = this.longitude.toString()
    )
}
