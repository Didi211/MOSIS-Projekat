package elfak.mosis.tourguide.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.theme.DragHandle
import elfak.mosis.tourguide.ui.theme.Typography


@Composable
fun PlaceDetails() {
    // TODO - add info about searched place or place with point of interest
    // Needed data: name, category, review...
    // Buttons: Cancel (X), Add to tour: as startLocation, endLocation, stopByLocation
}

@Composable
fun TourDetails(
    title: String,
    startLocation: String,
    endLocation: String,
    distance: String = "3.5km",
    time: String = "35min",
    onTitleChanged: (String) -> Unit,
    onStartLocationChanged: (String) -> Unit,
    onEndLocationChanged: (String) -> Unit,
    onDistanceChanged: (String) -> Unit = { },
    onTimeChanged: (String) -> Unit = { }
) {
    Column(
        modifier = Modifier
            .padding(top = 5.dp, start = 15.dp, end = 15.dp)
            .heightIn(max = 350.dp)
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .height(7.dp)
                    .width(50.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DragHandle),
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TransparentTextField(
                    modifier = Modifier.height(70.dp).padding(top = 0.dp, bottom = 0.dp),
                    text = title,
                    onTextChanged = onTitleChanged,
                    textStyle = Typography.h1
                )

            }
            // summary

            // inputs
            Column(
                modifier = Modifier.padding(top = 35.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("From:", color = MaterialTheme.colors.primary)
                    TransparentTextField(
                        text = startLocation,
                        onTextChanged = onStartLocationChanged,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Text("To:", color = MaterialTheme.colors.primary)
                    TransparentTextField(
                        text = endLocation,
                        onTextChanged = onEndLocationChanged
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start

                ) {
                    TextWithIcon(text = distance, icon = Icons.Filled.Hiking)
                    Spacer(Modifier.width(10.dp))
                    TextWithIcon(text = time, icon = Icons.Filled.Schedule)
                }

            }
        }
        Row(
            modifier = Modifier.padding(top = 15.dp, bottom = 15.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            SaveButton()
        }
    }
}


//    Box(
//        modifier = Modifier
//            .padding(15.dp)
//            .heightIn(max = 400.dp)
//            .fillMaxSize(),
////            .wrapContentSize(),
//        contentAlignment = Alignment.TopStart,
//
//        ) {
//    }

//@Composable
//fun ShowTimePicker(context: Context) {
//    val calendar = Calendar.getInstance()
//    val hour = calendar[Calendar.HOUR_OF_DAY]
//    val minute = calendar[Calendar.MINUTE]
//    val time = remember { mutableStateOf("${hour}:${minute}")}
//    val timePickerDialog = TimePickerDialog(
//        context,
//        { _,hour: Int, minute: Int ->
//            time.value = "$hour:$minute"
//        }, hour, minute, false
//    )
//
//    Button(
//        onClick = { timePickerDialog.show() }
//    ) {
//      Text(text = time.value)
//    }
//}

@Composable
fun TextWithIcon(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Text(text = text)
    }
}

