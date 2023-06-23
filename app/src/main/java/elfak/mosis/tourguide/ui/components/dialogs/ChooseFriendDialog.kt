package elfak.mosis.tourguide.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.domain.models.tour.TourSelectionDisplay
import elfak.mosis.tourguide.ui.components.buttons.ButtonRowContainer
import elfak.mosis.tourguide.ui.components.buttons.CancelButton
import elfak.mosis.tourguide.ui.components.buttons.SaveButton
import elfak.mosis.tourguide.ui.components.images.UserAvatar
import es.dmoral.toasty.Toasty

@Composable
fun ChooseFriendDialog(users: List<UserModel>, onDismiss: () -> Unit, onOkButtonClick: (String) -> Unit) {
    var selectedUserId by remember { mutableStateOf<String>("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .wrapContentHeight()
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(id = R.string.choose_friend) + ":",
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primary
            )
            Column(
                Modifier
                    .heightIn(max = 350.dp)
                    .padding(top = 5.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (users.isEmpty()) {
                    Column(
                        Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_friends),
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.primary
                        )
                    }
                    return@Dialog
                }
                // users list
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(users) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedUserId = user.id },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(Modifier.fillMaxWidth(0.8f),horizontalArrangement = Arrangement.Start) {
                                Column(
                                    Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    UserAvatar(user.thumbnailPhotoUrl, photoSize = 40.dp)
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.fillMaxWidth()) {
                                    Text(
                                        text = user.fullname,
                                        style = MaterialTheme.typography.body1
                                    )
                                    Text(
                                        text = "@${user.username}",
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            }
                            RadioButton(
                                selected = selectedUserId == user.id,
                                onClick = { selectedUserId = user.id },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colors.primary
                                )
                            )
                        }
                        Divider()
                    }
                }

                ButtonRowContainer {
                    val context = LocalContext.current
                    SaveButton {
                        if (selectedUserId.isBlank()) {
                            Toasty.error(context, "Tour not selected.").show()
                            return@SaveButton
                        }
                        onOkButtonClick(selectedUserId)
                        onDismiss()
                    }
                    Spacer(Modifier.width(10.dp))
                    CancelButton(onDismiss)
                }
            }

        }
    }
}