package elfak.mosis.tourguide.ui.screens.splashScreen

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import elfak.mosis.tourguide.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
