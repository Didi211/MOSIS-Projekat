package elfak.mosis.tourguide.domain.services.foreground

import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.data.models.tour.TourModel
import elfak.mosis.tourguide.data.models.tour.TourNotify

data class LocationServiceState(
    val userId: String = "",
    val isListenerRegistered: Boolean = false,
    val tour: TourModel = TourModel(),
    val user: UserModel = UserModel(),
    val isMocking: Boolean = false,
    val tourNotify: TourNotify = TourNotify(),
    val arrivedMeters: Float = 15f,
)
