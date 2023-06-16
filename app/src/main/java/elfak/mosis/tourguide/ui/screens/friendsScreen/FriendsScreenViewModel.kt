package elfak.mosis.tourguide.ui.screens.friendsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendsScreenViewModel @Inject constructor(): ViewModel() {
    var uiState by mutableStateOf(FriendsScreenUiState())
        private set

}
