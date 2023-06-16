package elfak.mosis.tourguide.ui.screens.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.repository.AuthRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authRepository: AuthRepository
) : ViewModel() {

    suspend fun isLoggedIn(): Boolean {
        return withContext(viewModelScope.coroutineContext) {
            val firebaseSignedIn = firebaseAuth.currentUser != null
            val hasAuthId = authRepository.getUserAuthIdLocal() != null
            val hasUserId = authRepository.getUserIdLocal() != null
            return@withContext firebaseSignedIn && hasAuthId && hasUserId
        }
    }
}
