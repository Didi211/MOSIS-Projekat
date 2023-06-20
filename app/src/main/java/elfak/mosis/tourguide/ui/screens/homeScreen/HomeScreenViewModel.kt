package elfak.mosis.tourguide.ui.screens.homeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.data.models.notification.NotificationModel
import elfak.mosis.tourguide.data.models.notification.TourNotificationModel
import elfak.mosis.tourguide.data.models.notification.TourNotificationType
import elfak.mosis.tourguide.data.models.tour.TourModel
import elfak.mosis.tourguide.domain.models.tour.TourCard
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.NotificationRepository
import elfak.mosis.tourguide.domain.repository.TourRepository
import elfak.mosis.tourguide.domain.repository.UsersRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    var uiState by mutableStateOf(HomeScreenUiState())
        private set

    init {
        viewModelScope.launch {
            launch {  getTours() }
            launch { getFriends() }
            launch {
                val userId = authRepository.getUserIdLocal()
                setUserId(userId!!)
            }
        }
    }

    private fun setUserId(id: String) {
        uiState = uiState.copy(userId = id)
    }
    private fun setTours (tours: List<TourCard>) {
        uiState = uiState.copy(tours = tours)
    }

    private fun setRefreshingFlag(value: Boolean) {
        uiState = uiState.copy(isRefreshing = value)
    }

    private suspend fun getTours() {
        val userId = authRepository.getUserIdLocal()
        setTours(tourRepository.getAllTours(userId!!).map { it.toTourCard() })
    }

    private suspend fun getFriends() {
        val userId = authRepository.getUserIdLocal()
        setFriends(usersRepository.getUserFriends(userId!!))
    }
    private fun setFriends(friends: List<UserModel>) {
        uiState = uiState.copy(friends = friends)
    }

    fun refreshTours() {
        viewModelScope.launch {
            setRefreshingFlag(true)
            delay(500)
            getTours()
            setRefreshingFlag(false)
        }
    }

    fun deleteTour(tourId: String) {
        viewModelScope.launch {
            tourRepository.deleteTour(tourId)
            val tours = uiState.tours.filter { it.id != tourId }
            setTours(tours)
            setSuccessMessage("Tour deleted.")
        }
    }

    fun sendTourInvitation(inviteUserId: String) {
        viewModelScope.launch {
            val tour = uiState.inviteTour
            val sender = async { usersRepository.getUserData(uiState.userId) }
            if (notificationRepository.isUserInvitedToTour(inviteUserId, tour.id)) {
                setSuccessMessage("User is already invited.")
                return@launch
            }

            val notification = TourNotificationModel(
                notification = NotificationModel(
                    senderId = uiState.userId,
                    receiverId = inviteUserId,
                    message = "${sender.await().fullname} invited you to tour '${tour.title}.'",
                    photoUrl = sender.await().thumbnailPhotoUrl
                ),
                notificationType = TourNotificationType.Invite,
                tourId = tour.id
            )
            notificationRepository.sendTourNotification(notification)
            setSuccessMessage("Invitation sent.")
        }
    }
    fun setTourForInvite(tour: TourCard) {
        uiState = uiState.copy(inviteTour = tour)
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
