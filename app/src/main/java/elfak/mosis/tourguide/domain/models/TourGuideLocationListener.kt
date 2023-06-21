package elfak.mosis.tourguide.domain.models

import android.location.Location

interface TourGuideLocationListener {
    val name: String
    fun onLocationResult(location: Location)
    fun onLocationAvailability(available: Boolean)
}