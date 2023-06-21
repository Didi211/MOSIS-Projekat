package elfak.mosis.tourguide.data.models

import com.google.android.gms.maps.model.LatLng


data class MyLatLng(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) {
    fun toGoogleLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
    fun toMap(): MutableMap<String, Double> {
        return hashMapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )
    }

}
