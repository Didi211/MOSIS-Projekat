package elfak.mosis.tourguide.ui.components.menu

import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.ui.components.scaffold.MenuData

@Composable
fun Menu(
    expanded: Boolean,
    menuItems: List<MenuData>,
    onDismissRequest: () -> Unit,
    onIconClick: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        menuItems.forEach { item ->
            DropdownMenuItem(
                text = {
                    Text(text = item.name)
                },
                onClick = {
                    onIconClick()
                    item.onClick()
                },
                leadingIcon = {
                    Icon(
                        imageVector = item.menuIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colors.primary,
                    leadingIconColor = MaterialTheme.colors.primary
                )
            )
        }
    }
}