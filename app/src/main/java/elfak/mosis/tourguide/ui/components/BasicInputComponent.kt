package elfak.mosis.tourguide.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.ui.InputTypes


@Composable
fun BasicInputComponent(
    text: String = "",
    onTextChanged: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions,
    inputType: InputTypes
) {
//    var text by remember { mutableStateOf(TextFieldValue("")) }
    val colors = MaterialTheme.colors
    val inputColors = TextFieldDefaults.outlinedTextFieldColors(
        backgroundColor = colors.secondary,
        textColor = colors.onSecondary,
        cursorColor = colors.onSecondary,
        focusedBorderColor = colors.secondaryVariant,
        unfocusedBorderColor = Color.Transparent,
        unfocusedLabelColor = colors.onPrimary,
        focusedLabelColor = colors.primary,
    )

    Column {

        OutlinedTextField(
            value = text,
            label = { Text(
                text = label,
                style = MaterialTheme.typography.body1,
            ) },
            onValueChange = onTextChanged,
            textStyle = MaterialTheme.typography.body1,
            colors = inputColors,
            shape = RoundedCornerShape(13.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            visualTransformation = if (inputType == InputTypes.Password) PasswordVisualTransformation() else VisualTransformation.None,
        )

    }
}
