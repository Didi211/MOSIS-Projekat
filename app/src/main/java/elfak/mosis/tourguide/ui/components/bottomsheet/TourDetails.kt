package elfak.mosis.tourguide.ui.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import elfak.mosis.tourguide.domain.models.Place
import elfak.mosis.tourguide.domain.models.TourDetails
import elfak.mosis.tourguide.ui.components.TransparentTextField
import elfak.mosis.tourguide.ui.components.buttons.CancelButton
import elfak.mosis.tourguide.ui.components.buttons.EditButton
import elfak.mosis.tourguide.ui.components.buttons.SaveButton
import elfak.mosis.tourguide.ui.screens.tourScreen.TourState
import elfak.mosis.tourguide.ui.theme.DragHandle
import elfak.mosis.tourguide.ui.theme.Typography


@Composable
fun TourDetails(
    state: TourState,
    tourDetails: TourDetails,
    onSave: () -> Unit = { },
    onEdit: () -> Unit = { },
    onCancel: () -> Unit = { },
) {
    when(state) {
        TourState.CREATING -> TourDetailsCreateMode(tourDetails, onSave, onCancel)
        TourState.VIEWING -> TourDetailsViewMode(tourDetails, onEdit)
        TourState.EDITING -> TourDetailsEditMode(tourDetails, onSave, onCancel)
    }
}

@Composable
fun TourDetailsCreateMode(tourDetails: TourDetails, onSave: () -> Unit, onCancel: () -> Unit) {
    TourDetailsContainer(tourDetails = tourDetails.clear()) {
        SaveButton(onSave)
        Spacer(Modifier.width(10.dp))
        CancelButton(onCancel)
    }
}
@Composable
fun TourDetailsViewMode(tourDetails: TourDetails, onEdit: () -> Unit) {
    TourDetailsContainer(tourDetails = tourDetails, enabledInputs = false) {
        EditButton(onEdit)
    }
}
@Composable
fun TourDetailsEditMode(tourDetails: TourDetails, onSave: () -> Unit, onCancel: () -> Unit) {
    TourDetailsContainer(tourDetails = tourDetails) {
        SaveButton(onSave)
        Spacer(Modifier.width(10.dp))
        CancelButton(onCancel)
    }
}

@Composable
fun TourDetailsContainer(
    tourDetails: TourDetails,
    enabledInputs: Boolean = true,
    buttons: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(top = 5.dp, start = 15.dp, end = 15.dp)
            .wrapContentSize()
    ) {
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
                        .height(70.dp)
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 0.dp),
                    text = tourDetails.title,
                    onTextChanged = tourDetails.onTitleChanged,
                    textStyle = Typography.h1,
                    enabled = enabledInputs
                )
            }
            // summary
            InputRowContainer {
                TransparentTextField(
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 0.dp),
                    text = tourDetails.summary,
                    onTextChanged = tourDetails.onSummaryChanged,
                    textStyle = Typography.body1,
                    enabled = enabledInputs,
                    singleLine = false
                )
            }
            // inputs
            Column(
                modifier = Modifier.padding(top = 15.dp)
            ) {
                InputRowContainer {
                    // Start Location
                    Text("From:", color = MaterialTheme.colors.primary)
                    TransparentTextField(
                        modifier = Modifier.widthIn(max = 280.dp),
                        text = tourDetails.startLocation.address,
                        enabled = enabledInputs,
                        onTextChanged = {
                            tourDetails.onStartLocationChanged(Place("",it, LatLng(0.0,0.0)))
                        },
                    )
                }
                InputRowContainer {
                    // End Location
                    Text("To:", color = MaterialTheme.colors.primary)
                    TransparentTextField(
                        modifier = Modifier.widthIn(max = 280.dp),
                        text = tourDetails.endLocation.address,
                        enabled = enabledInputs,
                        onTextChanged = {
                            tourDetails.onEndLocationChanged(Place("",it, LatLng(0.0,0.0)))
                        },
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if(tourDetails.distance != "") {
                        TextWithIcon(text = tourDetails.distance, icon = Icons.Filled.Hiking)
                        Spacer(Modifier.width(10.dp))
                    }
                    if(tourDetails.time != "") {
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



@Composable
fun ButtonRowContainer(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .padding(top = 15.dp, bottom = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,

    ) {
        content()
    }
}

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