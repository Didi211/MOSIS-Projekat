@file:OptIn(ExperimentalFoundationApi::class)

package elfak.mosis.tourguide.ui.components.images

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.models.Photo

@Composable
fun ProfileImage(
    photo: Photo,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
//            .align(Alignment.CenterHorizontally)
            .size(130.dp)
            .fillMaxWidth()
            .clip(CircleShape)
            .border(5.dp, MaterialTheme.colors.primary, CircleShape)
            .combinedClickable(
                interactionSource = MutableInteractionSource(),
                onClick = onClick,
                onLongClick = onLongClick,
                indication = LocalIndication.current,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        val hasPhoto = photo.hasPhoto && photo.uri != null
        if (!hasPhoto) {
            Icon(
                imageVector = Icons.Outlined.AddAPhoto,
                contentDescription = stringResource(id = R.string.add_profile_photo),
                tint = MaterialTheme.colors.primary,
                modifier = Modifier
                    .size(80.dp)
                    .scale(scaleX = -1f, scaleY = 1f)
            )
        }
        else {
            AsyncImage(
                model = photo.uri,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.user_photo)
            )
        }
    }
}