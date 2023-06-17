package elfak.mosis.tourguide.ui.screens.friendsScreen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PersonRemove
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.models.friends.FriendCard
import elfak.mosis.tourguide.ui.components.buttons.ButtonRowContainer
import elfak.mosis.tourguide.ui.components.buttons.CircleButton
import elfak.mosis.tourguide.ui.components.images.UserAvatar
import elfak.mosis.tourguide.ui.components.maps.SearchField
import elfak.mosis.tourguide.ui.components.menu.Menu
import elfak.mosis.tourguide.ui.components.menu.MenuIcon
import elfak.mosis.tourguide.ui.components.scaffold.MenuData
import elfak.mosis.tourguide.ui.components.scaffold.MenuViewModel
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import es.dmoral.toasty.Toasty

@Composable
fun FriendsScreen(
    viewModel: FriendsScreenViewModel,
    navController: NavController
){
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val menuViewModel = hiltViewModel<MenuViewModel>()
    Scaffold(
        scaffoldState = scaffoldState,
        // top navigation bar with menu button
        topBar = {
            TourGuideTopAppBar(
                title = stringResource(id = R.string.friends),
                scaffoldState = scaffoldState,
                coroutineScope = coroutineScope
            )
        },
        // menu content
        drawerContent = {
            TourGuideNavigationDrawer(
                navController = navController,
                menuViewModel = menuViewModel
            )

        }
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            // tab selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 5.dp)
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for(state in FriendsScreenState.values()) {
                    Column(
                        modifier = Modifier
                            .wrapContentWidth()
                            .weight(fill = true, weight = 1f)
                            .clip(RoundedCornerShape(5.dp))
                            .clickable { viewModel.setScreenState(state) }
                        , horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.toString(),
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.primary
                        )
                        Spacer(Modifier.height(2.dp))
                        AnimatedVisibility(viewModel.uiState.screenState == state) {
                            SelectedTabIndicator()
                        }
                    }
                }
            }

            // search field
            AnimatedVisibility(
                visible = viewModel.uiState.screenState != FriendsScreenState.Requests,
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {
                Spacer(Modifier.height(15.dp))
                SearchField(
                    onSearch = { viewModel.filterFriends(viewModel.uiState.searchText) },
                    text = viewModel.uiState.searchText,
                    onTextChanged = { text ->
                        viewModel.setSerachText(text)
                        viewModel.filterFriends(viewModel.uiState.searchText)
                    },
                    label = stringResource(id = R.string.search_friends) + ":",
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSecondary
                        )
                    },
                    placeholder = stringResource(id = R.string.search_by_username_or_fullname)
                )
            }

            when (viewModel.uiState.screenState) {
                FriendsScreenState.Friends -> { FriendsTab(viewModel.mockUsers()) }
                FriendsScreenState.Requests -> { FriendsRequestsTab(viewModel.mockUsers()) }
                FriendsScreenState.Search -> { SearchFriendsTab(viewModel.mockUsers()) }
            }
        }
    }
}

@Composable
fun FriendsTab(friends: List<FriendCard>) {
    FriendCardContainer(
        friends = friends,
        menuItems = listOf(
            MenuData(
                menuIcon = Icons.Filled.PersonRemove,
                name = "Unfriend",
                onClick = { /* unfriend user */ }
            )
        ),
        screenState = FriendsScreenState.Friends
    ) { friendId, isVisiting ->

    }
}



@Composable
fun FriendsRequestsTab(requests: List<FriendCard>) {
    FriendCardContainer(requests, screenState = FriendsScreenState.Requests) { friendId, isVisiting ->

    }
}

@Composable
fun SearchFriendsTab(searchResults: List<FriendCard>) {
    FriendCardContainer(searchResults, screenState = FriendsScreenState.Search) { friendId, isVisiting ->

    }
}

@Composable
fun SelectedTabIndicator() {
    Divider(
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .fillMaxWidth(0.8f),
        thickness = 4.dp

    )
}

