package elfak.mosis.tourguide.ui.screens.friendsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.notification.FriendRequestNotificationModel
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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

    private var textInputJob: Job? = null
    private var job: Job? = null


    init {
        viewModelScope.launch {
            // fetching id
            val userId = async { authRepository.getUserIdLocal() }.await()
            setUserId(userId!!)

            // fetch users' tours
            withContext(Dispatchers.IO) {
                val tours = tourRepository.getAllTours(userId)
                setTours(tours.map { tour -> tour.toTourSelectionDisplay() })
            }
        }

        viewModelScope.launch {
            // fetching friends
            launch {
                val friends = usersRepository.getUserFriends(uiState.userId)
                    .map { friend -> friend.toFriendCard() }
                setFriends(friends)
                setFilteredFriendList(friends)
            }

            // fetching friend requests
            launch {
                val requests = usersRepository.getUserFriendRequests(uiState.userId)
                    .map { friend -> friend.toFriendCard() }
                setRequests(requests)
            }
        }
    }

    //region UI STATE METHODS
    private fun setUserId(userId: String) {
        uiState = uiState.copy(userId = userId)
    }

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

    private fun addToFriendList(newFriend: FriendCard) {
        val newList = uiState.friends + newFriend
        uiState = uiState.copy(friends = newList,filteredFriends = newList)
    }
    private fun removeFromFriendList(id: String) {
        val newList = uiState.friends.filter { friend -> friend.id != id }
        uiState = uiState.copy(friends = newList, filteredFriends = newList)
    }
    private fun setFilteredFriendList(filteredList: List<FriendCard>) {
        uiState = uiState.copy(filteredFriends = filteredList)
    }
    fun sendTourInvitation(tourId: String, friendId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val sender = async { usersRepository.getUserData(uiState.userId) }
            val tour = async { tourRepository.getTour(tourId) }

            if (tour.await().createdBy == friendId) {
                setErrorMessage("Can't invite the creator.")
                return@launch
            }

            if(notificationRepository.isUserInvitedToTour(friendId, tour.await().id)) {
                setSuccessMessage("User is already invited.")
                return@launch
            }

            val notification = TourNotificationModel(
                notification = NotificationModel(
                    senderId = uiState.userId,
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

    fun unfriendUser(friendId: String) {
        viewModelScope.launch {
            usersRepository.removeFriend(uiState.userId, friendId)
        }
        removeFromFriendList(friendId)
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

    private fun removeFromRequestList(id: String) {
        uiState = uiState.copy(requests = uiState.requests.filter { friend -> friend.id != id })
    }
    fun acceptFriendRequest(friendId: String) {
        viewModelScope.launch {
            val notification = notificationRepository.getFriendRequestNotification(uiState.userId, friendId)
            notificationRepository.deleteFriendRequestNotification(notification.notification.id)
            usersRepository.addFriend(uiState.userId, friendId)

            val newFriend = uiState.requests.find { friend -> friend.id == friendId }
            addToFriendList(newFriend!!)
            removeFromRequestList(friendId)
            setSuccessMessage("You've got a new friend!")
        }
    }
    fun declineFriendRequest(friendId: String) {
        viewModelScope.launch {
            val notification = notificationRepository.getFriendRequestNotification(uiState.userId, friendId)
//            notificationRepository.sendFriendRequestResponse(notification.notification.id, NotificationResponseType.Accepted)
            notificationRepository.deleteFriendRequestNotification(notification.notification.id)
            removeFromRequestList(friendId)
            setSuccessMessage("Request removed.")
        }
    }

    //endregion

    //region SEARCH TAB
    private fun setSearchResults(results: List<FriendCard>) {
        uiState = uiState.copy(searchResults = results)

    }

    fun sendFriendRequest(friendId: String) {
        viewModelScope.launch {
            try {
                if (usersRepository.areFriends(uiState.userId, friendId)) {
                    throw Exception("You are already friends.")
                }

                val sender = async { usersRepository.getUserData(uiState.userId) }
                val notification = FriendRequestNotificationModel(
                    notification = NotificationModel(
                        senderId = uiState.userId,
                        receiverId = friendId,
                        message = "${sender.await().fullname} sent you a friend request.",
                        photoUrl = sender.await().thumbnailPhotoUrl
                    ),
                )
                notificationRepository.sendFriendRequestNotification(notification)
                setSuccessMessage("Friend request sent.")
            }
            catch (ex: Exception) {
                ex.message?.let { setErrorMessage(it) }
            }
        }
    }
    //endregion


    fun startSearch() {
        if (uiState.screenState == FriendsScreenState.Friends) {
           filterFriends()
        }
        else {
            // search on firebase
            searchFriends()
        }
    }

    private fun filterFriends() {
        val searchTextLowercase = uiState.searchText.lowercase()
        if (searchTextLowercase.isBlank()) return setFilteredFriendList(uiState.friends)
        val newList = uiState.friends.filter { friend ->
            friend.fullname
                .lowercase()
                .contains(searchTextLowercase)
            || friend.username
                .lowercase()
                .contains(searchTextLowercase)
        }
        setFilteredFriendList(newList)
    }

    private fun searchFriends() {
        if (uiState.searchText.length < 3 || uiState.searchText.isBlank())
            return setSearchResults(emptyList())

        textInputJob?.cancel()
        textInputJob = viewModelScope.launch {
            delay(500)
            job?.cancel()
            job = launch {
                val searchResults = usersRepository.searchFriends(uiState.userId, uiState.searchText)
                setSearchResults(searchResults.map { result -> result.toFriendCard() })
            }
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
