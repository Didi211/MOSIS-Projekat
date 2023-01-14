package elfak.mosis.tourguide.data.respository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import elfak.mosis.tourguide.data.models.UserModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStore: FirebaseFirestore
) {
    /* This implementation of firebase functions uses callback functions when the async call is completed
    *  So suspend keyword is not needed, but still they will be call inside the coroutine */

    fun login(username: String, password: String): Task<AuthResult> {
        // current way of handling async functions
        // IMPORTANT - if functions throws exception await won't catch it - use try catch also
        return firebaseAuth.signInWithEmailAndPassword(username, password)
    }

    suspend fun register(username: String, password: String, email: String, fullname: String): Any {

        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

        val user = UserModel(username, fullname, email)
        val result1 = firebaseStore.collection("Users").add(user).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
        }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        return result
    }

    fun logout() {
        return firebaseAuth.signOut()
    }
}