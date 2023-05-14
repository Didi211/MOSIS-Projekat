package elfak.mosis.tourguide.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R

@Composable
fun ButtonComponent(text: String, width: Dp, onClick: () -> Unit) {
    Button(
        modifier = Modifier.width(width).height(50.dp),
        shape = RoundedCornerShape(15.dp),
        onClick = onClick
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.button,

        )
    }
}

@Composable
fun SaveButton(onClick: () -> Unit = { }) {
    Button(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(30.dp),
        contentPadding = PaddingValues(10.dp),
        onClick = { }
    ) {
        Icon(
            Icons.Filled.Check,
            stringResource(id = R.string.check),
            modifier = Modifier
                .size(30.dp),
            tint = MaterialTheme.colors.onPrimary
        )
    }
}
@Composable
fun CancelButton() {

}
@Composable
fun EditButton() {

}

