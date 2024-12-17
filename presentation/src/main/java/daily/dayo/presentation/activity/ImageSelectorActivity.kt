package daily.dayo.presentation.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import daily.dayo.presentation.R

class ImageSelectorActivity : AppCompatActivity() {
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val resultIntent = Intent()
                resultIntent.putExtra("image_uri", uri.toString())
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        } else {
            finish() // 이미지 선택 취소 시 Activity 종료
        }
    }

    private val requestOpenGallery =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedList: List<String> = permissions.filter { !it.value }.map { it.key }

            when {
                deniedList.isNotEmpty() -> {
                    val map = deniedList.groupBy { permission ->
                        if (shouldShowRequestPermissionRationale(permission)) getString(R.string.permission_fail_second)
                        else getString(R.string.permission_fail_final)
                    }
                    map[getString(R.string.permission_fail_second)]?.let {
                        // request denied , request again
                        Toast.makeText(
                            this,
                            getString(R.string.permission_fail_message_gallery),
                            Toast.LENGTH_SHORT
                        ).show()
                        ActivityCompat.requestPermissions(
                            this,
                            PERMISSIONS_GALLERY,
                            1000
                        )
                    }
                    map[getString(R.string.permission_fail_final)]?.let {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                            val uri = Uri.parse("package:${this.packageName}")
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            it.data = uri
                        }
                        Toast.makeText(
                            this,
                            getString(R.string.permission_fail_final_message_gallery),
                            Toast.LENGTH_SHORT
                        ).show()
                        //request denied, send to settings
                    }
                    finish()
                }

                else -> {
                    //All request are permitted
                    openGallery()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestOpenGallery.launch(PERMISSIONS_GALLERY)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    companion object {
        val PERMISSIONS_GALLERY = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}

/*** Activity Launch 하는 경우 다음과 같이 사용 **/

/*
// onCreate()
val intent = Intent(this, ImageSelectorActivity::class.java)
imagePickerLauncher.launch(intent)

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uriString = result.data?.getStringExtra("image_uri")
            uriString?.let {
                val uri = Uri.parse(it)
                val bitmap = getBitmapFromUri(uri)
                Glide.with(this)
                    .load(bitmap)
                    .into(binding.testImageLoad)
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
            bitmap.copy(Bitmap.Config.ARGB_8888, true) // Bitmap을 mutable로 변환
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

 */