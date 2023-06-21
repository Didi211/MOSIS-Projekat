package elfak.mosis.tourguide.ui.components.bottomsheet

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.data.models.PlaceAutocompleteResult
import elfak.mosis.tourguide.domain.models.tour.LocationType
import elfak.mosis.tourguide.domain.models.Place
import elfak.mosis.tourguide.domain.models.tour.TourDetails
import elfak.mosis.tourguide.ui.components.TransparentTextField
import elfak.mosis.tourguide.ui.components.buttons.ButtonRowContainer
import elfak.mosis.tourguide.ui.components.buttons.CancelButton
import elfak.mosis.tourguide.ui.components.buttons.EditButton
import elfak.mosis.tourguide.ui.components.buttons.SaveButton
import elfak.mosis.tourguide.ui.components.dialogs.BlockTextDialog
import elfak.mosis.tourguide.ui.components.dialogs.SearchLocationDialog
import elfak.mosis.tourguide.ui.components.icons.CancelIcon
import elfak.mosis.tourguide.ui.screens.tourScreen.TourState
import elfak.mosis.tourguide.ui.theme.Typography


@Composable
fun TourDetails(
    state: TourState,
    tourDetails: TourDetails,
    onSave: () -> Unit = { },
    onEdit: () -> Unit = { },
    onCancel: () -> Unit = { },
    placesList: MutableList<PlaceAutocompleteResult>,
    searchForPlaces: (String) -> Unit = { },
) {
    when(state) {
        TourState.CREATING -> TourDetailsEditMode(tourDetails, onSave, onCancel, placesList, searchForPlaces)
        TourState.VIEWING -> TourDetailsViewMode(tourDetails, onEdit)
        TourState.EDITING -> TourDetailsEditMode(tourDetails, onSave, onCancel, placesList, searchForPlaces)
    }
}

@Composable
fun TourDetailsEditMode(
    tourDetails: TourDetails,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    placesList: MutableList<PlaceAutocompleteResult>,
    searchForPlaces: (String) -> Unit = { },
) {
    var locationInput by remember { mutableStateOf(LocationType.Origin.name) }
    var openDialog by remember { mutableStateOf(false) }
    var searchValue by remember { mutableStateOf("") }

    var originSet by remember {
        mutableStateOf( tourDetails.origin.id.isNotBlank() )
    }
    var destinationSet by remember {
        mutableStateOf( tourDetails.destination.id.isNotBlank() )
    }

    if (openDialog) {
        SearchLocationDialog(
            onDismiss = {
                openDialog = false
                placesList.clear()
            },
            onPlaceClick = { place ->
                if (locationInput == LocationType.Origin.name) {
                    tourDetails.onOriginChanged(Place(place.placeId, place.address))
                    originSet = true
                }
                else if (locationInput == LocationType.Destination.name) {
                    tourDetails.onDestinationChanged(Place(place.placeId, place.address))
                    destinationSet = true
                }
                if(originSet && destinationSet) {
                    tourDetails.onBothLocationsSet(true)
                }
                openDialog = false
                placesList.clear()
            },
            placesList = placesList,
            searchValue = searchValue,
            searchForPlaces = searchForPlaces
        )
    }
    TourDetailsContainer(
        tourState = TourState.EDITING,
        tourDetails = tourDetails,
        chooseLocation = { type, searchText ->
            locationInput = type.name
            searchValue = searchText
            openDialog = true
        }) {
        SaveButton(onClick = onSave)
        Spacer(Modifier.width(10.dp))
        CancelButton(onCancel)
    }
}
@Composable
fun TourDetailsViewMode(tourDetails: TourDetails, onEdit: () -> Unit) {
    TourDetailsContainer(tourState = TourState.VIEWING, tourDetails = tourDetails, enabledInputs = false) {
        EditButton(onEdit)
    }
}

