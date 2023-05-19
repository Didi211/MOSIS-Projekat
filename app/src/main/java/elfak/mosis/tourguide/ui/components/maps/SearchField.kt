package elfak.mosis.tourguide.ui.components.maps

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import elfak.mosis.tourguide.ui.InputTypes
import elfak.mosis.tourguide.ui.components.BasicInputComponent

@Composable
fun SearchField(
    onSearch: () -> Unit,
    text: String,
    onTextChanged: (String) -> Unit,
    label: String,
) {
    val focusManager = LocalFocusManager.current
    val context: Context = LocalContext.current


    BasicInputComponent(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        onTextChanged = onTextChanged,
        label = label,
        inputType = InputTypes.Text,
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = false,
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
            }
        ),
    )
}