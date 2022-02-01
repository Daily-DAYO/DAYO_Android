package com.daily.dayo.write

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.BuildConfig
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentWriteImageOptionBinding
import com.daily.dayo.util.DefaultDialogConfigure
import com.daily.dayo.util.autoCleared
import com.daily.dayo.util.setNavigationResult
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class WriteImageOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentWriteImageOptionBinding>()
    private lateinit var currentTakenPhotoPath: String
    private var uploadImageList = ArrayList<Uri>() // 갤러리에서 불러온 이미지 리스트
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteImageOptionBinding.inflate(inflater, container, false)
        // DialogFragment Radius 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Android Version 4.4 이하에서 Blue Line이 상단에 나타는 것 방지
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

    private fun resizeImageOptionDialogFragment() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = DefaultDialogConfigure.getDeviceWidthSize(requireContext())
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setImageSelectGalleryClickListener(){
        binding.layoutWriteImageOptionSelectGallery.setOnClickListener {
            requestOpenGallery.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
    private val requestOpenGallery = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
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
    val requestSelectGalleryActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            uploadImageList.clear()
            val data: Intent? = activityResult.data

            if (data?.clipData != null) { //사진 여러 개 선택 시
                val count = data.clipData!!.itemCount
                if (count > 5) {
                    Toast.makeText(requireContext(), getString(R.string.write_post_upload_alert_message_image_fail_max), Toast.LENGTH_SHORT).show()
                }
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    uploadImageList.add(imageUri)
                }
                setWritingPostImage(uploadImageList)
            } else { // 단일 선택
                data?.data?.let { uri ->
                    val imageUri : Uri?= data?.data
                    if (imageUri != null){
                        uploadImageList.add(imageUri)
                        setWritingPostImage(uploadImageList)
                    }
                }
            }
        }
    }

    private fun setImageTakePhotoClickListener() {
        binding.layoutWriteImageOptionTakePhoto.setOnClickListener {
            requestOpenCamera.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
    private val requestOpenCamera = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            if (it.value == false) {
                return@registerForActivityResult
            }
        }
        openCamera()
    }
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(requireContext().packageManager)!=null) {
            var photoFile: File?= null
            val tmpDir: File?= requireContext().cacheDir
            val timeStamp: String = SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA).format(Date())
            val photoFileName = "Capture_${timeStamp}_"
            try {
                val tmpPhoto = File.createTempFile(photoFileName, ".jpg", tmpDir)
                currentTakenPhotoPath = tmpPhoto.absolutePath
                photoFile = tmpPhoto
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if(photoFile!= null){
                val photoURI= FileProvider.getUriForFile(Objects.requireNonNull(requireContext().applicationContext), BuildConfig.APPLICATION_ID+".fileprovider", photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                requestTakePhotoActivity.launch(intent)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    val requestTakePhotoActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            uploadImageList.clear()
            val photoFile = File(currentTakenPhotoPath)
            uploadImageList.add(Uri.fromFile(photoFile))
            setWritingPostImage(uploadImageList)
        }
    }

    private fun setWritingPostImage(ImageUri : List<Uri>) {
        setNavigationResult("userWritingPostImageUri", ImageUri)
        findNavController().popBackStack()
    }
}