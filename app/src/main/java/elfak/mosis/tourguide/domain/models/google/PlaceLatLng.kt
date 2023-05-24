package elfak.mosis.tourguide.domain.models.google

import com.google.android.gms.maps.model.LatLng


data class PlaceLatLng(
    val lat: String = "",
    val lng: String = "",
) {
    companion object {
        fun toLatLng(latlng: LatLng): PlaceLatLng {
            return PlaceLatLng(
                lat = latlng.latitude.toString(),
                lng = latlng.longitude.toString()
            )
        }
    }
}
