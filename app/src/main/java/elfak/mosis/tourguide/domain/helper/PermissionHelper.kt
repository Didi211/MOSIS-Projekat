package elfak.mosis.tourguide.domain.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionHelper @Inject constructor(
    private val context: Context
) {

    //region LOCATION
    private var createdLocationPermissions = false
    private var locationPermissions: List<String>? = null

    fun createLocationPermissions(): List<String> {
        if (createdLocationPermissions) {
            return locationPermissions!!
        }
        locationPermissions =  listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        return this.locationPermissions!!
    }

    fun hasAllowedLocationPermissions(): Boolean {
        var hasAllPermissions = true

        for (permission in locationPermissions!!) {
            //you can return false instead of assigning, but by assigning you can log all permission values
            if (!hasPermission(permission)) {
                hasAllPermissions = false
            }
        }
        return hasAllPermissions
    }
    //endregion


    private fun hasPermission(permission: String): Boolean {
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED;
    }

}