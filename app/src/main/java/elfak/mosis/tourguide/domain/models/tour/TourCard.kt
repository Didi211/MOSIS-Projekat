package elfak.mosis.tourguide.domain.models.tour

import elfak.mosis.tourguide.domain.models.Place


data class TourCard(
    val id: String = "",
    val title: String = "",
    val summary: String = "",
    val createdBy: String = "",
    //    val rated: Boolean = false,
)
