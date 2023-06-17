package elfak.mosis.tourguide.ui.components.icons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R

@Composable
fun CancelIcon(iconColor: Color = MaterialTheme.colors.primary, onClick: () -> Unit = { }) {
    Icon(
        Icons.Filled.Close,
        stringResource(id = R.string.cancel),
        modifier = Modifier.clickable { onClick() }
            .size(30.dp),
        tint = iconColor
    )
}