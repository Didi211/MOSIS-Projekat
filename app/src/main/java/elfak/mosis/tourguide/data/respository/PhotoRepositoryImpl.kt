package elfak.mosis.tourguide.data.respository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import elfak.mosis.tourguide.domain.models.Photo
import elfak.mosis.tourguide.domain.repository.PhotoRepository
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
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
    private val firebaseStorage: FirebaseStorage
): PhotoRepository {

    private val storageRef: StorageReference = firebaseStorage.reference
    private val profilesRef: StorageReference = storageRef.child("profiles")
    private val thumbnailsRef: StorageReference = storageRef.child("thumbnails")


    override suspend fun uploadUserPhoto(photo: Photo): String {
        if (photo.uri == null) throw Exception("No URI for user photo found.")
//        val userPhotoFile = Uri.fromFile(File(photo.uri.toString()))
        val userPhotoFile = compressImage(photo.uri).toUri()
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

//    private suspend fun compressForProfile(photo: Uri): Uri {
//        val compressedPhoto = Compressor.compress(context, photo.toFile())
//        return compressedPhoto.toUri()
//    }

    private suspend fun compressImage(uri: Uri): File = withContext(Dispatchers.IO) {
        val compressedImageFile = File(context.cacheDir, "compressed_image.jpg")

        // Open an input stream from the Uri
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        inputStream?.use { input ->
            // Decode the input stream into a bitmap
            val bitmap = BitmapFactory.decodeStream(input)

            // Compress the bitmap to the desired quality
            val outputStream = FileOutputStream(compressedImageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            outputStream.close()
        }

        return@withContext compressedImageFile
    }
}