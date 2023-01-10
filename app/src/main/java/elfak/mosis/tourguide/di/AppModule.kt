package elfak.mosis.tourguide.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import elfak.mosis.tourguide.data.database.TourGuideDatabase
import elfak.mosis.tourguide.data.database.UsersDatabaseDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideFirebaseAuth(firebase: Firebase): FirebaseAuth = firebase.auth

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
