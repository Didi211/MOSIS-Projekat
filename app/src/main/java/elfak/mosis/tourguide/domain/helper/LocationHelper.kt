package elfak.mosis.tourguide.domain.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
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
    private var createdPermissions = false
    private var permissions: List<String>? = null
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

    fun changeRequest(timeInterval: Long, minimalDistance: Float) {
        this.timeInterval = timeInterval
        this.minimalDistance = minimalDistance
        createRequest()
        stopLocationTracking()
        startLocationTracking()
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

    fun decodePolyline(encodedPolyline: String): List<LatLng> {
        val polylinePoints = mutableListOf<Pair<Double, Double>>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < encodedPolyline.length) {
            var shift = 0
            var result = 0

            while (true) {
                val byte = encodedPolyline[index++].toInt() - 63
                result = result or ((byte and 0x1F) shl shift)
                shift += 5
                if (byte < 0x20) break
            }

            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0

            while (true) {
                val byte = encodedPolyline[index++].toInt() - 63
                result = result or ((byte and 0x1F) shl shift)
                shift += 5
                if (byte < 0x20) break
            }

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val decodedLat = lat / 1E5
            val decodedLng = lng / 1E5
            polylinePoints.add(Pair(decodedLat, decodedLng))
        }

        return polylinePoints.map { LatLng(it.first, it.second) }
    }


}










