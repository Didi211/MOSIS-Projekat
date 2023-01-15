package elfak.mosis.tourguide.ui.components.scaffold

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun TourGuideFloatingButton(
    contentDescription: String,
    icon: ImageVector = Icons.Filled.Add,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = {
        onClick()
    }) {
        Icon(
            modifier = modifier,
            imageVector = icon,
            contentDescription = contentDescription
        )
    }

}