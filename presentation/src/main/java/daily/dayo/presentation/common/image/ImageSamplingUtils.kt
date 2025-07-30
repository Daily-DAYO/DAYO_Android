package daily.dayo.presentation.common.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

/**
 *  OOM 방지를 위해 URI와 목표 사이즈를 받아 최적화된 비트맵을 로드하는 함수
 **/
fun decodeSampledBitmapFromUri(
    contentResolver: ContentResolver,
    uri: Uri,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {
    // inJustDecodeBounds = true 로 설정해 이미지의 실제 크기만 확인 (메모리 할당 X)
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    // 최적의 inSampleSize 계산
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

    // 계산된 inSampleSize로 실제 비트맵 디코딩 (메모리 할당 O)
    options.inJustDecodeBounds = false
    return contentResolver.openInputStream(uri)
        ?.use { BitmapFactory.decodeStream(it, null, options) }
}

/**
 *  최적의 inSampleSize 값을 계산하는 헬퍼 함수
 */
private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2
        // requested size보다 크기가 작아지지 않는 가장 큰 inSampleSize 값을 찾는다. (2의 거듭제곱)
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}