package elfak.mosis.tourguide.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.theme.Typography


@Composable
fun BasicInputComponent(
    text: String = "",
    onTextChanged: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    inputType: InputTypes,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    inputColors: TextFieldColors = basicInputColors(),
    modifier: Modifier = Modifier
) {
    Column {
        OutlinedTextField(
            modifier = modifier,
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
            keyboardActions = keyboardActions
        )

    }
}

@Composable
fun TransparentTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    onTextChanged: (String) -> Unit,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    textStyle: TextStyle = Typography.body1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    inputType: InputTypes = InputTypes.Text,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    inputColors: TextFieldColors = transparentInputColors(),
    placeholder: String = "",
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        value = text,
        onValueChange = onTextChanged,
        textStyle = textStyle,
        colors = inputColors,
        shape = RoundedCornerShape(13.dp),
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        visualTransformation = if (inputType == InputTypes.Password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardActions = keyboardActions,
        enabled = enabled,
        placeholder = { Text(placeholder) },
        trailingIcon = trailingIcon
    )
}

@Composable
fun basicInputColors(): TextFieldColors {
    val colors = MaterialTheme.colors
    return TextFieldDefaults.outlinedTextFieldColors(
        backgroundColor = colors.secondary,
        textColor = colors.onSecondary,
        cursorColor = colors.onSecondary,
        focusedBorderColor = colors.secondaryVariant,
        unfocusedBorderColor = Color.Transparent,
        unfocusedLabelColor = colors.onPrimary,
        focusedLabelColor = colors.primary,
    )
}

@Composable
fun transparentInputColors(): TextFieldColors {
    val colors = MaterialTheme.colors
    return TextFieldDefaults.outlinedTextFieldColors(
        backgroundColor = Color.Transparent,
        textColor = colors.primary,
        cursorColor = colors.primary,
        focusedBorderColor = colors.primary,
        unfocusedBorderColor = colors.primary,
        unfocusedLabelColor = colors.onPrimary,
        focusedLabelColor = colors.primary,
        disabledBorderColor = Color.Transparent,
        disabledTextColor = colors.primary
    )
}
