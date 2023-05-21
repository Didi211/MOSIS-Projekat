package elfak.mosis.tourguide.domain.helper

import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionTokenSingleton @Inject constructor() {
    private var _sessionToken: AutocompleteSessionToken? = null
    var token = getSessionToken()
    private fun getSessionToken(): AutocompleteSessionToken {
        if (_sessionToken == null) {
            // Generate a new session token
            _sessionToken = generateSessionToken()
        }
        return _sessionToken!!
    }

    private fun generateSessionToken(): AutocompleteSessionToken {
        // Generate the session token logic
        return AutocompleteSessionToken.newInstance()
    }
    fun invalidateToken() {
        _sessionToken = null
    }
}