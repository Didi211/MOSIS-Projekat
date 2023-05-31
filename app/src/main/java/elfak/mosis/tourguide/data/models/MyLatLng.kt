package elfak.mosis.tourguide.data.models

import com.google.android.gms.maps.model.LatLng


data class MyLatLng(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
fun MyLatLng.toGoogleLatLng(): LatLng {
    return LatLng(latitude, longitude)
}
