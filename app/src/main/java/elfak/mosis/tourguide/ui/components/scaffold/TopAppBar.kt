package elfak.mosis.tourguide.ui.components.scaffold

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable

@Composable
fun TourGuideTopAppBar(title: String, onIconClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onIconClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "navigation"
                )
            }
        }
    )
}