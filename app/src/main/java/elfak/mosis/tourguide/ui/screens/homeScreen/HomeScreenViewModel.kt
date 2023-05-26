package elfak.mosis.tourguide.ui.screens.homeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.toTourCard
import elfak.mosis.tourguide.domain.models.tour.TourCard
import elfak.mosis.tourguide.domain.repository.TourRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val tourRepository: TourRepository
) : ViewModel() {
    var uiState by mutableStateOf(HomeScreenUiState())
        private set

    init {
        viewModelScope.launch {
            setTours(tourRepository.getAllTours().map { it.toTourCard() })
        }
    }

    private fun setTours(tours: List<TourCard>) {
        uiState = uiState.copy(tours = tours)
    }


}
