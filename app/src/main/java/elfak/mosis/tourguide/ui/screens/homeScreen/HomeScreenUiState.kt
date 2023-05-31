package elfak.mosis.tourguide.ui.screens.homeScreen

import elfak.mosis.tourguide.domain.models.tour.TourCard


data class HomeScreenUiState(
    val tours: List<TourCard> = emptyList(),
    val isRefreshing: Boolean = false,
) {
}

private fun mockTourList(): List<TourCard> {
    val lorem = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.Lorem Ipsum is simply dummy text of the printing and typesetting industry."
    val loremShort = "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
    return listOf(
        TourCard(title = "title1",summary =  loremShort /*, false*/),
        TourCard(title = "title2",summary =  lorem /*, true*/),
        TourCard(title = "title3",summary =  loremShort /*, false*/),
        TourCard(title = "title3",summary =  lorem /*, true*/),
        TourCard(title = "title3",summary =  loremShort /*, false*/),
        TourCard(title = "title3",summary =  lorem /*, false*/),
    )
}
