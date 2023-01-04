package elfak.mosis.tourguide.data.respository

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    fun login(username: String, password: String) {
        Log.i("AUTH", "Auth Repository executed")
    }

}