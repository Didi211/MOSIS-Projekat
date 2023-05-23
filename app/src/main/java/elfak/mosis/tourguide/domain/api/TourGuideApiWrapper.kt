package elfak.mosis.tourguide.domain.api

import android.util.Log
import elfak.mosis.tourguide.domain.models.google.RouteRequest
import elfak.mosis.tourguide.domain.models.google.RouteResponse
import elfak.mosis.tourguide.domain.models.google.Waypoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TourGuideApiWrapper @Inject constructor(
    private val apiService: TourGuideApi
) {

    suspend fun getRoute(origin: String, destination: String): RouteResponse? {
        return try {
            val request = RouteRequest(
                origin = Waypoint(origin),
                destination = Waypoint(destination)
            )
            apiService.getRoute(request)

        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun testApi() {
        try {
            val result = apiService.testApi()
            Log.d("RETROFIT_TEST",result.toString())
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}