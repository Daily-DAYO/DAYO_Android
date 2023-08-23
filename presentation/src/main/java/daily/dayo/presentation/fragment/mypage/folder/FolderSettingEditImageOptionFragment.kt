package daily.dayo.presentation.fragment.mypage.folder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.databinding.FragmentFolderSettingEditImageOptionBinding
import daily.dayo.presentation.common.dialog.DefaultDialogConfigure
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.setOnDebounceClickListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

class FolderSettingEditImageOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentFolderSettingEditImageOptionBinding>()
    private lateinit var currentTakenPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSettingEditImageOptionBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageSelectGalleryClickListener()
        setImageTakePhotoClickListener()
        setImageResetClickListener()
    }

    override fun onResume() {
        super.onResume()
        resizeOptionDialogFragment()
    }

    private fun resizeOptionDialogFragment() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = DefaultDialogConfigure.getDeviceWidthSize(requireContext())
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setImageSelectGalleryClickListener() {
        binding.layoutFolderSettingEditImageOptionSelectGallery.setOnDebounceClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.type = "image/*"
            startForResult.launch(intent)
        }
    }

    private fun setImageResetClickListener() {
        binding.layoutFolderSettingEditImageOptionReset.setOnDebounceClickListener {
            setFolderCoverImage("")
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val uri = intent?.data!!
                setFolderCoverImage(uri.toString())
            } else {
                findNavController().popBackStack()
            }
        }

    private fun setImageTakePhotoClickListener() {
        binding.layoutFolderSettingEditImageOptionCamera.setOnDebounceClickListener {
            requestOpenCamera.launch(
                PERMISSIONS_CAMERA
            )
        }
    }

    private val requestOpenCamera =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (it.value == false) {
                    return@registerForActivityResult
                }
            }
            openCamera()
        }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            var photoFile: File? = null
            val tmpDir: File? = requireContext().cacheDir
            val timeStamp: String =
                SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA).format(Date())
            val photoFileName = "Capture_${timeStamp}_"
            try {
                val tmpPhoto = File.createTempFile(photoFileName, ".jpg", tmpDir)
                currentTakenPhotoPath = tmpPhoto.absolutePath
                photoFile = tmpPhoto
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    Objects.requireNonNull(requireContext().applicationContext),
                    BuildConfig.LIBRARY_PACKAGE_NAME + ".fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                requestTakePhotoActivity.launch(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val requestTakePhotoActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val photoFile = File(currentTakenPhotoPath)
                setFolderCoverImage(Uri.fromFile(photoFile).toString())
            }
        }

    private fun setFolderCoverImage(coverImageUri: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("imageUri", coverImageUri)
        findNavController().popBackStack()
    }

    companion object {
        val PERMISSIONS_CAMERA = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}