@file:OptIn(ExperimentalMaterialApi::class)

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
    onIconClick: () -> Unit = {  }
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
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
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

@Composable
fun TourGuideTopAppBar(
    title: String,
    icon: ImageVector = Icons.Rounded.Menu,
    coroutineScope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    onIconClick: () -> Unit = {  }
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
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
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

