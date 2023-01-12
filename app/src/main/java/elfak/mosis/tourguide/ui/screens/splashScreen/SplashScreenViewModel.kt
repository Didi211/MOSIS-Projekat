package elfak.mosis.tourguide.ui.screens.splashScreen

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
