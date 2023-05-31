package elfak.mosis.tourguide.ui.screens.homeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.data.models.toTourCard
import elfak.mosis.tourguide.domain.models.tour.TourCard
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.TourRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    var uiState by mutableStateOf(HomeScreenUiState())
        private set
    private val _tours: MutableLiveData<List<TourCard>> = MutableLiveData(emptyList())
    val tours: LiveData<List<TourCard>> = _tours

    init {
        viewModelScope.launch {
            getTours()
        }
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

    fun refreshTours() {
        viewModelScope.launch {
            setRefreshingFlag(true)
            delay(500)
            getTours()
            setRefreshingFlag(false)
        }
    }


}
