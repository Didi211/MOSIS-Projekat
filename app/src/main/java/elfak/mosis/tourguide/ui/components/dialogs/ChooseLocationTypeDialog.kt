package elfak.mosis.tourguide.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import elfak.mosis.tourguide.domain.LocationType
import elfak.mosis.tourguide.ui.components.buttons.ButtonRowContainer
import elfak.mosis.tourguide.ui.components.buttons.CancelButton
import elfak.mosis.tourguide.ui.components.buttons.SaveButton

@Composable
fun ChooseLocationTypeDialog(
    onDismiss: () -> Unit,
    onButtonClick: (LocationType) -> Unit = { }
) {
    var selectedLocationType by remember { mutableStateOf<LocationType>(LocationType.Origin) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .wrapContentHeight()
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Heading
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = "Set location as: ",
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primary
            )
            Column(
                Modifier
                    .wrapContentHeight()
                    .padding(top = 5.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Location Types
                for (type in LocationType.values()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp)
                            .clickable { selectedLocationType = type },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(0.7f),
                            text = type.description,
                            style = MaterialTheme.typography.body1
                        )
                        RadioButton(
                            selected = selectedLocationType.name == type.name,
                            onClick = { selectedLocationType = type },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colors.primary
                            )
                        )
                    }
                }
                ButtonRowContainer {
                    SaveButton {
                        onButtonClick(selectedLocationType)
                    }
                    Spacer(Modifier.width(10.dp))
                    CancelButton(onDismiss)
                }

            }
        }
    }
}