@Composable
fun FriendCardContainer(
    friends: List<FriendCard>,
    menuItems: List<MenuData> = emptyList(),
    screenState: FriendsScreenState,
    onCardClick: (friendId: String, isVisiting: Boolean) -> Unit = {_, _ ->  }
) {

    Box {
        LazyColumn(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(15.dp)
        ) {
            items(friends) { friend ->
                FriendCard(
                    friend = friend,
                    menuItems = menuItems,
                    screenState = screenState,
                    onCardClick = { onCardClick(friend.id, true) },
                )
            }
        }
    }
}

@Composable
fun FriendCard(
    friend: FriendCard,
    menuItems: List<MenuData> = emptyList(),
    screenState: FriendsScreenState,
    onCardClick: () -> Unit = { },
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = 5.dp,
        modifier = Modifier
            .wrapContentWidth()
            .clickable {
                onCardClick()
                Toasty
                    .info(context, "Visiting profile")
                    .show()
            }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .padding(bottom = 0.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .height(IntrinsicSize.Min)
            ) {
                UserAvatar(friend.photoUrl)
                Spacer(Modifier.width(5.dp))
                Column {
                    Row {
                        // user info
                        Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                            Text(
                                text = friend.fullname,
                                style = MaterialTheme.typography.h4,
                                color = MaterialTheme.colors.primary
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(
                                text = "@${friend.username}",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.primary
                            )
                        }
                        //menu
                        if (screenState == FriendsScreenState.Friends) {
                            Box {
                                MenuIcon(onClick = { isMenuExpanded = true })
                                Menu(
                                    isMenuExpanded,
                                    menuItems,
                                    onDismissRequest = { isMenuExpanded = false },
                                    onIconClick = { isMenuExpanded = false }
                                )
                            }
                        }

                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 5.dp, end = 8.dp)
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        val colors = MaterialTheme.colors

                        val modifier = Modifier.fillMaxSize()
                        val verticalAlignment = Alignment.CenterVertically
                        val horizontalArrangement = Arrangement.End
                        when (screenState) {
                            FriendsScreenState.Friends -> {
                               Row(modifier, verticalAlignment = verticalAlignment, horizontalArrangement = horizontalArrangement) {
                                   CardButton(
                                       text = stringResource(R.string.invite_to_tour),
                                       icon = Icons.Rounded.Add,
                                       backgroundColor = colors.secondary
                                   ) {
                                       Toasty.info(context, "Invitation sent").show()
                                       // send invitation
                                   }
                               }
                            }
                            FriendsScreenState.Requests -> {
                                Row(modifier, verticalAlignment = verticalAlignment, horizontalArrangement = horizontalArrangement) {
                                    CardButton(
                                        text = stringResource(R.string.accept),
                                        icon = Icons.Rounded.Check,
                                        backgroundColor = colors.primary
                                    ) {
                                        Toasty.info(context, "You are now friends").show()
                                        // accept request
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    CardButton(
                                        text = stringResource(R.string.cancel),
                                        icon = Icons.Rounded.Close,
                                        backgroundColor = colors.error
                                    ) {
                                        Toasty.info(context, "Request deleted").show()
                                        // decline request
                                    }
                                }
                            }
                            FriendsScreenState.Search -> {
                                Row(modifier, verticalAlignment = verticalAlignment, horizontalArrangement = horizontalArrangement) {
                                    CardButton(
                                        text = stringResource(R.string.add_friend),
                                        icon = Icons.Rounded.PersonAdd,
                                        backgroundColor = colors.primary
                                    ) {
                                        Toasty.info(context, "Request sent").show()
                                        // send request
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

@Composable
fun ButtonText(text: String) {
    Text(
        text = text,
        style= MaterialTheme.typography.body2
    )
}

@Composable
fun CardButton(text: String, icon: ImageVector, backgroundColor: Color, onClick: () -> Unit = { }) {
    Row(
        Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleButton(
            icon = icon,
            backgroundColor = backgroundColor,
            onClick = onClick
        )
        Spacer(Modifier.width(3.dp))
        ButtonText(text = text)
    }
}


