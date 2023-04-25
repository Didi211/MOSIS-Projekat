package elfak.mosis.tourguide.ui.components.scaffold

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TourGuideTopAppBar(
    title: String,
    icon: ImageVector = Icons.Rounded.Menu,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    onIconClick: () -> Unit = { openMenu(coroutineScope, scaffoldState) }
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (icon != Icons.Rounded.Menu) {
                        onIconClick()
                    }
                    else {
                        openMenu(coroutineScope, scaffoldState)
                    }
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "navigation"
                )
            }
        }
    )
}

private fun openMenu(coroutineScope: CoroutineScope, scaffoldState: ScaffoldState) {
    coroutineScope.launch {
        scaffoldState.drawerState.open()
    }
}