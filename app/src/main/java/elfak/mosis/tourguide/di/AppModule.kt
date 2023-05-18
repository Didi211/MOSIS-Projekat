package elfak.mosis.tourguide.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import elfak.mosis.tourguide.domain.api.GoogleRoutesApi
import elfak.mosis.tourguide.domain.api.RetrofitClient
import elfak.mosis.tourguide.domain.api.RoutesApiWrapper
import elfak.mosis.tourguide.domain.helper.BitmapHelper
import elfak.mosis.tourguide.domain.helper.LocationHelper
import elfak.mosis.tourguide.domain.helper.UnitConvertor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /* For classes that are imported from the outside we define their injection here
    *  Our custom services for DI are created here automatically
    *  */


    // We need firebase as an injection in auth repository and others.
    @Singleton
    @Provides
    fun provideFirebaseAuth(firebase: Firebase): FirebaseAuth = firebase.auth

    @Singleton
    @Provides
    fun provideFirebaseFirestore(firebase: Firebase): FirebaseFirestore = firebase.firestore

    // It is needed for the injection above
    @Singleton
    @Provides
    fun provideFirebase(): Firebase = Firebase


    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context)
        :FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)


    @Provides
    fun provideLocationHelper(
        @ApplicationContext context: Context,
        fusedLocationProviderClient: FusedLocationProviderClient
    ) : LocationHelper =  LocationHelper(context, fusedLocationProviderClient)


    @Provides
    fun provideBitmapHelper() : BitmapHelper = BitmapHelper()

    @Provides
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient = Places.createClient(context)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = RetrofitClient.googleRoutesApiClient()

    @Provides
    @Singleton
    fun provideGoogleRoutesApi(retrofit: Retrofit): GoogleRoutesApi = retrofit.create(GoogleRoutesApi::class.java)

    @Singleton
    @Provides
    fun provideWrapper(googleRoutesApi: GoogleRoutesApi) = RoutesApiWrapper(googleRoutesApi)

    @Provides
    fun provideUnitConvertor(): UnitConvertor = UnitConvertor()


}
