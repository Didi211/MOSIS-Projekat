package elfak.mosis.tourguide.domain.services.foreground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import elfak.mosis.tourguide.R
import elfak.mosis.tourguide.domain.helper.LocationHelper
import elfak.mosis.tourguide.domain.helper.PermissionHelper
import elfak.mosis.tourguide.domain.models.TourGuideLocationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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

    @Inject
    lateinit var permissionHelper: PermissionHelper
    @Inject
    lateinit var locationHelper: LocationHelper

    private var isListenerRegistered by mutableStateOf(false)
    override val name: String
        get() = this::class.simpleName.toString()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    // region LocationListener
    override fun onLocationResult(location: Location) {
        // persist in db
    }

    override fun onLocationAvailability(available: Boolean) {
        // gps available -> keep listening for location
        // gps not available -> show in notification that location is off and cant fetch location data

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
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
//        if (isListenerRegistered) {
//            locationHelper.unregisterListener(this.name)
//        }

    }
    // endregion

    // region HELPER FUNCTIONS

    private  fun startService() {
        if (!permissionHelper.hasAllowedLocationPermissions()) {
            throw Exception("Permissions not allowed. Go in settings to allow.")
        }
//        if (!isListenerRegistered) {
//            locationHelper.registerListener(this)
//            isListenerRegistered = true
//        }
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
            .set

        return notificationBuilder.build()
    }
    private fun createChannel() {
        // Create the NotificationChannel.
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(Companion.NOTIFICATION_CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
    // endregion
}