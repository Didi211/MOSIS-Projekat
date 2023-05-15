package elfak.mosis.tourguide.ui.components.maps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import elfak.mosis.tourguide.data.models.AutocompleteResult

@Composable
fun ListOfPlaces(
    placesList: List<AutocompleteResult>,
    onPlaceClick: (AutocompleteResult) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    AnimatedVisibility(
        visible = placesList.isNotEmpty(),
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(999f)
    ) {
        Surface(shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(250.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(12.dp)) {
                items(
//                    viewModel.locationAutofill
                    placesList
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clickable {
                                onPlaceClick(it)
                            }
                    ) {
                        Column {
                            Text(it.address)
                            Divider(
                                color = Color.DarkGray,
                                modifier = Modifier
                                    .height(1.dp)
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                            )

                        }
                    }
                }
            }
        }
    }
}