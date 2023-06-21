package elfak.mosis.tourguide.domain.helper

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import elfak.mosis.tourguide.domain.models.TourGuideLocationListener
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
    private var isRequesting: Boolean = false
    private val listeners: MutableList<TourGuideLocationListener> = mutableListOf()

    init {
        request = createRequest()
    }

    fun registerListener(newListener: TourGuideLocationListener) {
        if (findListener(newListener.name) != null) {
            throw Exception("Can't register. Listener: ${newListener.name} is already registered.")
        }
        listeners.add(newListener)
        if (isRequesting) {
            return
        }
        startLocationTracking()
    }

    fun unregisterListener(name: String) {
        val listener = findListener(name)
            ?: throw Exception("Can't unregister. Listener: $name is not found.")
        listeners.remove(listener)
        if (listeners.isEmpty()) {
            stopLocationTracking()
        }
    }

    private fun findListener(name: String): TourGuideLocationListener? {
        return listeners.find { listener -> listener.name == name }
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

    private fun startLocationTracking() {
        try {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(this.request)
            val client: SettingsClient = LocationServices.getSettingsClient(context)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {
                fusedLocationProviderClient.requestLocationUpdates(this.request, this, Looper.getMainLooper())
                this.isRequesting = true
            }
        }
        catch(e: Exception) {
            throw e
        }
    }

    private fun stopLocationTracking() {
        this.isRequesting = false
        fusedLocationProviderClient.removeLocationUpdates(this)
    }

    override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        if (locationResult.lastLocation == null) {
            return
        }
        for (listener in listeners) {
            listener.onLocationResult(locationResult.lastLocation!!)
        }
    }

    override fun onLocationAvailability(result: LocationAvailability) {
        super.onLocationAvailability(result)
        for (listener in listeners) {
            listener.onLocationAvailability(result.isLocationAvailable)
        }
//        if (!result.isLocationAvailable) {
//            stopLocationTracking()
//        }
    }

    fun isGpsOn(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

}