package elfak.mosis.tourguide.ui.screens.friendsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.models.friends.FriendCard
import javax.inject.Inject

@HiltViewModel
class FriendsScreenViewModel @Inject constructor(): ViewModel() {
    var uiState by mutableStateOf(FriendsScreenUiState())
        private set

    init {
        setFriendListFunctions()
        setRequestListFunctions()
        setSearchListFunctions()

        //mocking
        uiState = uiState.copy(
            friends = mockUsers()
        )
    }

    //region UI STATE METHODS

    fun setScreenState(state: FriendsScreenState) {
        uiState = uiState.copy(screenState = state)
    }
    fun setSearchText(text: String) {
        uiState = uiState.copy(searchText = text)

    }
    //endregion

    //region FRIENDS TAB
    private fun setFriendListFunctions() {
        uiState = uiState.copy(friendListFunctions = uiState.friendListFunctions.copy(
            inviteFriendToTour = { id -> inviteFriendToTour(id) },
            unfriendUser = { id -> unfriendUser(id)}
        ))
    }
    private fun addToFriendList(newFriend: FriendCard) {
        uiState = uiState.copy(friends = uiState.friends + newFriend)
    }
    private fun removeFromFriendList(id: String) {
        uiState = uiState.copy(friends = uiState.friends.filter { friend -> friend.id != id })
    }
    private fun setFilteredFriendList(filteredList: List<FriendCard>) {
        uiState = uiState.copy(filteredFriends = filteredList)
    }
    private fun inviteFriendToTour(id: String) {
        // send invitation
        setSuccessMessage("Invitation sent.")
    }
    private fun unfriendUser(id: String) {
        removeFromFriendList(id)
        // unfriend on firestore
    }
    //endregion

    //region REQUESTS TAB
    private fun setRequestListFunctions() {
        uiState = uiState.copy(requestListFunctions = uiState.requestListFunctions.copy(
            acceptRequest = { id -> acceptFriendRequest(id) },
            declineRequest = { id -> declineFriendRequest(id) }
        ))
    }
    private fun removeFromRequestList(id: String) {
        uiState = uiState.copy(requests = uiState.requests.filter { friend -> friend.id != id })
    }
    private fun acceptFriendRequest(id: String) {
        val newFriend = uiState.requests.find { friend -> friend.id == id }
        addToFriendList(newFriend!!)
        removeFromRequestList(id)
        setSuccessMessage("You've got a new friend!")
    }
    private fun declineFriendRequest(id: String) {
        removeFromRequestList(id)
        setSuccessMessage("Request removed.")
    }
    //endregion

    //region SEARCH TAB
    private fun setSearchListFunctions() {
        uiState = uiState.copy(searchListFunctions = uiState.searchListFunctions.copy(
            sendRequest = { id -> sendFriendRequest(id) },
        ))
    }

    private fun sendFriendRequest(id: String) {

    }
    //endregion


    fun startSearch() {
        val searchText = uiState.searchText
        if (uiState.screenState == FriendsScreenState.Friends) {
            if (searchText.isBlank()) return setFilteredFriendList(emptyList())
            val newList = uiState.friends.filter { friend ->
                friend.fullname.contains(searchText) || friend.username.contains(searchText)
            }
            setFilteredFriendList(newList)
            if (newList.isEmpty()) { setSuccessMessage("No friend found.") }
        }
        else {
            // search on firebase
        }
    }


    private fun mockUsers(): List<FriendCard> {
        val users = mutableListOf<FriendCard>()
        for (i in 0..5) {
            var photoUrl: String? = null
            if (i in 1..3)
                photoUrl = "https://firebasestorage.googleapis.com/v0/b/tour-guide-375011.appspot.com/o/profiles%2Fmiticd99?alt=media&token=fe22f942-7b10-4029-aa6c-5a3253238dd5"
            users.add(FriendCard(
                id = i.toString(),
                fullname = "Friend no:$i",
                username = "username",
                photoUrl =  photoUrl
            ))
        }
        return users
//        return emptyList()
    }

    //region Message Handler
    fun clearErrorMessage() {
        uiState = uiState.copy(toastData = uiState.toastData.copy(hasErrors = false))
    }
    private fun setErrorMessage(message: String) {
        uiState = uiState.copy(toastData = uiState.toastData.copy(errorMessage = message, hasErrors = true))
    }
    private fun setSuccessMessage(message: String) {
        uiState = uiState.copy(toastData = uiState.toastData.copy(successMessage = message, hasSuccessMessage = true))
    }
    fun clearSuccessMessage() {
        uiState = uiState.copy(toastData = uiState.toastData.copy(hasSuccessMessage = false))
    }
    //endregion
}
