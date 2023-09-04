package daily.dayo.presentation.common.image

import android.content.ContentResolver
import android.net.Uri

object ImageUploadUtil {
    private val PERMIT_IMAGE_EXTENSIONS = listOf("jpg", "jpeg", "png", "webp")

    // Get Image Extension
    fun Uri.extension(contentResolver: ContentResolver): String = contentResolver
            .getType(this)
            .toString()
            .split("/")[1]

    val String.isPermitExtension: Boolean get() = this in PERMIT_IMAGE_EXTENSIONS
}