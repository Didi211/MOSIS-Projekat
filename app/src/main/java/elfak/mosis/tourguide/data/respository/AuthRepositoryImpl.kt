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
    context: Context,
    private val firebaseAuth: FirebaseAuth,
    fireStore: FirebaseFirestore
): AuthRepository {
    /* This implementation of firebase functions uses callback functions when the async call is completed
    *  So suspend keyword is not needed, but still they will be call inside the coroutine */

    private val dataStore = context.dataStore
    private val usersRef = fireStore.collection("Users")

    companion object {
        const val USER_AUTH_ID = "AUTH_ID"
        const val USER_ID = "ID"
    }

    override suspend fun login(email: String, password: String) {
        // current way of handling async functions
        // IMPORTANT - if functions throws exception await won't catch it - use try catch also
        val result =  firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val query = usersRef.whereEqualTo("email", email).get().await()
        val userId = query.toObjects(UserModel::class.java)[0].id
        saveLocal(USER_AUTH_ID,result.user!!.uid)
        saveLocal(USER_ID,userId)
    }

    override suspend fun register(user: UserModel, password: String): String {
        val result = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
        saveLocal(USER_AUTH_ID,result.user!!.uid) // uid is id from authentication not document id
        user.authId = result.user!!.uid
        val userId = usersRef.add(user).await().id
        saveLocal(USER_ID,userId)
        return userId
    }

    override suspend fun tryRegister(username: String): Boolean {
        val usernameTaken = usersRef
            .whereEqualTo("username", username)
            .get().await()
        return usernameTaken.isEmpty
    }

    override suspend fun logout() {
        removeLocal(USER_ID)
        removeLocal(USER_AUTH_ID)
        return firebaseAuth.signOut()
    }

    private suspend fun saveLocal(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { auth ->
            auth[dataStoreKey] = value
        }
    }

    override suspend fun getUserAuthIdLocal(): String? {
        val key = stringPreferencesKey(USER_AUTH_ID)
        return dataStore.data.first()[key]
    }

    override suspend fun getUserIdLocal(): String? {
        val key = stringPreferencesKey(USER_ID)
        return dataStore.data.first()[key]
    }

    private suspend fun removeLocal(key: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { auth ->
            auth.remove(dataStoreKey)
        }
    }

    override suspend fun changePassword(password: String) {
        try {
            if (firebaseAuth.currentUser == null) {
                throw Exception("User not authenticated.")
            }
            if (firebaseAuth.currentUser!!.uid != getUserAuthIdLocal()) {
                throw Exception("User not authenticated.")
            }
            firebaseAuth.currentUser!!.updatePassword(password).await()
        }
        catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun sendResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }
}