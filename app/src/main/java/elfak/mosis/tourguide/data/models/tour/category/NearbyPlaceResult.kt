package elfak.mosis.tourguide.data.models.tour.category

import com.google.android.gms.maps.model.BitmapDescriptor
import elfak.mosis.tourguide.data.models.PlaceDetails
import elfak.mosis.tourguide.domain.models.google.PlaceLatLng
import elfak.mosis.tourguide.domain.models.tour.CategoryMarker

data class NearbyPlaceResult(
    val placeId: String,
    val name: String,
    val location: PlaceLatLng,
    val type: String,
    val vicinity: String,
    val iconUrl: String,
    val rating: Double,
    val selected: Boolean = false
) {
    fun toPlaceDetails(): PlaceDetails {
        return PlaceDetails(
            id = placeId,
            address = vicinity,
            name = name,
            type = type,
            rating = rating,
            iconUrl = iconUrl,
        )
    }
}