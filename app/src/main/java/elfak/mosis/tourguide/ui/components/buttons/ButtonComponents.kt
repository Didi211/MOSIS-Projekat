package elfak.mosis.tourguide.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import elfak.mosis.tourguide.R

@Composable
fun ButtonComponent(text: String, width: Dp, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .width(width)
            .height(50.dp),
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
fun SaveButton(
    modifier: Modifier = Modifier.width(100.dp),
    paddingValues: PaddingValues = PaddingValues(10.dp),
    onClick: () -> Unit = { }
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        contentPadding = paddingValues,
        onClick = onClick
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
fun AddToTourButton(onClick: () -> Unit = { }) {
    Button(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(30.dp),
        contentPadding = PaddingValues(top = 10.dp, start = 4.dp, end = 10.dp, bottom = 10.dp),
        onClick = onClick
    ) {
        Icon(
            Icons.Filled.AddLocation,
            stringResource(id = R.string.add),
            modifier = Modifier
                .size(30.dp),
            tint = MaterialTheme.colors.onPrimary
        )
        Text(stringResource(id = R.string.add))
    }
}
@Composable
fun CancelButton(onClick: () -> Unit = { }) {
    Button(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(30.dp),
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
        onClick = onClick
    ) {
        Icon(
            Icons.Filled.Close,
            stringResource(id = R.string.cancel_icon_description),
            modifier = Modifier
                .size(30.dp),
            tint = MaterialTheme.colors.onPrimary
        )
    }
}
@Composable
fun EditButton(onClick: () -> Unit = { }) {
    Button(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(30.dp),
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
        onClick = onClick
    ) {
        Icon(
            Icons.Filled.Edit,
            stringResource(id = R.string.edit),
            modifier = Modifier
                .size(30.dp),
            tint = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
fun CircleButton(
    icon: ImageVector,
    backgroundColor: Color = MaterialTheme.colors.primary,
    iconColor: Color = MaterialTheme.colors.onPrimary,
    onClick: () -> Unit = { }
) {
    Button(
      modifier = Modifier.size(40.dp),
      shape = CircleShape,
      colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
      onClick = onClick,
    ) {
     Icon(
         icon,
         contentDescription = null,
         modifier = Modifier.requiredSize(30.dp),
         tint = iconColor
     )
    }
}