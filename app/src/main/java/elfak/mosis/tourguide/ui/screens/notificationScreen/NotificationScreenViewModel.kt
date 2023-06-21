package elfak.mosis.tourguide.ui.screens.notificationScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.notification.TourNotificationType
import elfak.mosis.tourguide.data.respository.NotificationResponseType
import elfak.mosis.tourguide.domain.models.notification.NotificationCard
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.NotificationRepository
import elfak.mosis.tourguide.domain.repository.TourRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val tourRepository: TourRepository
): ViewModel() {
    var uiState by mutableStateOf(NotificationScreenUiState())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = authRepository.getUserIdLocal()

            val tourNotifications = notificationRepository.getTourNotifications(userId!!)
            setTourNotifications(tourNotifications.map { notification -> notification.toNotificationCard() })
        }
//        setNotifications(mockNotifications())
    }

    private fun setNotificationStatus(notificationId: String, answered: NotificationResponseType) {
        val notifications = uiState.notifications.map { notif ->
            if (notif.id == notificationId) notif.copy(status = answered)
            else notif
        }
        uiState = uiState.copy(notifications = notifications)
    }

    //region UI STATE METHODS

    private fun setNotifications(notifications: List<NotificationCard>) {
        uiState = uiState.copy(notifications = notifications)
    }

    private fun removeFromNotificationList(id: String) {
        uiState = uiState.copy(
            notifications = uiState.notifications.filter { notification ->
                notification.id != id
            }
        )
    }

    private fun setTourNotifications(notifications: List<NotificationCard>) {
        uiState = uiState.copy(notifications = notifications)
    }

    //endregion

    private fun mockNotifications(): List<NotificationCard> {
        val notificationList = mutableListOf<NotificationCard>()
        for (i in 0..5) {
            var photoUrl: String? = null
            var tourId: String? = null
            var message = "Notification message that ."
            var tourNotificationType: TourNotificationType = TourNotificationType.DataUpdated
            if (i in 1..3) {
                photoUrl = "https://firebasestorage.googleapis.com/v0/b/tour-guide-375011.appspot.com/o/images%2Fmiticd99%40gmail.com_250x250?alt=media&token=1d698c7f-a19b-4c6f-b69f-d2c39f0cebcb"
                tourId = "CSC6Ym5gopFH3VMpZhgw"
                message += "Tour destination changed"
                tourNotificationType = TourNotificationType.Invite
            }
            notificationList.add(
                NotificationCard(
                    id = i.toString(),
                    tourId = tourId,
                    message = message,
                    photoUrl =  photoUrl,
                    tourNotificationType = tourNotificationType
                )
            )
        }
        return notificationList
    }

    fun deleteNotification(notificationId: String) {
        removeFromNotificationList(notificationId)
        setSuccessMessage("Notification removed.")
        // update in firestore
        viewModelScope.launch {
            notificationRepository.deleteTourNotification(notificationId)
        }
    }

    fun acceptTourInvite(notificationId: String) {
        viewModelScope.launch {
            launch { notificationRepository.sendTourNotificationResponse(notificationId, NotificationResponseType.Accepted) }
            val tourId = async { notificationRepository.getTourNotification(notificationId).tourId }
            val userId = async { authRepository.getUserIdLocal() }

            tourRepository.addFriendToTour(tourId.await(), userId.await()!!)
            setNotificationStatus(notificationId, NotificationResponseType.Accepted)
        }
    }

    fun declineTourInvite(id: String) {
        viewModelScope.launch {
            notificationRepository.sendTourNotificationResponse(id, NotificationResponseType.Declined)
            setNotificationStatus(id, NotificationResponseType.Declined)
        }
    }

    //region MESSAGE HANDLER
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
