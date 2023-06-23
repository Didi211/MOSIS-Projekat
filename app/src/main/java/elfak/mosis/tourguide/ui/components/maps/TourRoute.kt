package elfak.mosis.tourguide.ui.components.maps

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.Polyline
import elfak.mosis.tourguide.ui.theme.RouteBlue
import elfak.mosis.tourguide.ui.theme.RouteBorderBlue

@Composable
fun TourRoute(polylinePoints: List<LatLng>) {
    //border of the route
    Polyline(
        points = polylinePoints,
//                        pattern = pattern
        color = RouteBorderBlue,
        width = 25f,
        startCap = RoundCap(),
        endCap = RoundCap()
    )
    //route
    Polyline(
        points = polylinePoints,
//                        pattern = pattern
        color = RouteBlue,
        width = 15f,
        startCap = RoundCap(),
        endCap = RoundCap()
    )
}