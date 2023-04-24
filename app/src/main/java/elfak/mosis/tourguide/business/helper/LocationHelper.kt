package elfak.mosis.tourguide.business.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
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
            if(!isGpsOn()) {
                // TODO - turn on gps
            }
            fusedLocationProviderClient.requestLocationUpdates(this.request, this, Looper.getMainLooper())

        }
        catch(e: Exception) {
            Log.e("ERORR",e.message!!)
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
}










