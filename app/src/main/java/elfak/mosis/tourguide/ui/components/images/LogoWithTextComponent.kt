package elfak.mosis.tourguide.ui.components.images

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import elfak.mosis.tourguide.R



val backIconSize = 45.dp
val padding = 15.dp


@Composable
fun LogoWithTextComponent(
    text: String,
    titleSize: TextUnit = 20.sp,
    logoSize: Dp = 75.dp,
    customTextSize: TextUnit = 40.sp,
    navigateBack: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(padding)
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            //Back Arrow Button
            IconButton(
                onClick = navigateBack,
            )
                 {
                Icon(
                    Icons.Rounded.ArrowBack,
                    stringResource(id = R.string.back_arrow_button),
                    modifier = Modifier
                        .size(backIconSize),
                    tint = MaterialTheme.colors.primary
                )
            }

        }
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //LogoComponent
            LogoComponent(logoSize, titleSize)
            //Custom text
            Text(
                text = text,
                style = MaterialTheme.typography.h1,
                fontSize = customTextSize,
                color = MaterialTheme.colors.primary
            )
        }
    }

}
