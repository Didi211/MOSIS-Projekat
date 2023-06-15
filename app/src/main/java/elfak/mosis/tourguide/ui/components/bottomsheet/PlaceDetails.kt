package elfak.mosis.tourguide.ui.components.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gowtham.ratingbar.RatingBar
import elfak.mosis.tourguide.data.models.PlaceDetails
import elfak.mosis.tourguide.domain.LocationType
import elfak.mosis.tourguide.domain.models.Place
import elfak.mosis.tourguide.ui.components.buttons.AddToTourButton
import elfak.mosis.tourguide.ui.components.buttons.ButtonRowContainer
import elfak.mosis.tourguide.ui.components.buttons.CancelButton
import elfak.mosis.tourguide.ui.components.dialogs.ChooseLocationTypeDialog

@Composable
fun PlaceDetails(
    placeDetails: PlaceDetails,
    onCancel: () -> Unit = { },
    onAddToTour: (Place, LocationType) -> Unit = {_,_ -> }
) {
    PlaceDetailsContainer(placeDetails, onCancel, onAddToTour)
}

@Composable
fun PlaceDetailsContainer(
    placeDetails: PlaceDetails,
    onCancel: () -> Unit = { },
    onAddToTour: (Place, LocationType) -> Unit = {_,_ ->  }
) {
    Column(
        modifier = Modifier
            .padding(top = 5.dp, start = 15.dp, end = 15.dp)
            .wrapContentSize()
    ) {
        // HandleBar
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Handlebar()
            Spacer(modifier = Modifier.height(5.dp))
        }

        // Place details
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            if (placeDetails.name != null) {
                Text(
                    modifier = Modifier.padding(vertical = 15.dp),
                    text = placeDetails.name,
                    style = MaterialTheme.typography.h1
                )
            }

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (placeDetails.address != null) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.height(25.dp),
                    )
                    Text(
                        text = placeDetails.address,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            // Type
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(placeDetails.iconUrl != null) {
                    AsyncImage(
                        modifier = Modifier.height(25.dp),
                        model = placeDetails.iconUrl,
                        contentDescription = "${placeDetails.name}'s icon"
                    )
                    Spacer(Modifier.width(5.dp))
                }
                if (placeDetails.type != null) {
                    Text(
                        text = placeDetails.type,
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            // Rating
            if(placeDetails.rating != null) {
                Row(Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    RatingBar(
                        value = placeDetails.rating.toFloat(),
                        onValueChange = { },
                        onRatingChanged = { }
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(text = "(${placeDetails.rating})")
                }
            }
        }
        ButtonRowContainer {
            var openDialog by remember { mutableStateOf(false) }

            if (openDialog) {
                ChooseLocationTypeDialog(
                    onDismiss = { openDialog = false },
                    onButtonClick = { type ->
                        onAddToTour(Place(address = placeDetails.address!!,id = placeDetails.id),type)
                        openDialog = false
                    }
                )
            }
            AddToTourButton(onClick = { openDialog = true
            })
            Spacer(Modifier.width(10.dp))
            CancelButton(onCancel)
        }
    }
}

