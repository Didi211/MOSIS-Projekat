package elfak.mosis.tourguide.domain.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Singleton
class RetrofitClient() {
    companion object {
        //https://routes.googleapis.com/directions/v2:computeRoutes
        private const val GOOGLE_API_BASE_URL = "https://routes.googleapis.com/"
//        private const val GOOGLE_API_BASE_URL = "https://catfact.ninja/"
        private const val GOOGLE_API_VERSION = "v2"

        fun googleRoutesApiClient(): Retrofit  {
            return Retrofit.Builder()
                .baseUrl(GOOGLE_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }


}