package elfak.mosis.tourguide.domain.models.tour

import elfak.mosis.tourguide.ui.components.scaffold.MenuData

data class TourCard(
    val id: String = "",
    val title: String = "",
    val summary: String = "",
    val dropdownOptions: List<MenuData> = emptyList()
    //    val rated: Boolean = false,
)
