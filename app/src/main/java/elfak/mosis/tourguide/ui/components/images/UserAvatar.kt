package elfak.mosis.tourguide.ui.components.images

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import elfak.mosis.tourguide.R

@Composable
fun UserAvatar(photoUrl: String? = null, photoSize: Dp = 80.dp) {
    Column(
        modifier = Modifier
            .clip(CircleShape)
            .size(photoSize)
            .border(3.dp, MaterialTheme.colors.primary, CircleShape)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val contentDescription = stringResource(id = R.string.user_photo)
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop,
                model = photoUrl,
                contentDescription = contentDescription
            )
        }
        else {
            Icon(
                modifier = Modifier.fillMaxSize().padding(5.dp),
                imageVector = Icons.Filled.Person,
                contentDescription = contentDescription,
                tint = Color.LightGray
            )
        }
    }
}