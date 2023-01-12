package elfak.mosis.tourguide.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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



//    @Singleton
//    @Provides
//    fun provideUsersDao(appDatabase: TourGuideDatabase): UsersDatabaseDao
//        = appDatabase.usersDao()
//
//    @Singleton
//    @Provides
//    fun providesAppDatabase(@ApplicationContext context: Context): TourGuideDatabase
//        = Room.databaseBuilder(
//            context,
//            TourGuideDatabase::class.java,
//            "tour_guide_db"
//        )
//        .fallbackToDestructiveMigration()
//        .build()

}
