package elfak.mosis.tourguide.domain.repository

import elfak.mosis.tourguide.domain.models.Photo

interface PhotoRepository {
    suspend fun uploadUserPhoto(photo: Photo): String
//    fun getUserPhoto(url: String)
}