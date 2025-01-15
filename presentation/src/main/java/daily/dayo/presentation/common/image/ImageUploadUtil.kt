package daily.dayo.presentation.common.image

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

object ImageUploadUtil {
    private val PERMIT_IMAGE_EXTENSIONS = listOf("jpg", "jpeg", "png", "webp")

    // Get Image Extension
    fun Uri.extension(contentResolver: ContentResolver): String = contentResolver
            .getType(this)
            .toString()
            .split("/")[1]

    val String.isPermitExtension: Boolean get() = this in PERMIT_IMAGE_EXTENSIONS
}

@Composable
fun launchGallery(
    context: Context,
    onImageSelected: (Uri?) -> Unit,
    onPermissionDenied: () -> Unit
): () -> Unit {
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        onImageSelected(uri)
    }

    val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            onPermissionDenied()
        }
    }

    val openGallery: () -> Unit = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            imagePermission
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            galleryLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(imagePermission)
        }
    }

    return openGallery
}

@Composable
fun launchCamera(
    context: Context,
    onImageCaptured: (Bitmap?) -> Unit,
    onPermissionDenied: () -> Unit
): () -> Unit {
    // 1) 카메라 결과를 받을 Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        onImageCaptured(bitmap)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 권한 허용 시 카메라 실행
            cameraLauncher.launch(null)
        } else {
            onPermissionDenied()
        }
    }

    // 3) 외부에서 이 함수를 호출하면,
    //    - 권한이 없으면 요청,
    //    - 권한이 있으면 카메라 실행
    val openCamera: () -> Unit = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            // 이미 권한이 있으면 곧바로 카메라 실행
            cameraLauncher.launch(null)
        } else {
            // 권한 없으면 요청
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    return openCamera
}