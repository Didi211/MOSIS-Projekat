package elfak.mosis.tourguide.ui.screens.friendsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.models.friends.FriendCard
import javax.inject.Inject

@HiltViewModel
class FriendsScreenViewModel @Inject constructor(): ViewModel() {
    var uiState by mutableStateOf(FriendsScreenUiState())
        private set
    fun setScreenState(state: FriendsScreenState) {
        uiState = uiState.copy(screenState = state)
    }

    fun filterFriends(searchText: Any) {

    }

    fun setSerachText(text: String) {
        uiState = uiState.copy(searchText = text)

    }



    fun mockUsers(): List<FriendCard> {
        val users = mutableListOf<FriendCard>()
        for (i in 0..5) {
            var photoUrl: String? = null
            if (i in 1..3)
                photoUrl = "https://firebasestorage.googleapis.com/v0/b/tour-guide-375011.appspot.com/o/profiles%2Fmiticd99?alt=media&token=fe22f942-7b10-4029-aa6c-5a3253238dd5"
            users.add(FriendCard(
                id = i.toString(),
                fullname = "Aleksandar Dimitrijevic",
                username = "aleksandar.dimitrijevic13",
                photoUrl =  photoUrl
            ))
        }
        return users
    }


}
