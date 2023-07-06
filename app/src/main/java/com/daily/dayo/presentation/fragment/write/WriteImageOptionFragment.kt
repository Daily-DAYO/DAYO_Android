package com.daily.dayo.presentation.fragment.write

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.BuildConfig
import com.daily.dayo.R
import com.daily.dayo.common.dialog.DefaultDialogConfigure
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.dialog.LoadingAlertDialog
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentWriteImageOptionBinding
import com.daily.dayo.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

@AndroidEntryPoint
class WriteImageOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentWriteImageOptionBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private lateinit var currentTakenPhotoPath: String
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteImageOptionBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)
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

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun resizeImageOptionDialogFragment() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = DefaultDialogConfigure.getDeviceWidthSize(requireContext())
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setImageSelectGalleryClickListener() {
        binding.layoutWriteImageOptionSelectGallery.setOnDebounceClickListener {
            requestOpenGallery.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private val requestOpenGallery =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (it.value == false) {
                    return@registerForActivityResult
                }
            }
            openGallery()
        }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        requestSelectGalleryActivity.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val requestSelectGalleryActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val data: Intent? = activityResult.data
                val remainingNum = 5 - writeViewModel.postImageUriList.size()
                if (data?.clipData != null) { //사진 여러 개 선택 시
                    val count = data.clipData!!.itemCount
                    if (count >= remainingNum) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.write_post_upload_alert_message_image_fail_max),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    for (i in 0 until min(count, remainingNum)) { // 이전 선택 사진을 포함해 선택 사진의 총 갯수가 5개 이하면 count를, 초과하면 remainingNumber을 선택
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        writeViewModel.postImageUriList.add(imageUri.toString())
                    }
                    LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
                    LoadingAlertDialog.resizeDialogFragment(requireContext(), loadingAlertDialog, 0.8f)
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
                                writeViewModel.postImageUriList.add(imageUri.toString())
                                LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
                                LoadingAlertDialog.resizeDialogFragment(requireContext(), loadingAlertDialog, 0.8f)
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }

    private fun setImageTakePhotoClickListener() {
        binding.layoutWriteImageOptionTakePhoto.setOnDebounceClickListener {
            requestOpenCamera.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
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
                    BuildConfig.APPLICATION_ID + ".fileprovider",
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
                writeViewModel.postImageUriList.add(Uri.fromFile(photoFile).toString())
                LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
                LoadingAlertDialog.resizeDialogFragment(requireContext(), loadingAlertDialog, 0.8f)
                findNavController().popBackStack()
            }
        }
}