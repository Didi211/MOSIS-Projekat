package elfak.mosis.tourguide.domain.models

import android.net.Uri

data class Photo(
    val hasPhoto: Boolean = false,
    val uri: Uri? = null,
    val filename: String = "", // identifier in firebase storage
//    val photoIsInUrl: Boolean = false,
)
