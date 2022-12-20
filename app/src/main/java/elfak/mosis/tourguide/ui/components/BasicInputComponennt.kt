package elfak.mosis.tourguide.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.ui.InputTypes


@Composable
fun BasicInputComponent(label: String, keyboardOptions: KeyboardOptions, inputType: InputTypes): Unit {

    val colors = MaterialTheme.colors
    var textState = remember { mutableStateOf(TextFieldValue()) }

    val inputColors = TextFieldDefaults.textFieldColors(
        backgroundColor = colors.secondary,
        textColor = colors.onSecondary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = colors.onSecondary,
    )

    Column() {
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(start = 5.dp)

        )
        TextField(
            value = textState.value,
            label = null,
            onValueChange = { textState.value = it },
            textStyle = MaterialTheme.typography.body1,
            colors = inputColors,
            shape = RoundedCornerShape(13.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            visualTransformation = if (inputType == InputTypes.Password) PasswordVisualTransformation() else VisualTransformation.None,
        )

    }
}
