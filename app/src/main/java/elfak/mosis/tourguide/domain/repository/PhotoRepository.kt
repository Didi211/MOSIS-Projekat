package elfak.mosis.tourguide.domain.repository

import elfak.mosis.tourguide.data.models.UserModel
import elfak.mosis.tourguide.domain.models.Photo

interface PhotoRepository {
    suspend fun uploadUserPhoto(photo: Photo)
    suspend fun updateUserPhotos(userId:String, originalFile: String): String
//    fun getUserPhoto(url: String)
    suspend fun deleteOldImages(originalFile: String)
}