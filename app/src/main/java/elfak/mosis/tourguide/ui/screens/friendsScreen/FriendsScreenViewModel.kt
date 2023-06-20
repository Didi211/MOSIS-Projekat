package elfak.mosis.tourguide.ui.screens.friendsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.notification.NotificationModel
import elfak.mosis.tourguide.data.models.notification.TourNotificationModel
import elfak.mosis.tourguide.data.models.notification.TourNotificationType
import elfak.mosis.tourguide.domain.models.friends.FriendCard
import elfak.mosis.tourguide.domain.models.tour.TourSelectionDisplay
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.NotificationRepository
import elfak.mosis.tourguide.domain.repository.TourRepository
import elfak.mosis.tourguide.domain.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FriendsScreenViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val notificationRepository: NotificationRepository
): ViewModel() {
    var uiState by mutableStateOf(FriendsScreenUiState())
        private set

    init {
        setFriendListFunctions()
        setRequestListFunctions()
        setSearchListFunctions()

        setFilteredFriendList(mockUsers())
        setFriends(mockUsers())

        viewModelScope.launch {
            val userId = async { authRepository.getUserIdLocal() }.await()

            // fetch users' tours
            withContext(Dispatchers.IO) {
                val tours = tourRepository.getAllTours(userId!!)
                setTours(tours.map { tour -> tour.toTourSelectionDisplay() })
            }

//            // fetch friends
//            launch(Dispatchers.IO) {
//                val userId = authRepository.getUserIdLocal()
//                val friends = usersRepository.getUserFriends(userId!!)
//                setFriends(friends.map { friend -> friend.toFriendCard() })
//            }
//
//            // fetch friend requests
//            launch(Dispatchers.IO) {
//                val requests = usersRepository.getUserFriendRequests(userId!!)
//                setRequests(requests.map { request -> request.toFriendCard() })
//            }

        }
    }

    //region UI STATE METHODS

    fun setScreenState(state: FriendsScreenState) {
        uiState = uiState.copy(screenState = state)
    }
    fun setSearchText(text: String) {
        uiState = uiState.copy(searchText = text)

    }
    private fun setTours(tours: List<TourSelectionDisplay>) {
        uiState = uiState.copy(tours = tours)
    }

    //endregion

    //region FRIENDS TAB
    private fun setFriends(friends: List<FriendCard>) {
        uiState = uiState.copy(friends = friends)
    }
    private fun setFriendListFunctions() {
        uiState = uiState.copy(friendListFunctions = uiState.friendListFunctions.copy(
            sendTourInvitation = { tourId, friendId ->
                sendTourInvitation(tourId, friendId)
            },
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
    private fun sendTourInvitation(tourId: String, friendId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = async { authRepository.getUserIdLocal() }.await()
            var sender = async { usersRepository.getUserData(userId!!) }
            var tour = async { tourRepository.getTour(tourId) }

            val notification = TourNotificationModel(
                notification = NotificationModel(
                    senderId = userId!!,
                    receiverId = friendId,
                    message = "${sender.await().fullname} invited you to tour '${tour.await().title}.'",
                    photoUrl = sender.await().thumbnailPhotoUrl
                ),
                notificationType = TourNotificationType.Invite,
                tourId = tourId
            )
            notificationRepository.sendTourNotification(notification)
            setSuccessMessage("Invitation sent.")

        }
    }
    private fun unfriendUser(id: String) {
        removeFromFriendList(id)
        // unfriend on firestore
    }
    fun setInviteUserId(id: String) {
        uiState = uiState.copy(inviteUserId = id)
    }
    //endregion

    //region REQUESTS TAB
    private fun setRequests(requests: List<FriendCard>) {
        uiState = uiState.copy(requests = requests)
    }
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
    private fun setSearchResults(results: List<FriendCard>) {
        uiState = uiState.copy(searchResults = results)

    }
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
            if (searchText.isBlank()) return setFilteredFriendList(uiState.friends)
            val newList = uiState.friends.filter { friend ->
                friend.fullname.contains(searchText) || friend.username.contains(searchText)
            }
            setFilteredFriendList(newList)
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
                photoUrl = "https://firebasestorage.googleapis.com/v0/b/tour-guide-375011.appspot.com/o/images%2Fmiticd99%40gmail.com_250x250?alt=media&token=1d698c7f-a19b-4c6f-b69f-d2c39f0cebcb"
            users.add(FriendCard(
                id = "cociJfcajd0ABHKBMj1g",
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
