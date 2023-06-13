package elfak.mosis.tourguide.data.respository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import elfak.mosis.tourguide.domain.models.Photo
import elfak.mosis.tourguide.domain.repository.PhotoRepository
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage
): PhotoRepository {

    private val storageRef: StorageReference = firebaseStorage.reference
    private val profilesRef: StorageReference = storageRef.child("profiles")
    private val thumbnailsRef: StorageReference = storageRef.child("thumbnails")


    override suspend fun uploadUserPhoto(photo: Photo): String {
        if (photo.uri == null) throw Exception("No URI for user photo found.")
//        val userPhotoFile = Uri.fromFile(File(photo.uri.toString()))
        val userPhotoFile = photo.uri
        val metadata = storageMetadata {
            contentType = "image/jpg"
        }
        val photoRef = profilesRef.child(photo.filename)
        try {
            photoRef.putFile(userPhotoFile, metadata).await()
            return photoRef.downloadUrl.await().toString()
        } catch (ex: Exception) {
            throw ex
        }
    }

    override fun getUserPhoto(url: String) {
        TODO("Not yet implemented")
    }
}