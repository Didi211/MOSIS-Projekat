package elfak.mosis.tourguide.domain.services.foreground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.data.models.MyLatLng
import elfak.mosis.tourguide.data.models.UserLocation
import elfak.mosis.tourguide.domain.helper.LocationHelper
import elfak.mosis.tourguide.domain.helper.PermissionHelper
import elfak.mosis.tourguide.domain.models.TourGuideLocationListener
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.UsersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LocationTrackingService @Inject constructor(
): Service(), TourGuideLocationListener {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        private const val NOTIFICATION_CHANNEL_ID = "LocationTrackingChannel"
        private const val NOTIFICATION_ID = 1
    }
    override val name: String
        get() = this::class.simpleName.toString()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private  var serviceState by mutableStateOf(LocationServiceState())

    //region DI
    @Inject
    lateinit var permissionHelper: PermissionHelper
    @Inject
    lateinit var locationHelper: LocationHelper
    @Inject
    lateinit var usersRepository: UsersRepository
    @Inject
    lateinit var authRepository: AuthRepository
    //endregion

    //region UiState Methods
    private fun setIsListenerRegistered(value: Boolean) {
        serviceState = serviceState.copy(isListenerRegistered = value)
    }
    private fun setUserId(userId: String) {
        serviceState = serviceState.copy(userId = userId)
    }
    //endregion

    // region LocationListener
    override fun onLocationResult(location: Location) {
        // persist location
        serviceScope.launch {
            usersRepository.updateUserLocation(serviceState.userId, UserLocation(
                location = MyLatLng(location.latitude, location.latitude)
            ))
        }
        val notification = createNotification("New location: LAT:${location.latitude}, LONG:${location.longitude}")
        updateNotification(notification)
    }

    override fun onLocationAvailability(available: Boolean) {
        if (!available) {
            val notification = createNotification("Gps is turned off. Service can't track location...")
            updateNotification(notification)
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
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startService()
            ACTION_STOP -> stopService()
        }
        serviceScope.launch {
            val userId = authRepository.getUserIdLocal()
                ?: throw Exception("User not authenticated.")
            setUserId(userId)
        }
        return START_STICKY
    }



    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (serviceState.isListenerRegistered) {
            locationHelper.unregisterListener(this.name)
        }

    }
    // endregion

    // region HELPER FUNCTIONS

    private  fun startService() {
        if (!permissionHelper.hasAllowedLocationPermissions()) {
            throw Exception("Permissions not allowed. Go in settings to allow.")
        }
        if (!serviceState.isListenerRegistered) {
            locationHelper.registerListener(this)
            setIsListenerRegistered(true)
        }
        val notification = createNotification("Location tracking service is running...")
        startForeground(NOTIFICATION_ID, notification)
    }

    private  fun stopService() {
        stopSelf()
    }

    private fun createNotification(contentText: String): Notification {
        val notificationBuilder = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Location Tracking Service")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.my_location)
//            .setContentIntent(createPendingIntent())

        return notificationBuilder.build()
    }
//    private fun createPendingIntent() : PendingIntent {
//        val startActivityIntent = Intent(this, MainActivity::class.java)
//            .putExtra("path",Screen.TourScreen.withOptionalArgs(NavigationArgument("tourId","PUyG8Mtrf1HzKfdwVfxd")))
//        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
//            addNextIntentWithParentStack(startActivityIntent)
//            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//        }
//        return resultPendingIntent!!
//
////        return PendingIntent.getActivity(
////            this, 1,
////            startActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
////        )
//    }

    private fun createChannel() {
        // Create the NotificationChannel.
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun updateNotification(notification: Notification) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    // endregion
}