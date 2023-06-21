package elfak.mosis.tourguide.ui.screens.friendsScreen

import elfak.mosis.tourguide.domain.models.ToastData
import elfak.mosis.tourguide.domain.models.friends.FriendCard
import elfak.mosis.tourguide.domain.models.tour.TourSelectionDisplay

data class FriendsScreenUiState(
    val userId: String = "",

    val screenState: FriendsScreenState = FriendsScreenState.Friends,
    val searchText: String = "",

    val friends: List<FriendCard> = emptyList(),
    val filteredFriends: List<FriendCard> = emptyList(),
    val inviteUserId: String = "",
    val tours: List<TourSelectionDisplay> = emptyList(),

    val requests: List<FriendCard> = emptyList(),

    val searchResults: List<FriendCard> = emptyList(),

    val toastData: ToastData = ToastData()
)