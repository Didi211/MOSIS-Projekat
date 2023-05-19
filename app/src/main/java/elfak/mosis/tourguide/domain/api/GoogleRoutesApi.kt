package elfak.mosis.tourguide.domain.api

import elfak.mosis.tourguide.BuildConfig.MAPS_API_KEY
import elfak.mosis.tourguide.domain.models.google.RouteRequest
import elfak.mosis.tourguide.domain.models.google.RouteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import javax.inject.Singleton

@Singleton
interface GoogleRoutesApi {
    // Define other API endpoints here

    @POST("api/routes/get")
    suspend fun getRouteAPI(@Body routeRequest: RouteRequest): RouteResponse

    @GET("api/routes/test")
    suspend fun testApi(): Any

//    @GET("computeRoutes")
//    fun getRouteWithStops(originId: String, destinationId: String, stops: List<String>)
}