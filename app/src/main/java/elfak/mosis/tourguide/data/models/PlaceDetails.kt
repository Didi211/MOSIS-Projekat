package elfak.mosis.tourguide.data.models

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.Place

data class PlaceDetails(
    val id: String = "",
    val address: String? = null,
    val name: String? = null,
    val type: String? = null,
    val rating: Double? = null,
    val iconUrl: String? = null,
    val viewport: LatLngBounds? = null,
    val location: LatLng? = null
) {
    companion object {
        fun convert(place: Place): PlaceDetails {
            return PlaceDetails(
                address = place.address,
                name = place.name,
                type = formatType(place.types?.get(0).toString()),
                rating = place.rating,
                iconUrl = place.iconUrl,
                viewport = place.viewport,
                location = place.latLng
            )
        }
    }
}

private fun formatType(type: String): String {
    return type
        .replace('_',' ')
        .lowercase()
        .replaceFirstChar { it.uppercase() }
}