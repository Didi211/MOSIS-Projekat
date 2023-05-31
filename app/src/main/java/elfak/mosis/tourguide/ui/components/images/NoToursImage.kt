package elfak.mosis.tourguide.ui.components.images

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import elfak.mosis.tourguide.R

@Composable
fun NoToursImage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.tour),
            contentDescription = stringResource(id = R.string.no_tours_image_description),
        )
        Text(
            stringResource(id = R.string.no_tours_created),
            style = MaterialTheme.typography.h2,
            color = MaterialTheme.colors.primary,
            fontSize = 35.sp
        )
    }
}