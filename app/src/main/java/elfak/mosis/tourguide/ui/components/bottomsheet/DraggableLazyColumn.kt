package elfak.mosis.tourguide.ui.components.bottomsheet

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.domain.models.Place
import elfak.mosis.tourguide.ui.components.icons.CancelIcon
import elfak.mosis.tourguide.ui.screens.tourScreen.TourState
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun DraggableLazyColumn(
    waypoints: List<Place>,
    tourState: TourState,
    onMove: (ItemPosition, ItemPosition) -> Unit,
    onRemoveFromList: (Place) -> Unit,
) {
    val state = rememberReorderableLazyListState(onMove = onMove)
    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ) {
        items(waypoints, { item -> item.id }) { waypoint ->
            ReorderableItem(reorderableState = state, key = waypoint.id) { dragging ->
                val elevation = animateDpAsState(if (dragging) 8.dp else 0.dp)
                Column(
                    modifier = Modifier
                        .shadow(elevation.value)
                        .background(MaterialTheme.colors.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val fraction = if (tourState != TourState.VIEWING) 0.9f else 1f
                        Row(
                            Modifier.fillMaxWidth(fraction),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${waypoints.indexOf(waypoint) + 1}.",
                                style = MaterialTheme.typography.body2,
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = waypoint.address, overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.body2,
                                maxLines = 1,
                            )
                        }
                        if (tourState != TourState.VIEWING) {
                            CancelIcon(
                                onClick = { onRemoveFromList(waypoint) }
                            )
                        }
                    }
                }
            }
        }
    }
}
