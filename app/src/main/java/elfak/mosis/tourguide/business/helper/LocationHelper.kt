package elfak.mosis.tourguide.business.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.GpsStatus
import elfak.mosis.tourguide.R
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.rpc.context.AttributeContext.Resource
import es.dmoral.toasty.Toasty
import javax.inject.Inject
import javax.inject.Singleton


@SuppressLint("MissingPermission")
@Singleton
class LocationHelper @Inject constructor(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
): LocationCallback() {
    private var timeInterval: Long = 1500
    private var minimalDistance: Float = 10f
    private var request: LocationRequest
    private var onLocationResultListener: (location: Location) -> Unit = { }
    private var createdPermissions = false
    private var permissions: List<String>? = null


    init {
        request = createRequest()

    }



    private fun createRequest(): LocationRequest {
        // New builder
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {
            setMinUpdateDistanceMeters(minimalDistance)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
        return request
    }

    fun changeRequest(timeInterval: Long, minimalDistance: Float) {
        this.timeInterval = timeInterval
        this.minimalDistance = minimalDistance
        createRequest()
        stopLocationTracking()
        startLocationTracking()
    }

    fun startLocationTracking() {
        try {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(this.request)
            val client: SettingsClient = LocationServices.getSettingsClient(context)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {
                Toasty.info(context, "GPS ON ").show()
            }
            task.addOnFailureListener {
                Toasty.error(context, R.string.location_needed, Toast.LENGTH_SHORT).show()
            }
            fusedLocationProviderClient.requestLocationUpdates(this.request, this, Looper.getMainLooper())

        }
        catch(e: Exception) {
            Log.e("ERROR",e.message!!)
        }
    }


    fun stopLocationTracking() {
        fusedLocationProviderClient.flushLocations()
        fusedLocationProviderClient.removeLocationUpdates(this)
    }

    fun setOnLocationResultListener(listener: (location: Location) -> Unit) {
        this.onLocationResultListener = listener
    }

    override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        locationResult ?: return
        for (location in locationResult.locations) {
            this.onLocationResultListener(location)
        }
    }

    fun isGpsOn(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
//    suspend fun getLastLocation(): Location? {
//        return this.fusedLocationProviderClient.lastLocation.await()
//    }

    fun createLocationPermissions(): List<String> {
        if (createdPermissions) {
            return this.permissions!!
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
             this.permissions =  listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            this.permissions =  listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
        return this.permissions!!
    }

    fun distanceInMeter(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLat,startLon,endLat,endLon,results)
        return results[0]
    }

    fun hasAllowedPermissions(): Boolean {
        var hasAllPermissions = true

        for (permission in permissions!!) {
            //you can return false instead of assigning, but by assigning you can log all permission values
            if (!hasPermission(permission)) {
                hasAllPermissions = false
            }
        }
        return hasAllPermissions
    }

    private fun hasPermission(permission: String): Boolean {
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED;
    }
}










