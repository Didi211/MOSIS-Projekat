@file:OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)

package elfak.mosis.tourguide.ui.screens.homeScreen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.models.menu.MenuData
import elfak.mosis.tourguide.domain.models.tour.TourCard
import elfak.mosis.tourguide.ui.components.ToastHandler
import elfak.mosis.tourguide.ui.components.dialogs.ChooseFriendDialog
import elfak.mosis.tourguide.ui.components.images.NoToursImage
import elfak.mosis.tourguide.ui.components.menu.Menu
import elfak.mosis.tourguide.ui.components.menu.MenuIcon
import elfak.mosis.tourguide.ui.components.scaffold.MenuViewModel
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideFloatingButton
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideNavigationDrawer
import elfak.mosis.tourguide.ui.components.scaffold.TourGuideTopAppBar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    navigateToTour: (tourId: String?, editMode: Boolean) -> Unit,
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val menuViewModel = hiltViewModel<MenuViewModel>()
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.uiState.isRefreshing,
        onRefresh = { viewModel.refreshTours() }
    )
    var showFriendsDialog by remember { mutableStateOf(false)}

    if (showFriendsDialog) {
        ChooseFriendDialog(
            users = viewModel.uiState.friends,
            onDismiss = { showFriendsDialog = false },
            onOkButtonClick = { friendId: String ->
                viewModel.sendTourInvitation(friendId)
            }
        )
    }

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
                title = stringResource(id = R.string.home),
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState,
            )
        },
        // menu content
        drawerContent = {
            TourGuideNavigationDrawer(
                navController = navController,
                menuViewModel = menuViewModel,
                hideDrawer = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        floatingActionButton = {
            TourGuideFloatingButton(
                contentDescription = stringResource(id = R.string.add),
                icon = Icons.Rounded.Add,
//                modifier = Modifier.size(36.dp),
                onClick = { navigateToTour(null, true) }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AnimatedContent(targetState = viewModel.uiState.tours.isNotEmpty()) { state ->
                when (state) {
                    true -> {
                        TourCardsContainer(
                            refreshState = refreshState,
                            isRefreshing = viewModel.uiState.isRefreshing,
                            tours = viewModel.uiState.tours,
                            onTourEdit = navigateToTour,
                            onTourInviteFriend = { tour ->
                                showFriendsDialog = true
                                viewModel.setTourForInvite(tour)
                            },
                            onTourDelete = viewModel::deleteTour,
                            onCardClick = navigateToTour,
                            checkIsUserCreator = viewModel::checkIsUserCreator
                        )
                    }
                    false -> {
                        NoToursImage(refreshState, isRefreshing = viewModel.uiState.isRefreshing)

                    }
                }
            }
        }
    }
}

@Composable
fun TourCardsContainer(
    refreshState: PullRefreshState,
    isRefreshing: Boolean,
    tours: List<TourCard>,
    onTourEdit: (tourId: String, editMode: Boolean) -> Unit,
    onTourInviteFriend: (tour: TourCard) -> Unit,
    onTourDelete: (tourId: String) -> Unit,
    onCardClick: (tourId: String, editMode: Boolean) -> Unit,
    checkIsUserCreator: (userId: String) -> Boolean
) {
    Box(modifier = Modifier.pullRefresh(refreshState)) {
        LazyColumn(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(15.dp)
        ) {
            items(tours) { tour ->
                TourCard(
                    tour = tour,
                    menuItems = createDropdownOptions(
                        isCreator = checkIsUserCreator(tour.createdBy),
                        tourId = tour.id,
                        onEdit = { onTourEdit(tour.id, true) },
                        onInvite = { onTourInviteFriend(tour) },
                        onDelete = { onTourDelete(tour.id)}
                    ),
                    onClick = { onCardClick(tour.id, false) },
                )
            }
        }
        PullRefreshIndicator(isRefreshing, refreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun TourCard(
    tour: TourCard,
    menuItems: List<MenuData>,
    onClick: () -> Unit = { },
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = 5.dp,
        modifier = Modifier
            .height(150.dp)
            .clickable {
                onClick()
            }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.8f)
                        .padding(10.dp)

                ) {
                    Text(text = tour.title, style = MaterialTheme.typography.h1, color = MaterialTheme.colors.primary)
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = tour.summary,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.primary,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
//                    contentAlignment = Alignment.Center
                ) {
                    Box {
                        MenuIcon(onClick = { isMenuExpanded = true })
                        Menu(
                            isMenuExpanded,
                            menuItems,
                            onDismissRequest = { isMenuExpanded = false },
                            onIconClick = { isMenuExpanded = false }
                        )
                    }
//                    RatingStar(
//                        fraction = if (tour.rated) 1f else 0f,
//                        config = RatingBarConfig()
//                            .activeColor(MaterialTheme.colors.primary)
//                            .inactiveBorderColor(Color.White)
//                            .inactiveBorderColor(MaterialTheme.colors.primary)
//                            .hideInactiveStars(false),
//                        modifier = Modifier.size(40.dp)
//                    )
                }
            }
        }
    }
}

private fun createDropdownOptions(
    isCreator: Boolean,
    tourId: String,
    onEdit: (String) -> Unit,
    onInvite: () -> Unit,
    onDelete: (String) -> Unit,
): List<MenuData> {
    val deleteText = if (isCreator) "Delete" else "Leave"
    return listOf(
        MenuData(Icons.Rounded.Edit, "Edit", onClick = { onEdit(tourId) }),
        MenuData(Icons.Rounded.GroupAdd, "Invite friends", onClick = { onInvite() }),
        MenuData(Icons.Rounded.Delete, deleteText, onClick = { onDelete(tourId) })
    )
}
