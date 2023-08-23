package daily.dayo.presentation.fragment.write

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.dialog.DefaultDialogConfigure
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.LoadingAlertDialog
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.resizeDialogFragment
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.showLoadingDialog
import daily.dayo.presentation.common.extension.makeToast
import daily.dayo.presentation.common.extension.showToast
import daily.dayo.presentation.common.image.ImageUploadUtil.extension
import daily.dayo.presentation.common.image.ImageUploadUtil.isPermitExtension
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentWriteImageOptionBinding
import daily.dayo.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class WriteImageOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentWriteImageOptionBinding> {
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private lateinit var currentTakenPhotoPath: String
    private val loadingAlertDialog by lazy { LoadingAlertDialog.createLoadingDialog(requireContext()) }
    private var warningToast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteImageOptionBinding.inflate(inflater, container, false)
        setDialogFragmentStyle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageSelectGalleryClickListener()
        setImageTakePhotoClickListener()
    }

    override fun onResume() {
        super.onResume()
        resizeImageOptionDialogFragment()
    }

    private fun setDialogFragmentStyle() {
        isCancelable = true
        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setGravity(Gravity.BOTTOM)
        }
    }

    private fun resizeImageOptionDialogFragment() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = DefaultDialogConfigure.getDeviceWidthSize(requireContext())
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setImageSelectGalleryClickListener() {
        binding.layoutWriteImageOptionSelectGallery.setOnDebounceClickListener {
            requestOpenGallery.launch(PERMISSIONS_GALLERY)
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
                        warningToast = requireContext()
                            .makeToast(getString(R.string.permission_fail_message_gallery))
                        warningToast.showToast()
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            PERMISSIONS_GALLERY,
                            1000
                        )
                    }
                    map[getString(R.string.permission_fail_final)]?.let {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                            val uri = Uri.parse("package:${requireContext().packageName}")
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            it.data = uri
                        }
                        warningToast = requireContext()
                            .makeToast(getString(R.string.permission_fail_final_message_gallery))
                        warningToast.showToast()
                        //request denied ,send to settings
                    }
                }

                else -> {
                    //All request are permitted
                    openGallery()
                }
            }
        }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        requestSelectGalleryActivity.launch(intent)
    }

    private val requestSelectGalleryActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val data: Intent? = activityResult.data
                var remainingNum = 5 - writeViewModel.postImageUriList.size()
                if (data?.clipData != null) { //사진 여러 개 선택 시
                    var uriIdx = 0
                    var count = data.clipData!!.itemCount
                    if (count >= remainingNum) {
                        warningToast = requireContext()
                            .makeToast(getString(R.string.write_post_upload_alert_message_image_fail_max))
                        warningToast.showToast()
                    }
                    // 선택한 갯수 만큼 loop
                    while (count > 0) {
                        remainingNum = 5 - writeViewModel.postImageUriList.size()
                        if (remainingNum > 0 && uriIdx < data.clipData!!.itemCount) {
                            val imageUri = data.clipData!!.getItemAt(uriIdx).uri
                            if (imageUri.extension(requireActivity().applicationContext.contentResolver).isPermitExtension) {
                                writeViewModel.addUploadImage(imageUri.toString(), true)
                            } else {
                                warningToast = requireContext()
                                    .makeToast(getString(R.string.write_post_upload_alert_message_image_fail_file_extension))
                                warningToast.showToast()
                            }
                        } else break

                        uriIdx++
                        count--
                    }
                    displayLoadingDialog()
                    findNavController().popBackStack()
                } else { // 단일 선택
                    data?.data?.let { uri ->
                        if (remainingNum <= 1) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.write_post_upload_alert_message_image_fail_max),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        if (remainingNum >= 1) {
                            val imageUri: Uri? = data.data
                            if (imageUri != null) {
                                if (imageUri.extension(requireActivity().applicationContext.contentResolver).isPermitExtension) {
                                    writeViewModel.addUploadImage(imageUri.toString(), true)
                                    showLoadingDialog(loadingAlertDialog)
                                    resizeDialogFragment(requireContext(), loadingAlertDialog, 0.8f)
                                } else {
                                    warningToast = requireContext()
                                        .makeToast(getString(R.string.write_post_upload_alert_message_image_fail_file_extension))
                                    warningToast.showToast()
                                }
                            }
                        }
                        findNavController().popBackStack()
                    }
                }
            }
        }

    private fun setImageTakePhotoClickListener() {
        binding.layoutWriteImageOptionTakePhoto.setOnDebounceClickListener {
            requestOpenCamera.launch(
                PERMISSIONS_CAMERA
            )
        }
    }

    private val requestOpenCamera =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (!it.value) {
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
    private val requestTakePhotoActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                try {
                    val photoFile = File(currentTakenPhotoPath)
                    writeViewModel.addUploadImage(Uri.fromFile(photoFile).toString(), true)
                    displayLoadingDialog()
                } catch (nullException: NullPointerException) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.write_post_upload_alert_message_image_fail_null),
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    findNavController().popBackStack()
                }
            }
        }

    private fun displayLoadingDialog() {
        showLoadingDialog(loadingAlertDialog)
        resizeDialogFragment(requireContext(), loadingAlertDialog, 0.8f)
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