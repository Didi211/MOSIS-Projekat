@file:OptIn(ExperimentalAnimationApi::class)

package elfak.mosis.tourguide.ui.screens.notificationScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.data.models.notification.TourNotificationType
import elfak.mosis.tourguide.data.respository.NotificationResponseType
import elfak.mosis.tourguide.domain.models.notification.NotificationCard
import elfak.mosis.tourguide.ui.components.ToastHandler
import elfak.mosis.tourguide.ui.components.icons.CancelIcon
import elfak.mosis.tourguide.ui.components.images.NoNotificationsImage
import elfak.mosis.tourguide.ui.components.images.UserAvatar
import elfak.mosis.tourguide.ui.components.scaffold.MenuViewModel
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import elfak.mosis.tourguide.ui.navigation.NavigationArgument
import elfak.mosis.tourguide.ui.navigation.Screen
import elfak.mosis.tourguide.ui.screens.friendsScreen.CardButton

@Composable
fun NotificationScreen(
    viewModel: NotificationScreenViewModel,
    navController: NavController,
    navigate: (String) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val menuViewModel = hiltViewModel<MenuViewModel>()

    ToastHandler(
        toastData = viewModel.uiState.toastData,
        clearErrorMessage = viewModel::clearErrorMessage,
        clearSuccessMessage = viewModel::clearSuccessMessage
    )

    Scaffold(
        scaffoldState = scaffoldState,
        // top navigation bar with menu button
        topBar = {
            TourGuideTopAppBar(
                title = stringResource(id = R.string.notifications),
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState,
            )
        },
        // menu content
        drawerContent = {
            TourGuideNavigationDrawer(
                navController = navController,
                menuViewModel = menuViewModel
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AnimatedContent(targetState = viewModel.uiState.notifications.isNotEmpty()) { state ->
                when (state) {
                    true -> {
                        NotificationCardsContainer(
                            notifications = viewModel.uiState.notifications,
                            onNavigate = navigate,
                            onDelete = viewModel::deleteNotification,
                            onAccept = viewModel::acceptTourInvite,
                            onDecline = viewModel::declineTourInvite
                        )
                    }
                    false -> {
                        NoNotificationsImage(imageTitle = stringResource(id = R.string.no_new_notifications))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCardsContainer(
    notifications: List<NotificationCard>,
    onNavigate: (path: String) -> Unit = { },
    onDelete: (notificationId: String) -> Unit = { },
    onAccept: (notificationId: String) -> Unit = { },
    onDecline: (notificationId: String) -> Unit = { },
) {
    Box {
        LazyColumn(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(15.dp)
        ) {
            items(notifications) { notification ->
                NotificationCard(
                    notification = notification,
                    onClick = {
                        if (notification.tourId != null) {
                            onNavigate(Screen.TourScreen.withOptionalArgs(
                                NavigationArgument(NotificationCard::tourId.name, notification.tourId)
                            ))
                        }
                    },
                    onDelete = { onDelete(notification.id) },
                    onAccept = onAccept,
                    onDecline = onDecline
                )
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationCard,
    onClick: () -> Unit,
    onDelete: (notificationId: String) -> Unit,
    onAccept: (notificationId: String) -> Unit,
    onDecline: (notificationId: String) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = 5.dp,
        modifier = Modifier.height(150.dp)
    ) {
        val cardModifier = Modifier.wrapContentWidth()
        val canNavigate = notification.tourId != null && notification.status == NotificationResponseType.Accepted
        Card(
            modifier = if (canNavigate) cardModifier.clickable { onClick() } else cardModifier,
            shape = RoundedCornerShape(20.dp),
            elevation = 5.dp,
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .padding(bottom = 0.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxSize()
                        .height(IntrinsicSize.Min)
                ) {
                    UserAvatar(notification.photoUrl)
                    Spacer(Modifier.width(5.dp))
                    Column {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            // notification info
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(top = 10.dp),
                            ) {
                                Text(
                                    text = notification.message,
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.primary
                                )
                            }

                            // delete icon
                            Box {
                                Column(
                                    Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    CancelIcon { onDelete(notification.id) }
                                }
                            }
                        }
                        val modifier = Modifier.fillMaxSize()
                        val verticalAlignment = Alignment.CenterVertically
                        val horizontalArrangement = Arrangement.End
                        Row(
                            modifier = Modifier
                                .padding(top = 5.dp, end = 8.dp)
                                .fillMaxHeight()
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            if (notification.status != NotificationResponseType.Waiting) {
                                Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                                    Text(
                                        text = notification.status.toString(),
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.primary
                                    )
                                }

                            }
                            else {
                                if (notification.tourNotificationType == TourNotificationType.Invite) {
                                    Row(modifier, horizontalArrangement, verticalAlignment) {
                                        CardButton(
                                            text = stringResource(R.string.accept),
                                            icon = Icons.Rounded.Check,
                                            backgroundColor = MaterialTheme.colors.primary
                                        ) {
                                            onAccept(notification.id)
                                        }
                                        Spacer(Modifier.width(10.dp))
                                        CardButton(
                                            text = stringResource(R.string.cancel_icon_description),
                                            icon = Icons.Rounded.Close,
                                            backgroundColor = MaterialTheme.colors.error
                                        ) {
                                            onDecline(notification.id)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
