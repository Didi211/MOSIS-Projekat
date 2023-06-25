package elfak.mosis.tourguide.domain.models.tour

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.data.models.MyLatLng

data class CategoryMarker(
    val location: LatLng = LatLng(0.0, 0.0),
    var icon: Float = DefaultMarkerIcon
) {
    companion object {
        const val DefaultMarkerIcon = 160f
        const val SelectedMarkerIcon = 220f
    }
}
