package elfak.mosis.tourguide.data.respository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
): AuthRepository {
    /* This implementation of firebase functions uses callback functions when the async call is completed
    *  So suspend keyword is not needed, but still they will be call inside the coroutine */

    private val dataStore = context.dataStore
    private val usersRef = fireStore.collection("Users")

    override suspend fun login(username: String, password: String) {
        // current way of handling async functions
        // IMPORTANT - if functions throws exception await won't catch it - use try catch also
        val result =  firebaseAuth.signInWithEmailAndPassword(username, password).await()
        saveUserIdLocal(result.user!!.uid)
    }

    override suspend fun register(user: UserModel, password: String) {
        val result = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
        saveUserIdLocal(result.user!!.uid) // uid is id from authentication not document id
        user.id = result.user!!.uid
        val fireStoreResult = usersRef.add(user).await()
    }

    override suspend fun tryRegister(username: String): Boolean {
        val usernameTaken = usersRef
            .whereEqualTo("username", username)
            .get().await()
        return usernameTaken.isEmpty
    }

    override suspend fun logout() {
        removeUserIdLocal()
        return firebaseAuth.signOut()
    }

    private suspend fun saveUserIdLocal(userId: String) {
        val dataStoreKey = stringPreferencesKey("userId")
        dataStore.edit { auth ->
            auth[dataStoreKey] = userId
        }
    }

    override suspend fun getUserIdLocal(): String? {
        val key = stringPreferencesKey("userId")
        return dataStore.data.first()[key]
    }

    private suspend fun removeUserIdLocal() {
        val dataStoreKey = stringPreferencesKey("userId")
        dataStore.edit { auth ->
            auth.remove(dataStoreKey)
        }
    }
}