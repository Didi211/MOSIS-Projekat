package elfak.mosis.tourguide.domain.helper

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleMapHelper @Inject constructor() {
    fun decodePolyline(encodedPolyline: String): List<LatLng> {
        val polylinePoints = mutableListOf<Pair<Double, Double>>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < encodedPolyline.length) {
            var shift = 0
            var result = 0

            while (true) {
                val byte = encodedPolyline[index++].code - 63
                result = result or ((byte and 0x1F) shl shift)
                shift += 5
                if (byte < 0x20) break
            }

            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0

            while (true) {
                val byte = encodedPolyline[index++].code - 63
                result = result or ((byte and 0x1F) shl shift)
                shift += 5
                if (byte < 0x20) break
            }

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val decodedLat = lat / 1E5
            val decodedLng = lng / 1E5
            polylinePoints.add(Pair(decodedLat, decodedLng))
        }

        return polylinePoints.map { LatLng(it.first, it.second) }
    }

    fun distanceInMeter(startLocation: LatLng, endLocation: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            startLocation.latitude, startLocation.longitude,
            endLocation.latitude, endLocation.longitude,
            results
        )
        return results[0]
    }

    fun createLatLngBounds(locations: List<LatLng>): LatLngBounds.Builder {
        val builder = LatLngBounds.Builder()
        for (location in locations) {
            builder.include(location)
        }
        return builder
    }
}