package elfak.mosis.tourguide.domain.api

import elfak.mosis.tourguide.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Singleton
class RetrofitClient() {
    companion object {
        private const val TOUR_GUIDE_API = BuildConfig.API_URL

        fun googleRoutesApiClient(): Retrofit  {
            return Retrofit.Builder()
                .baseUrl(TOUR_GUIDE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }


}