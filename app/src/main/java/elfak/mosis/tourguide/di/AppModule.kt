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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import elfak.mosis.tourguide.data.respository.AuthRepositoryImpl
import elfak.mosis.tourguide.data.respository.PhotoRepositoryImpl
import elfak.mosis.tourguide.data.respository.TourRepositoryImpl
import elfak.mosis.tourguide.domain.api.RetrofitClient
import elfak.mosis.tourguide.domain.api.TourGuideApi
import elfak.mosis.tourguide.domain.api.TourGuideApiWrapper
import elfak.mosis.tourguide.domain.helper.LocationHelper
import elfak.mosis.tourguide.domain.helper.SessionTokenSingleton
import elfak.mosis.tourguide.domain.helper.UnitConvertor
import elfak.mosis.tourguide.domain.repository.AuthRepository
import elfak.mosis.tourguide.domain.repository.PhotoRepository
import elfak.mosis.tourguide.domain.repository.TourRepository
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

    @Singleton
    @Provides
    fun provideFirebaseStorage(firebase: Firebase): FirebaseStorage = firebase.storage

    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context)
        :FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)


    @Provides
    fun provideLocationHelper(
        @ApplicationContext context: Context,
        fusedLocationProviderClient: FusedLocationProviderClient
    ) : LocationHelper =  LocationHelper(context, fusedLocationProviderClient)


    @Provides
    fun provideSessionTokenSingleton(): SessionTokenSingleton = SessionTokenSingleton()

    @Provides
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient = Places.createClient(context)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = RetrofitClient.googleRoutesApiClient()

    @Provides
    @Singleton
    fun provideTourGuideApi(retrofit: Retrofit): TourGuideApi = retrofit.create(TourGuideApi::class.java)

    @Singleton
    @Provides
    fun provideWrapper(tourGuideApi: TourGuideApi) = TourGuideApiWrapper(tourGuideApi)

    @Provides
    fun provideUnitConvertor(): UnitConvertor = UnitConvertor()

    @Singleton
    @Provides
    fun provideTourRepository(firestore: FirebaseFirestore): TourRepository = TourRepositoryImpl(firestore)

    @Singleton
    @Provides
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): AuthRepository = AuthRepositoryImpl(context, firebaseAuth, firestore)

    @Singleton
    @Provides
    fun providePhotoRepository(firebaseStorage: FirebaseStorage) : PhotoRepository = PhotoRepositoryImpl(firebaseStorage)
}
