package elfak.mosis.tourguide.ui.screens.notificationScreen

import elfak.mosis.tourguide.domain.models.ToastData
import elfak.mosis.tourguide.domain.models.notification.NotificationCard

data class NotificationScreenUiState(
    val hasNotification: Boolean = false,
    val notifications: List<NotificationCard> = emptyList(),
    val toastData: ToastData = ToastData()
)