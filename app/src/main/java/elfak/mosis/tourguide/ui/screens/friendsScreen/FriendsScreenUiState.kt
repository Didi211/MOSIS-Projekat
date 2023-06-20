package elfak.mosis.tourguide.ui.screens.friendsScreen

import elfak.mosis.tourguide.domain.models.ToastData
import elfak.mosis.tourguide.domain.models.friends.FriendCard
import elfak.mosis.tourguide.domain.models.tour.TourSelectionDisplay

data class FriendsScreenUiState(
    val screenState: FriendsScreenState = FriendsScreenState.Friends,
    val searchText: String = "",

    val friends: List<FriendCard> = emptyList(),
    val filteredFriends: List<FriendCard> = emptyList(),
    val friendListFunctions: FriendListFunctions = FriendListFunctions(),
    val inviteUserId: String = "",
    val tours: List<TourSelectionDisplay> = emptyList(),

    val requests: List<FriendCard> = emptyList(),
    val requestListFunctions: RequestListFunctions = RequestListFunctions(),

    val searchResults: List<FriendCard> = emptyList(),
    val searchListFunctions: SearchListFunctions = SearchListFunctions(),

    val toastData: ToastData = ToastData()
) {
}

data class FriendListFunctions(
    val unfriendUser: (friendId: String) -> Unit = { },
    val sendTourInvitation: (tourId: String, friendId: String) -> Unit = {_, _ ->  },
)
data class RequestListFunctions(
    val acceptRequest: (friendId: String) -> Unit = { },
    val declineRequest: (friendId: String) -> Unit = { },
)
data class SearchListFunctions(
    val sendRequest: (friendId: String) -> Unit = { },
)