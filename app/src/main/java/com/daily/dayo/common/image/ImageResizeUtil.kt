package com.daily.dayo.common.image

import android.graphics.Bitmap

object ImageResizeUtil {
    fun resizeBitmap(
        originalBitmap: Bitmap,
        resizedWidth: Int = 480,
        resizedHeight: Int = 480, ): Bitmap {
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

}