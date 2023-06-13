package elfak.mosis.tourguide.data.respository

import com.google.firebase.firestore.FirebaseFirestore
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
}