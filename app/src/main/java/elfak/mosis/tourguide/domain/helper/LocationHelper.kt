package elfak.mosis.tourguide.domain.helper

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
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
    private var onLocationAvailabilityListener: (gpsEnabled: Boolean) -> Unit = { }
    private var isRequesting: Boolean = false

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

    fun startLocationTracking() {
        try {
            if (isRequesting) {
                stopLocationTracking()
            }
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(this.request)
            val client: SettingsClient = LocationServices.getSettingsClient(context)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {
                fusedLocationProviderClient.requestLocationUpdates(this.request, this, Looper.getMainLooper())
                this.isRequesting = true
            }
            task.addOnFailureListener {
            }
        }
        catch(e: Exception) {
            Log.e("ERROR",e.message!!)
        }
    }

    fun stopLocationTracking() {
        this.isRequesting = false
//        fusedLocationProviderClient.flushLocations()
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

    fun setonLocationAvailabilityListener(listener: (gpsEnabled: Boolean) -> Unit) {
        this.onLocationAvailabilityListener = listener
    }

    override fun onLocationAvailability(result: LocationAvailability) {
        super.onLocationAvailability(result)
//        Toasty.info(context, "GPS - ${result.isLocationAvailable}").show()
        this.onLocationAvailabilityListener(result.isLocationAvailable)
    }

//    fun enableGps() {
//        val builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(this.request)
//        val client: SettingsClient = LocationServices.getSettingsClient(context)
//        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//        task.addOnSuccessListener {
////            Toasty.info(context, "GPS ON ").show()
////            fusedLocationProviderClient.requestLocationUpdates(this.request, this, Looper.getMainLooper())
//        }
//        task.addOnFailureListener {
//            Toasty.error(context, R.string.location_needed).show()
//        }
//    }

    fun isGpsOn(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}










