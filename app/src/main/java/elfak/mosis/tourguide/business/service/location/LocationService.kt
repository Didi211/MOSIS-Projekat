package elfak.mosis.tourguide.business.service.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*

@SuppressLint("MissingPermission")
class LocationService(
    context: Context,
): LocationCallback() {
    private var timeInterval: Long = 1500
    private var minimalDistance: Float = 10f
    private var request: LocationRequest
    private var locationClient: FusedLocationProviderClient

    init {
        //getting the location client
        locationClient = LocationServices.getFusedLocationProviderClient(context)
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
        locationClient.requestLocationUpdates(this.request, this, Looper.getMainLooper())
    }

    fun stopLocationTracking() {
        locationClient.flushLocations()
        locationClient.removeLocationUpdates(this)
    }

    override fun onLocationResult(location: LocationResult) {
        super.onLocationResult(location)
        // TODO - call location updater
    }

    override fun onLocationAvailability(availability: LocationAvailability) {
        super.onLocationAvailability(availability)
    }
}









