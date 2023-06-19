package elfak.mosis.tourguide.data.respository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.domain.models.Photo
import elfak.mosis.tourguide.domain.repository.PhotoRepository
import elfak.mosis.tourguide.domain.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val context: Context,
    firebaseStorage: FirebaseStorage,
    private val usersRepository: UsersRepository
): PhotoRepository {

    private val storageRef: StorageReference = firebaseStorage.reference
    private val imagesRef = storageRef.child("images")

    companion object {
        const val ThumbnailDimensions = "250x250"
        const val ProfileDimensions = "600x600"
    }

    override suspend fun uploadUserPhoto(photo: Photo) {
        if (photo.uri == null) throw Exception("No URI for user photo found.")

        val userPhotoFile = compressImage(photo.uri).toUri()
        val metadata = storageMetadata { contentType = "image/jpeg"  }
        val photoRef = imagesRef.child(photo.filename)

        try {
            if(isInStorage("${photo.filename}_${ProfileDimensions}")) {
                deleteOldImages(photo.filename)
            }
            photoRef.putFile(userPhotoFile, metadata).await()
        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun updateUserPhotos(userId: String, originalFile: String): String {
        val fileFullname = "${originalFile}_${ProfileDimensions}"

        // getting urls
        val (profilePhotoUrl, thumbnailPhotoUrl) = withContext(Dispatchers.IO) {
            while (!isInStorage(fileFullname)) {
                delay(1000)
            }
            val profilePhotoUrl = async { getPhotoUrl(originalFile, ProfileDimensions) }
            val thumbnailPhotoUrl = async { getPhotoUrl(originalFile, ThumbnailDimensions) }
            Pair(profilePhotoUrl.await(), thumbnailPhotoUrl.await())
        }

        // linking urls with user
        withContext(Dispatchers.IO) {
            usersRepository.updateUserPhotos(userId, UserModel(
                profilePhotoUrl = profilePhotoUrl,
                thumbnailPhotoUrl = thumbnailPhotoUrl
            ))
        }
        return profilePhotoUrl
    }

    override suspend fun deleteOldImages(originalFile: String) {
        if (originalFile.isBlank()) throw Exception("Filename cannot be empty.")

        withContext(Dispatchers.IO) {
            imagesRef.child("${originalFile}_${ProfileDimensions}").delete()
            imagesRef.child("${originalFile}_${ThumbnailDimensions}").delete()
        }.await()
    }
    private suspend fun isInStorage(filename: String): Boolean {
        return try {
            imagesRef.child(filename).downloadUrl.await()
                .toString().isNotBlank()
        }
        catch (ex: Exception) {
            false
        }
    }

    private suspend fun getPhotoUrl(filename: String, dimensions: String):String {
        return imagesRef.child("${filename}_${dimensions}").downloadUrl.await().toString()
    }

    private suspend fun compressImage(uri: Uri): File = withContext(Dispatchers.IO) {
        val compressedImageFile = File(context.cacheDir, "compressed_image.jpg")

        // Open an input stream from the Uri
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        inputStream?.use { input ->
            // Decode the input stream into a bitmap
            val bitmap = BitmapFactory.decodeStream(input)

            // Compress the bitmap to the desired quality
            val outputStream = FileOutputStream(compressedImageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            outputStream.close()
        }

        return@withContext compressedImageFile
    }
}