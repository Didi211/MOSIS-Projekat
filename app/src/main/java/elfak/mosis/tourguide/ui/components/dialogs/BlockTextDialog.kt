package elfak.mosis.tourguide.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import elfak.mosis.tourguide.ui.components.TransparentTextField
import elfak.mosis.tourguide.ui.components.buttons.SaveButton
import elfak.mosis.tourguide.ui.theme.Typography

@Composable
fun BlockTextDialog(
    text: String = "",
    onTextChanged: (String) -> Unit = { },
    label: String = "",
    enabled: Boolean = false,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .wrapContentHeight()
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    Modifier.padding(start = 10.dp),
                    verticalArrangement = Arrangement.Bottom,
                ){
                    Text(
                        text = label,
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.body1
                    )
                }
                Column(Modifier.padding(top = 10.dp, end = 10.dp)) {
                    SaveButton(
                        modifier = Modifier
                            .width(70.dp)
                            .height(35.dp),
                        paddingValues = PaddingValues(5.dp),
                        onClick = onDismiss
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            TransparentTextField(
                modifier = Modifier
                    .heightIn(min = 50.dp,max = 250.dp)
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 0.dp)
                    .verticalScroll(rememberScrollState()),
                text = text,
                placeholder = "Nothing to show",
                onTextChanged = onTextChanged,
                textStyle = Typography.body1,
                enabled = enabled,
                singleLine = false,
            )
//            Column(
//                modifier = Modifier
//                    .wrapContentHeight()
//                    .fillMaxWidth()
//                    .padding(end = 10.dp),
//                horizontalAlignment = Alignment.End,
//            ) {
//                Spacer(Modifier.height(30.dp))
//                SaveButton(onClick = onDismiss)
//            }
        }
    }
}