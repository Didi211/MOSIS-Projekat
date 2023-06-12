package elfak.mosis.tourguide.domain.helper

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import elfak.mosis.tourguide.R
import java.io.File

class CameraFileProvider: FileProvider(R.xml.image_file_path) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory
            )
            val authority = context.packageName
            return getUriForFile(context, authority, file)
        }
    }
}