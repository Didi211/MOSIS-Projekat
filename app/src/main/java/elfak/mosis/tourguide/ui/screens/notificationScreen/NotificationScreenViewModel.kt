package elfak.mosis.tourguide.ui.screens.notificationScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.models.notification.NotificationCard
import javax.inject.Inject

@HiltViewModel
class NotificationScreenViewModel @Inject constructor(): ViewModel() {
    var uiState by mutableStateOf(NotificationScreenUiState())
        private set

    init {
        setNotifications(mockNotifications())
    }

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

    private fun mockNotifications(): List<NotificationCard> {
        val notificationList = mutableListOf<NotificationCard>()
        for (i in 0..5) {
            var photoUrl: String? = null
            var tourId: String? = null
            var message = "Notification message that ."
            if (i in 1..3) {
                photoUrl = "https://firebasestorage.googleapis.com/v0/b/tour-guide-375011.appspot.com/o/images%2Fmiticd99%40gmail.com_250x250?alt=media&token=1d698c7f-a19b-4c6f-b69f-d2c39f0cebcb"
                tourId = "CSC6Ym5gopFH3VMpZhgw"
                message += "Tour destination changed"
            }
            notificationList.add(
                NotificationCard(
                    id = i.toString(),
                    tourId = tourId,
                    message = message,
                    photoUrl =  photoUrl
                )
            )
        }
        return notificationList
    }

    fun deleteNotification(notificationId: String) {
        removeFromNotificationList(notificationId)
        setSuccessMessage("Notification removed.")
        // update in firestore
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
