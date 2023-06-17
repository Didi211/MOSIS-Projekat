package elfak.mosis.tourguide.ui.components.images

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import elfak.mosis.tourguide.R

@Composable
fun NoFriendsImage(imageTitle: String, additionDescription: String? = null) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.friend_requests),
                contentDescription = stringResource(id = R.string.no_friends_image),
            )
            Column(
                Modifier.padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    imageTitle,
                    style = MaterialTheme.typography.h2,
                    color = MaterialTheme.colors.primary,
                    fontSize = 35.sp
                )
                additionDescription?.let {
                    Text(
                        it,
                        modifier = Modifier.padding(top = 5.dp, start = 30.dp, end = 30.dp),
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Left
                    )
                }
            }
        }
    }
}