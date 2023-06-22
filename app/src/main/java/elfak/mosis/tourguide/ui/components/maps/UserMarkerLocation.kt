package elfak.mosis.tourguide.ui.components.maps

import elfak.mosis.tourguide.data.models.MyLatLng
import java.util.Date

data class UserMarkerLocation(
    val coordinates: MyLatLng = MyLatLng(),
    val date: Date? = null
)
