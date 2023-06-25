package elfak.mosis.tourguide.data.models.tour.category

import elfak.mosis.tourguide.domain.models.google.PlaceLatLng

data class NearbyPlaceRequest(
    val radius: Int,
    val categoryFilter: String,
    val latLng: PlaceLatLng
)