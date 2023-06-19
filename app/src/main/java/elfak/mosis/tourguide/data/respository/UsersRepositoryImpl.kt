package elfak.mosis.tourguide.data.respository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.domain.repository.UsersRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
): UsersRepository {
    private val collectionRef = firestore.collection("Users")

    override suspend fun deleteTestUsers(fullname: String) {
        // TODO - delete user from auth and delete its images
        try {
            val result = collectionRef.whereEqualTo("fullname", fullname).get().await()
            val batch = firestore.batch()
            for (document in result.documents) {
                val documentRef = collectionRef.document(document.id)
                batch.delete(documentRef)
            }
            batch.commit().await()
        }
        catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun getUserData(userId: String): UserModel {
        val user = collectionRef.document(userId).get().await()
            ?: throw Exception("User not found in the database.")
        return user.toObject<UserModel>()!!
    }

    override suspend fun updateUserData(userId: String, user: UserModel) {
        collectionRef.document(userId).get().await()
            .toObject<UserModel>()
            ?: throw Exception("User not found in the database.")
        collectionRef.document(userId).update(
//            UserModel::email, user.email, // email cannot be changed
            UserModel::phoneNumber.name, user.phoneNumber,
            UserModel::fullname.name, user.fullname,
//            UserModel::username.name, user.username
        ).await()
    }

    override suspend fun updateUserPhotos(userId: String, photos: UserModel) {
        collectionRef.document(userId).update(
            UserModel::profilePhotoUrl.name, photos.profilePhotoUrl,
            UserModel::thumbnailPhotoUrl.name, photos.thumbnailPhotoUrl).await()
    }
}