package daily.dayo.presentation.common.image

import android.graphics.Bitmap
import android.media.ThumbnailUtils

object ImageResizeUtil {
    const val USER_PROFILE_THUMBNAIL_RESIZE_SIZE = 320
    const val POST_IMAGE_RESIZE_SIZE = 1080

    fun resizeBitmap(
        originalBitmap: Bitmap,
        resizedWidth: Int = 480,
        resizedHeight: Int = 480,
    ): Bitmap {
        var bmpWidth = originalBitmap.width.toFloat()
        var bmpHeight = originalBitmap.height.toFloat()

        if (bmpWidth > resizedWidth) {
            // 원하는 너비보다 클 경우의 설정
            val mWidth = bmpWidth / 100
            val scale = resizedWidth / mWidth
            bmpWidth *= scale / 100
            bmpHeight *= scale / 100
        } else if (bmpHeight > resizedHeight) {
            // 원하는 높이보다 클 경우의 설정
            val mHeight = bmpHeight / 100
            val scale = resizedHeight / mHeight
            bmpWidth *= scale / 100
            bmpHeight *= scale / 100
        }
        return Bitmap.createScaledBitmap(originalBitmap, bmpWidth.toInt(), bmpHeight.toInt(), true)
    }

    fun Bitmap.cropCenterBitmap(): Bitmap {
        val dimension = getSquareCropDimensionForBitmap(this)
        return ThumbnailUtils.extractThumbnail(this, dimension, dimension)
    }

    private fun getSquareCropDimensionForBitmap(bitmap: Bitmap): Int {
        //use the smallest dimension of the image to crop to
        return Math.min(bitmap.width, bitmap.height)
    }
}