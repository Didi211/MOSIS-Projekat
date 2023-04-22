package elfak.mosis.tourguide.business.wrapper

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import elfak.mosis.tourguide.R
import es.dmoral.toasty.Toasty
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionWrapper @Inject constructor() {

    fun createLocationPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
    }
}