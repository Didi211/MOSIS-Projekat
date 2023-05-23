package elfak.mosis.tourguide.domain.api

import elfak.mosis.tourguide.domain.models.google.RouteRequest
import elfak.mosis.tourguide.domain.models.google.RouteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import javax.inject.Singleton

@Singleton
interface TourGuideApi {
    // Define other API endpoints here

    @POST("api/routes/get")
    suspend fun getRoute(@Body routeRequest: RouteRequest): RouteResponse

    @GET("api/routes/test")
    suspend fun testApi(): Any

//    @GET("computeRoutes")
//    fun getRouteWithStops(originId: String, destinationId: String, stops: List<String>)
}