package elfak.mosis.tourguide.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import elfak.mosis.tourguide.R

@Composable
fun LogoComponent(logoSize: Dp, titleSize: TextUnit = MaterialTheme.typography.h1.fontSize) {

    Column(
//        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoImage(size = logoSize)
        Text(
            stringResource(id = R.string.home_screen_title).uppercase(),
            style = MaterialTheme.typography.h1,
            color = MaterialTheme.colors.primary,
            fontSize = titleSize
        )
    }
}


