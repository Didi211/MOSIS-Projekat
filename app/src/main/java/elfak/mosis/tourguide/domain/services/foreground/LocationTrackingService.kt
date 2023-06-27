package elfak.mosis.tourguide.domain.services.foreground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import elfak.mosis.tourguide.MainActivity
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.data.models.MyLatLng
import elfak.mosis.tourguide.data.models.PlaceModel
import elfak.mosis.tourguide.data.models.UserLocation
import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.data.models.tour.TourModel
import elfak.mosis.tourguide.data.models.tour.TourNotify
import elfak.mosis.tourguide.domain.helper.GoogleMapHelper
import elfak.mosis.tourguide.domain.helper.LocationHelper
import elfak.mosis.tourguide.domain.helper.PermissionHelper
import elfak.mosis.tourguide.domain.models.TourGuideLocationListener
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.TourRepository
import elfak.mosis.tourguide.domain.repository.UsersRepository
import elfak.mosis.tourguide.ui.navigation.NavigationConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class LocationTrackingService @Inject constructor(
): Service(), TourGuideLocationListener {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_MOCK = "ACTION_MOCK"
        private const val SERVICE_WORKING_CHANNEL_ID = "LocationTrackingChannel"
        private const val SERVICE_WORKING_NOTIFICATION_ID = 1
        private const val TOUR_CHANNEL_ID = "TourNotificationChannel"
        private const val TOUR_CHANNEL_NOTIFICATION_ID = 2
    }
    override val name: String
        get() = this::class.simpleName.toString()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private  var serviceState by mutableStateOf(LocationServiceState())
    private var gpsOnMessage: String = ""
    private var gpsOffMessage: String = ""


    //region DI
    @Inject
    lateinit var permissionHelper: PermissionHelper
    @Inject
    lateinit var locationHelper: LocationHelper
    @Inject
    lateinit var usersRepository: UsersRepository
    @Inject
    lateinit var authRepository: AuthRepository
    @Inject
    lateinit var tourRepository: TourRepository
    @Inject
    lateinit var googleMapHelper: GoogleMapHelper
    //endregion

    //region ServiceState Methods
    private fun setIsMocking(mock: Boolean) {
        serviceState = serviceState.copy(isMocking = mock)
    }
    private fun setIsListenerRegistered(value: Boolean) {
        serviceState = serviceState.copy(isListenerRegistered = value)
    }
    private fun setUserId(userId: String) {
        serviceState = serviceState.copy(userId = userId)
    }
    private fun setTour(tour: TourModel) {
        serviceState = serviceState.copy(tour = tour)
    }
    private fun setUser(user: UserModel) {
        serviceState = serviceState.copy(user = user)
    }
    private fun setTourNotify(tourNotify: TourNotify) {
        serviceState = serviceState.copy(tourNotify = tourNotify)

    }
    //endregion

    // region LocationListener
    override fun onLocationResult(location: Location) {
        // persist location
        serviceScope.launch {
            usersRepository.updateUserLocation(serviceState.userId, UserLocation(
                coordinates = MyLatLng(location.latitude, location.longitude)
            ))
        }

        serviceScope.launch {
            val resultLatLng = LatLng(location.latitude, location.longitude)
            val locations: MutableList<PlaceModel> = mutableListOf()
            if (serviceState.tour.waypoints != null) {
                locations.addAll(serviceState.tour.waypoints!!)
            }
            if (serviceState.tour.destination != null) {
                locations.add(serviceState.tour.destination!!)
            }

            val distances = mutableListOf<Pair<Float, PlaceModel>>()
            locations.forEachIndexed { _, loc ->
                val distance = googleMapHelper.distanceInMeter(resultLatLng, loc.location.toGoogleLatLng())
                if (distance <= serviceState.tourNotify.radius) {
                    distances.add(Pair(distance, loc))
                }
            }
            if (distances.isNotEmpty()) {
                distances.sortBy { distance -> distance.first } //ascending - closest is at index = [0]
                val closest = distances[0]
                val notificationMessage: String = if (closest.first <= 5) { //meaning we are at the destination
                    "You've arrived at location: ${distances[0].second.address}!"
                } else {
                    "You are near the location: ${distances[0].second.address}!"
                }
                val notification = createTourNotification(serviceState.tour.title, notificationMessage, serviceState.tour.id)
                updateNotification(notification, TOUR_CHANNEL_NOTIFICATION_ID)
            }
        }
    }

    override fun onLocationAvailability(available: Boolean) {
        if (available) {
            val notification = createSettingsNotification(gpsOnMessage)
            updateNotification(notification, SERVICE_WORKING_NOTIFICATION_ID)
        }
        else {
            val notification = createSettingsNotification(gpsOffMessage)
            updateNotification(notification, SERVICE_WORKING_NOTIFICATION_ID)
        }
    }
    //endregion

    // region OVERRIDE METHODS

    override fun onBind(p0: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createSettingsChannel()
        createTourChannel()
        setMessages()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch {
            val userId = authRepository.getUserIdLocal()
            if (userId != null) {
                setUserId(userId)
//                stopService()
            }
            when (intent?.action) {
                ACTION_START -> { startService() }
                ACTION_STOP -> { stopService() }
                ACTION_MOCK -> { mockLocations() }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (serviceState.isListenerRegistered) {
            locationHelper.unregisterListener(this.name)
            setIsListenerRegistered(false)
        }

    }
    // endregion

    // region HELPER FUNCTIONS

    private  fun startService() {
        if (!permissionHelper.hasAllowedLocationPermissions()) {
            throw Exception("Permissions not allowed. Go in settings to allow.")
        }
        if (serviceState.userId.isBlank()) {
            val notification = createSettingsNotification("User id not found.")
            startForeground(SERVICE_WORKING_NOTIFICATION_ID, notification)
            return
        }

        serviceScope.launch {
            serviceSetup()
            if (!serviceState.isListenerRegistered) {
                locationHelper.registerListener(this@LocationTrackingService)
                setIsListenerRegistered(true)
            }
        }


        val notification = createSettingsNotification(gpsOnMessage)
        startForeground(SERVICE_WORKING_NOTIFICATION_ID, notification)
    }



    private  fun stopService() {
        stopSelf()
    }

    private fun mockLocations() {
        serviceScope.launch {
            serviceSetup()
            if (!serviceState.isListenerRegistered) {
                locationHelper.registerListener(this@LocationTrackingService)
                setIsListenerRegistered(true)
            }
            locationHelper.stopLocationTracking()
            val notification = createSettingsNotification("Mocking your location")
            startForeground(SERVICE_WORKING_NOTIFICATION_ID, notification)

            val locations = listOf(
                LatLng(43.30971080019176, 21.9229167765876), // PMF
                LatLng(43.31468683638086, 21.927918229290018), // Veterinar - trosarina
                LatLng(43.31755610342172, 21.92398446028673), // zona2
                LatLng(43.319233254974606, 21.914439369230358), // lukoil bozidar azdije
                LatLng(43.32471715355245, 21.90788314502992), //general computer service - delta
                LatLng(43.32650250208093, 21.902939681949714), // most mladosti
                LatLng(43.33152079038301, 21.892378476967508) // elfak
            )
            for (loc in locations) {
                locationHelper.mockLocations(loc)
            }
            locationHelper.startLocationTracking()
        }

    }

    private suspend fun serviceSetup() {
        try {
            val user = usersRepository.getUserData(serviceState.userId)
            setUser(user)
            var tourNotify = tourRepository.getTourNotify(user.id)
            if (tourNotify == null) {
                tourNotify = tourRepository.addTourNotify(TourNotify(userId = user.id))
            }
            setTourNotify(tourNotify)
            val tour = tourRepository.getTour(tourNotify.tourId)
            setTour(tour)
        }
        catch (ex: Exception) {
            ex.printStackTrace()

        }
    }

    private fun setMessages() {
        gpsOnMessage = getString(R.string.location_service_gps_on)
        gpsOffMessage = getString(R.string.location_service_gps_off)
    }

    //endregion

    //region Notification Functions
    private fun createTourNotification(title:String, contentText: String, tourId: String): Notification {
       val notification = createNotification(TOUR_CHANNEL_ID, title, contentText)
           .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
           .setContentIntent(createTourPendingIntent(tourId))
        return notification.build()
    }
    private fun createSettingsNotification(contentText: String): Notification {
        val notification = createNotification(SERVICE_WORKING_CHANNEL_ID, "Location Tracking Service", contentText)
            .setContentIntent(createSettingsPendingIntent())
        return notification.build()
    }
    private fun createNotification(channelId: String, title: String, contentText: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_stat_onesignal_default)
    }
    private fun updateNotification(notification: Notification, notificationId: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
    //endregion

    //region Pending Intent Functions
    private fun createTourPendingIntent(tourId: String) : PendingIntent {
        return createPendingIntent("${NavigationConstants.TourUri}/$tourId")
    }
    private fun createSettingsPendingIntent() : PendingIntent {
        return createPendingIntent(NavigationConstants.SettingsUri)
    }
    private fun createPendingIntent(deepLink: String): PendingIntent {
        val startActivityIntent = Intent(
            Intent.ACTION_VIEW,
            deepLink.toUri(),
            this,
            MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(startActivityIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
        return resultPendingIntent!!
    }
    //endregion

    //region Channel Functions
    private fun createSettingsChannel() {
        // Create the NotificationChannel.
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(SERVICE_WORKING_CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
    private fun createTourChannel() {
        // Create the NotificationChannel.
        val name = getString(R.string.tour_channel_name)
        val descriptionText = getString(R.string.tour_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(TOUR_CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    //endregion
    // endregion
}