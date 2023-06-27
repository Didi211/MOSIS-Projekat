package elfak.mosis.tourguide.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.components.BasicInputComponent
import elfak.mosis.tourguide.ui.components.buttons.ButtonRowContainer
import elfak.mosis.tourguide.ui.components.buttons.CancelButton
import elfak.mosis.tourguide.ui.components.buttons.SaveButton
import es.dmoral.toasty.Toasty

@Composable
fun SetRadiusDialog(onDismiss: () -> Unit, onOkButtonClick: (String) -> Unit, validateRadius: (String) -> Boolean) {
    var radius by remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .wrapContentHeight()
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(id = R.string.set_radius) + ":",
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primary
            )
            Column(
                Modifier
                    .heightIn(max = 350.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    Modifier
                        .wrapContentHeight()
                        .padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BasicInputComponent(
                        text = radius,
                        onTextChanged = { radius = it },
                        label = stringResource(id = R.string.search_in_radius),
                        placeholder = "Meters",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Phone,
                        )
                    )
                }


                ButtonRowContainer {
                    SaveButton {
                        if (!validateRadius(radius))
                            return@SaveButton
                        onOkButtonClick(radius)
                        onDismiss()
                    }
                    Spacer(Modifier.width(10.dp))
                    CancelButton(onDismiss)
                }
            }

        }
    }
}