@Composable
fun TourDetailsContainer(
    tourState: TourState,
    tourDetails: TourDetails,
    enabledInputs: Boolean = true,
    chooseLocation: (LocationType, String) -> Unit = { _, _ ->  },
    buttons: @Composable () -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        BlockTextDialog(
            text = tourDetails.summary,
            onTextChanged = tourDetails.onSummaryChanged,
            label = stringResource(id = R.string.summary) + ":",
            enabled = tourState != TourState.VIEWING,
            onDismiss = {
                openDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .padding(top = 5.dp, start = 15.dp, end = 15.dp)
            .wrapContentSize()
    ) {
        // Handlebar
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Handlebar()
            Spacer(modifier = Modifier.height(5.dp))
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            InputRowContainer {
                TransparentTextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 0.dp),
                    text = tourDetails.title,
                    placeholder = stringResource(id = R.string.title),
                    onTextChanged = tourDetails.onTitleChanged,
                    textStyle = Typography.h1,
                    enabled = enabledInputs,
                    singleLine = false,
                    keyboardOptions = KeyboardOptions().copy(imeAction = ImeAction.Next, capitalization = KeyboardCapitalization.Sentences)
                )
            }
            // Summary
            InputRowContainer {
                TransparentTextField(
                    modifier = Modifier
                        .height(70.dp)
                        .border(
                            width = 1.dp,
                            color = if (tourState != TourState.VIEWING) MaterialTheme.colors.primary else Color.Transparent,
                            shape = RoundedCornerShape(13.dp)
                        )
                        .padding(top = 0.dp, bottom = 0.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .fillMaxWidth()
                        .clickable {
                            openDialog = true
                        },
                    text = tourDetails.summary,
                    placeholder = stringResource(id = R.string.summary),
                    onTextChanged = tourDetails.onSummaryChanged,
                    textStyle = Typography.body1,
                    enabled = false,
                    singleLine = false,
                )
            }
            // Inputs
            Column(
                modifier = Modifier.padding(top = 15.dp)
            ) {
                InputRowContainer {
                    // Origin
                    Text("From:", color = MaterialTheme.colors.primary)
                    Column {
                        val modifier = if (enabledInputs) {
                            Modifier.clickable {
                                chooseLocation(LocationType.Origin, tourDetails.origin.address)
                            }
                        } else Modifier
                        TransparentTextField(
                            modifier = modifier
                                .widthIn(max = 280.dp),
                            text = tourDetails.origin.address,
                            placeholder = stringResource(id = R.string.search_holder2),
                            enabled = false,
                            onTextChanged = {
                                tourDetails.onOriginChanged(Place("",it, LatLng(0.0,0.0)))
                            },
                            trailingIcon = {
                                if(tourState != TourState.VIEWING && tourDetails.origin.id.isNotBlank()) {
                                    CancelIcon(onClick = {
                                        onCancelIconClick(
                                            tourDetails = tourDetails,
                                            clearInput = {
                                                tourDetails.onOriginChanged(Place())
                                            }
                                        )
                                    })
                                }
                            }
                        )
                        Divider(
                            color = Color.DarkGray,
                            modifier = Modifier.widthIn(max = 280.dp)
                        )
                    }

                }
                InputRowContainer {
                    // Destination
                    Text("To:", color = MaterialTheme.colors.primary)
                    Column {
                        val modifier = if (enabledInputs) {
                            Modifier.clickable {
                                chooseLocation(LocationType.Destination, tourDetails.destination.address)
                            }
                        } else Modifier
                        TransparentTextField(
                            modifier = modifier
                                .widthIn(max = 280.dp),
                            text = tourDetails.destination.address,
                            placeholder = stringResource(id = R.string.search_holder),
                            enabled = false,
                            onTextChanged = {
                                tourDetails.onDestinationChanged(Place("",it, LatLng(0.0,0.0)))
                            },
                            trailingIcon = {
                                if(tourState != TourState.VIEWING && tourDetails.destination.id.isNotBlank()) {
                                    CancelIcon(onClick = {
                                        onCancelIconClick(
                                            tourDetails = tourDetails,
                                            clearInput = {
                                                tourDetails.onDestinationChanged(Place())
                                            }
                                        )
                                    })
                                }
                            }
                        )
                        Divider(
                            color = Color.DarkGray,
                            modifier = Modifier.widthIn(max = 280.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if(tourDetails.distance.isNotBlank()) {
                        TextWithIcon(text = tourDetails.distance, icon = Icons.Filled.Hiking)
                        Spacer(Modifier.width(10.dp))
                    }
                    if(tourDetails.time.isNotBlank()) {
                        TextWithIcon(text = tourDetails.time, icon = Icons.Filled.Schedule)
                    }
                }
            }
        }
        ButtonRowContainer {
            buttons()
        }
    }
}



private fun onCancelIconClick(tourDetails: TourDetails, clearInput: () -> Unit) {
    clearInput()
    tourDetails.onBothLocationsSet(false)
    tourDetails.onTimeChanged("")
    tourDetails.onDistanceChanged("")
}




@Composable
fun TextWithIcon(text: String, icon: ImageVector) {
    val color = MaterialTheme.colors.primary
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color
        )
        Text(text = text, color = color)
    }
}

@Composable
fun InputRowContainer(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        content()
    }
}




