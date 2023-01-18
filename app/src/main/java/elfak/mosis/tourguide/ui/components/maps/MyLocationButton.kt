package elfak.mosis.tourguide.ui.components.maps

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideFloatingButton
import elfak.mosis.tourguide.ui.theme.DarkGreen
import elfak.mosis.tourguide.ui.theme.LightRed

@Composable 
fun MyLocationButton(buttonState: LocationState, onClick: () -> Unit) {
    val currentIcon: LocationButtonTypes = when (buttonState) {
        LocationState.LocationOff -> LocationButtonTypes.LocationOff
        LocationState.LocationOn -> LocationButtonTypes.LocationOn
        LocationState.Located -> LocationButtonTypes.Located
    }

    TourGuideFloatingButton(
        contentDescription = stringResource(id = R.string.my_location_button),
        icon = currentIcon.icon,
        backgroundColor = Color.White,
        contentColor = currentIcon.color,
        iconSize = 32.dp,
        onClick = onClick
    )
}

 // for multiple states of location button
enum class LocationState {
    LocationOff,
    LocationOn,
    Located
}

sealed class LocationButtonTypes(
    val color: Color,
    val icon: ImageVector
) {
    object LocationOff: LocationButtonTypes(
        color = LightRed,
        icon = Icons.Filled.LocationOff // permission or gps is off
    )
    object LocationOn: LocationButtonTypes(
        color = Color.Gray,
        icon = Icons.Filled.LocationOn // permission on gps off
    )
    object Located: LocationButtonTypes(
        color = DarkGreen,
        icon = Icons.Filled.LocationOn // permission and gps are on
    )
}