package elfak.mosis.tourguide.ui.components.scaffold

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TourGuideFloatingButton(
    contentDescription: String,
    icon: ImageVector = Icons.Filled.Add,
    iconSize: Dp = 40.dp,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    contentColor: Color = MaterialTheme.colors.onSecondary,
    shape: Shape = CircleShape,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        shape = shape,
        onClick = {
        onClick()
    }) {
        Icon(
            icon,
            modifier = Modifier.size(iconSize),
            contentDescription = contentDescription
        )
    }

}