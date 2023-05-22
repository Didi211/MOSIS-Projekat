package elfak.mosis.tourguide.ui.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.ui.theme.DragHandle

@Composable
fun Handlebar() {
    Spacer(
        modifier = Modifier
            .height(7.dp)
            .width(50.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(DragHandle),
    )
}