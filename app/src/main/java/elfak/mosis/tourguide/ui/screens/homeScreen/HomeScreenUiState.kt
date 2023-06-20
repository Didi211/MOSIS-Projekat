package elfak.mosis.tourguide.ui.screens.homeScreen

import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.data.models.tour.TourModel
import elfak.mosis.tourguide.domain.models.ToastData
import elfak.mosis.tourguide.domain.models.tour.TourCard


data class HomeScreenUiState(
    val userId: String = "",
    val tours: List<TourCard> = emptyList(),
    val isRefreshing: Boolean = false,
    val friends: List<UserModel> = emptyList(),
    val inviteTour: TourCard = TourCard(),
    val toastData: ToastData = ToastData()
)

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
