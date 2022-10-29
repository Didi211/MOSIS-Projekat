package elfak.mosis.tourguide.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import elfak.mosis.tourguide.R

@Composable
fun LogoImage(size: Dp) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = stringResource(id = R.string.logo_description),
        contentScale = ContentScale.FillHeight,
        modifier = Modifier.size(size)
    )         
}