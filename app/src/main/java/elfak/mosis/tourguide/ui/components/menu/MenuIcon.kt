package elfak.mosis.tourguide.ui.components.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun MenuIcon(onClick: () -> Unit) {
    Column(
        Modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .size(40.dp)
            .padding(5.dp)
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            imageVector = Icons.Filled.MoreVert,
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
        )
    }
}