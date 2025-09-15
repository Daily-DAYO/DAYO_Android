package daily.dayo.presentation.common.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

data class ExifInfo(
    val orientation: Int,
    val rotationDegrees: Int,
    val flipX: Boolean,
    val flipY: Boolean
)

fun ContentResolver.readExifInfo(uri: Uri): ExifInfo {
    openFileDescriptor(uri, "r")?.use { pfd ->
        val exif = ExifInterface(pfd.fileDescriptor)
        val o = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (o) {
            ExifInterface.ORIENTATION_ROTATE_90   -> ExifInfo(o, 90,  false, false)
            ExifInterface.ORIENTATION_ROTATE_180  -> ExifInfo(o, 180, false, false)
            ExifInterface.ORIENTATION_ROTATE_270  -> ExifInfo(o, 270, false, false)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> ExifInfo(o, 0, true,  false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL   -> ExifInfo(o, 0, false, true)
            ExifInterface.ORIENTATION_TRANSPOSE   -> ExifInfo(o, 90,  true,  false)  // flipH + rot90
            ExifInterface.ORIENTATION_TRANSVERSE  -> ExifInfo(o, 270, true,  false)  // flipH + rot270
            else -> ExifInfo(ExifInterface.ORIENTATION_NORMAL, 0, false, false)
        }
    }
    return ExifInfo(ExifInterface.ORIENTATION_UNDEFINED, 0, false, false)
}

fun Bitmap.applyExif(exif: ExifInfo): Bitmap {
    if (exif.rotationDegrees == 0 && !exif.flipX && !exif.flipY) return this
    val m = Matrix()
    // flip 먼저
    if (exif.flipX || exif.flipY) m.preScale(if (exif.flipX) -1f else 1f, if (exif.flipY) -1f else 1f)
    // 회전
    if (exif.rotationDegrees != 0) m.postRotate(exif.rotationDegrees.toFloat())
    return Bitmap.createBitmap(this, 0, 0, width, height, m, true)
}