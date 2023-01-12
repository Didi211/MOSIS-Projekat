package elfak.mosis.tourguide.data.respository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebase: FirebaseAuth
) {
    /* This implementation of firebase functions uses callback functions when the async call is completed
    *  So suspend keyword is not needed, but still they will be call inside the coroutine */

    fun login(username: String, password: String): Task<AuthResult> {
        // current way of handling async functions
        // IMPORTANT - if functions throws exception await won't catch it - use try catch also
        return firebase.signInWithEmailAndPassword(username, password)
    }
}