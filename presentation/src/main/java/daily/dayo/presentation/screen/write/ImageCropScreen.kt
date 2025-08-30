package daily.dayo.presentation.screen.write

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import daily.dayo.presentation.R
import daily.dayo.presentation.common.image.ExifInfo
import daily.dayo.presentation.common.image.decodeSampledBitmapFromUri
import daily.dayo.presentation.common.image.readExifInfo
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.view.DayoTextButton
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.WriteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ImageCropScreen(
    navController: NavController,
    viewModel: WriteViewModel,
    imageIndex: Int,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageAssets by viewModel.writeImagesUri.collectAsState()
    val imageAsset = imageAssets.getOrNull(imageIndex)

    var containerSize by remember { mutableStateOf<IntSize?>(null) }
    var stateHolder by remember { mutableStateOf<ImageCropStateHolder?>(null) }

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Uri로부터 비동기적으로 비트맵과 EXIF 정보 로드 (로딩 상태 처리)
    val bitmapState =
        produceState<Pair<Bitmap?, ExifInfo?>?>(
            initialValue = null, 
            key1 = imageAsset, 
            key2 = containerSize
        ) {
            // containerSize가 정해진 후에야 이 블록이 실행됨
            if (imageAsset != null && containerSize != null) {
                value = withContext(Dispatchers.IO) {
                    val uri = Uri.parse(imageAsset.uriString)
                    val exifInfo = context.contentResolver.readExifInfo(uri)
                    // 이미지를 직접 가져오지 않고, 기기에 맞게 디코딩해서 가져옴
                    val bitmap = decodeSampledBitmapFromUri(
                        context.contentResolver,
                        uri,
                        containerSize!!.width,
                        containerSize!!.height
                    )
                    Pair(bitmap, exifInfo)
                }
            } else {
                null
            }
        }
    val sampledBitmap = bitmapState.value?.first
    val exifInfo = bitmapState.value?.second

    // 이미지 로드 실패 또는 Uri가 없는 경우 처리
    if (imageAsset == null) {
        LaunchedEffect(Unit) { onBackClick() }
        return
    }

    // 비트맵과 컨테이너 사이즈가 준비되면 stateHolder를 생성
    LaunchedEffect(sampledBitmap, containerSize, exifInfo) {
        if (sampledBitmap != null && containerSize != null) {
            stateHolder = ImageCropStateHolder(
                originalBitmap = sampledBitmap,
                containerSize = containerSize!!,
                exifInfo = exifInfo
            )
        }
    }

    Scaffold(
        topBar = {
            ImageCropActionbarLayout(
                onBackClick = onBackClick,
                isCropEnabled = stateHolder != null,
                onCropClick = {
                    stateHolder?.let { holder ->
                        scope.launch {
                            viewModel.cropImageAndUpdate(imageIndex, holder)
                            navController.popBackStack()
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DayoTheme.colorScheme.background)
                    .onSizeChanged { containerSize = it },
                contentAlignment = Alignment.Center
            ) {
                // stateHolder의 존재 여부에 따라 로딩 UI 또는 Crop UI를 표시
                stateHolder?.let { holder ->
                    Image(
                        bitmap = sampledBitmap!!.asImageBitmap(),
                        contentDescription = stringResource(id = R.string.write_post_image_edit),
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((holder.imageHeight / LocalDensity.current.density).dp)
                    )
                    ImageCropCanvas(stateHolder = holder)
                } ?: run {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ImageCropCanvas(stateHolder: ImageCropStateHolder) {
    val cropProperties by stateHolder.cropProperties

    // 두 손가락 제스처와 4개의 모서리를 드래그 하는 제스처의 동시 발생을 방지를 위한 리사이징 진행중 FLag
    var isResizing by remember { mutableStateOf(false) }

    val dimPath = remember(cropProperties) {
        Path().apply {
            val cropRect = Rect(
                offset = cropProperties.cropOffset,
                size = Size(cropProperties.cropSize, cropProperties.cropSize)
            )
            addRect(
                Rect(
                    left = 0f,
                    top = stateHolder.imageTop,
                    right = stateHolder.imageWidth,
                    bottom = stateHolder.imageTop + stateHolder.imageHeight
                )
            )
            addRect(cropRect)
            fillType = PathFillType.EvenOdd
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    if (!isResizing) {
                        stateHolder.handleTransform(pan, zoom)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cropRect = Rect(
                offset = cropProperties.cropOffset,
                size = Size(cropProperties.cropSize, cropProperties.cropSize)
            )

            // Dimmed Background
            drawPath(path = dimPath, color = Color.Black.copy(alpha = 0.4f))

            // Grid lines
            val guideLineColor = Color.White.copy(alpha = 0.5f)
            val strokeWidth = 1.dp.toPx()
            val third = cropProperties.cropSize / 3
            drawLine(
                guideLineColor,
                Offset(cropRect.left + third, cropRect.top),
                Offset(cropRect.left + third, cropRect.bottom),
                strokeWidth
            )
            drawLine(
                guideLineColor,
                Offset(cropRect.left + 2 * third, cropRect.top),
                Offset(cropRect.left + 2 * third, cropRect.bottom),
                strokeWidth
            )
            drawLine(
                guideLineColor,
                Offset(cropRect.left, cropRect.top + third),
                Offset(cropRect.right, cropRect.top + third),
                strokeWidth
            )
            drawLine(
                guideLineColor,
                Offset(cropRect.left, cropRect.top + 2 * third),
                Offset(cropRect.right, cropRect.top + 2 * third),
                strokeWidth
            )

            drawRect(
                guideLineColor,
                topLeft = cropRect.topLeft,
                size = cropRect.size,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        CropHandles(
            cropProperties = cropProperties,
            onResizeStart = { isResizing = true },
            onResizeEnd = { isResizing = false },
            onDrag = { corner, dragAmount -> stateHolder.handleCornerDrag(corner, dragAmount) }
        )
    }
}

@Composable
private fun CropHandles(
    cropProperties: CropProperties,
    onResizeStart: () -> Unit,
    onResizeEnd: () -> Unit,
    onDrag: (Corner, Offset) -> Unit
) {
    val density = LocalDensity.current
    val handleSizeDp = 32.dp
    val handleRadiusPx = with(density) { (handleSizeDp / 2).toPx() }

    Corner.values().forEach { corner ->
        val position = Offset(
            x = cropProperties.cropOffset.x + if (corner.isLeft()) 0f else cropProperties.cropSize,
            y = cropProperties.cropOffset.y + if (corner.isTop()) 0f else cropProperties.cropSize
        )

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (position.x - handleRadiusPx).roundToInt(),
                        (position.y - handleRadiusPx).roundToInt()
                    )
                }
                .size(handleSizeDp)
                .pointerInput(corner) {
                    detectDragGestures(
                        onDragStart = { onResizeStart() },
                        onDragEnd = { onResizeEnd() },
                        onDragCancel = { onResizeEnd() }
                    ) { change, dragAmount ->
                        change.consume()
                        onDrag(corner, dragAmount)
                    }
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val center = size.toRect().center
                val stroke = 3.dp.toPx()
                val length = min(cropProperties.cropSize * 0.1f, 20.dp.toPx())

                val hSign = if (corner.isLeft()) 1f else -1f
                val vSign = if (corner.isTop()) 1f else -1f

                drawLine(
                    Color.White,
                    start = center,
                    end = Offset(center.x + (length * hSign), center.y),
                    strokeWidth = stroke
                )
                drawLine(
                    Color.White,
                    start = center,
                    end = Offset(center.x, center.y + (length * vSign)),
                    strokeWidth = stroke
                )
            }
        }
    }
}

@Composable
private fun ImageCropActionbarLayout(
    onBackClick: () -> Unit,
    isCropEnabled: Boolean,
    onCropClick: () -> Unit,
) {
    TopNavigation(
        leftIcon = {
            NoRippleIconButton(
                onClick = onBackClick,
                iconContentDescription = stringResource(R.string.back_sign),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
        title = stringResource(R.string.write_post_image_crop_title),
        rightIcon = {
            DayoTextButton(
                onClick = {
                    if (isCropEnabled) onCropClick()
                },
                text = stringResource(R.string.complete),
                textStyle = DayoTheme.typography.b3.copy(
                    textAlign = TextAlign.Center,
                    color = Dark
                ),
                modifier = Modifier.padding(10.dp),
            )
        },
        titleAlignment = TopNavigationAlign.CENTER,
    )
}