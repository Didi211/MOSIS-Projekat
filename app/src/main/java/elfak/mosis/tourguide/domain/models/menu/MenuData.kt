package elfak.mosis.tourguide.domain.models.menu

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuData(
    val menuIcon: ImageVector,
    val name: String,
    val onClick: () -> Unit
)
