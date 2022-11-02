package elfak.mosis.tourguide.ui.components

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.ui.navigation.Screen


val logoSize = 75.dp
val titleSize = 20.sp
val customTextSize = 40.sp
val iconSize = 45.dp
val padding = 15.dp


@Composable
fun CustomWelcomeScreenLogoComponent(text: String, navigateBack: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            val context = LocalContext.current
            //Back Arrow Button
            IconButton(
                onClick = navigateBack,
            )
                 {
                Icon(
                    Icons.Rounded.ArrowBack,
                    stringResource(id = R.string.back_arrow_button),
                    modifier = Modifier
                        .size(iconSize),
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

// NOTE: check later
//fun navigateBack(): () -> Unit{
// //navigate to WelcomeScreen always
//}