package daily.dayo.presentation.screen.write

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import daily.dayo.presentation.common.image.ExifInfo
import kotlin.math.min

data class ImageAsset(
    val uriString: String,
    val lastModified: Long = System.currentTimeMillis(),
    val exifInfo: ExifInfo? = null
)

data class CropProperties(
    val cropSize: Float = 0f,
    val cropOffset: Offset = Offset.Zero
)

class ImageCropStateHolder(
    private val originalBitmap: Bitmap,
    private val containerSize: IntSize,
    private val minCropSize: Float = 100f,
    val exifInfo: ExifInfo? = null
) {
    private val _cropProperties = mutableStateOf(CropProperties())
    val cropProperties: State<CropProperties> = _cropProperties

    private val imageAspectRatio = originalBitmap.height.toFloat() / originalBitmap.width
    val imageWidth = containerSize.width.toFloat()
    val imageHeight = imageWidth * imageAspectRatio
    val imageTop = (containerSize.height - imageHeight) / 2f
    private val maxCropSize = min(imageWidth, imageHeight)

    init {
        val initialSize = min(imageWidth, imageHeight) * 0.8f
        _cropProperties.value = CropProperties(
            cropSize = initialSize,
            cropOffset = Offset(
                x = (imageWidth - initialSize) / 2f,
                y = imageTop + (imageHeight - initialSize) / 2f
            )
        )
    }

    // 두 손가락으로 그리드를 확대/축소하고 이동하는 제스처 처리
    fun handleTransform(pan: Offset, zoom: Float) {
        val currentSize = _cropProperties.value.cropSize
        val currentOffset = _cropProperties.value.cropOffset

        val newSize = (currentSize * zoom).coerceIn(minCropSize, maxCropSize)
        val sizeDelta = (newSize - currentSize) / 2f

        val newOffsetX = (currentOffset.x - sizeDelta + pan.x)
            .coerceIn(0f, imageWidth - newSize)
        val newOffsetY = (currentOffset.y - sizeDelta + pan.y)
            .coerceIn(imageTop, imageTop + imageHeight - newSize)

        _cropProperties.value =
            CropProperties(cropSize = newSize, cropOffset = Offset(newOffsetX, newOffsetY))
    }

    // 각 그리드 모서리 드래그 제스처 처리
    fun handleCornerDrag(corner: Corner, dragAmount: Offset) {
        val currentSize = _cropProperties.value.cropSize
        val currentOffset = _cropProperties.value.cropOffset

        val newSize = when (corner) {
            Corner.TOP_LEFT, Corner.TOP_RIGHT, Corner.BOTTOM_LEFT, Corner.BOTTOM_RIGHT -> {
                val potentialSizeX =
                    if (corner.isLeft()) currentSize - dragAmount.x else currentSize + dragAmount.x
                val potentialSizeY =
                    if (corner.isTop()) currentSize - dragAmount.y else currentSize + dragAmount.y
                min(potentialSizeX, potentialSizeY).coerceIn(minCropSize, maxCropSize)
            }
        }

        val newOffset = when (corner) {
            Corner.TOP_LEFT -> Offset(
                currentOffset.x + (currentSize - newSize),
                currentOffset.y + (currentSize - newSize)
            )

            Corner.TOP_RIGHT -> Offset(currentOffset.x, currentOffset.y + (currentSize - newSize))
            Corner.BOTTOM_LEFT -> Offset(currentOffset.x + (currentSize - newSize), currentOffset.y)
            Corner.BOTTOM_RIGHT -> currentOffset
        }

        val finalSize = newSize.coerceIn(minCropSize, maxCropSize)
        val finalOffset = Offset(
            x = newOffset.x.coerceIn(0f, imageWidth - finalSize),
            y = newOffset.y.coerceIn(imageTop, imageTop + imageHeight - finalSize)
        )
        _cropProperties.value = CropProperties(cropSize = finalSize, cropOffset = finalOffset)
    }
}

enum class Corner {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

    fun isLeft(): Boolean = this == TOP_LEFT || this == BOTTOM_LEFT
    fun isTop(): Boolean = this == TOP_LEFT || this == TOP_RIGHT
}