package elfak.mosis.tourguide.data.models

import com.google.android.gms.maps.model.LatLng

data class PlaceModel(
    val id: String = "",
    val location: LatLng = LatLng(0.0, 0.0),
    val address: String = ""
)