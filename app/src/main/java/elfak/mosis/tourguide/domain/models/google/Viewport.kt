package elfak.mosis.tourguide.domain.models.google

import com.google.android.gms.maps.model.LatLng

data class Viewport(
    val low: LatLng,
    val high: LatLng
)