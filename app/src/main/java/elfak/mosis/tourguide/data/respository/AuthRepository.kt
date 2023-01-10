package elfak.mosis.tourguide.data.respository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebase: FirebaseAuth
) {
    //private val firebase: FirebaseAuth = Firebase.auth

    fun login(username: String, password: String) {
        firebase.signInWithEmailAndPassword(username, password)
            .addOnSuccessListener { task ->
                //TODO return information about successful login
                Log.d("FB","${task.user?.uid}")
            }
            .addOnFailureListener { task ->
                //TODO return information about unsuccessful login
                Log.d("FB","${task.message}")
            }
    }

}