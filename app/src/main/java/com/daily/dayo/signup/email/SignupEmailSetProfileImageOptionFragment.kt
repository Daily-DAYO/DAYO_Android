package com.daily.dayo.signup.email

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.BuildConfig
import com.daily.dayo.databinding.FragmentSignupEmailSetProfileImageOptionBinding
import com.daily.dayo.util.autoCleared
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SignupEmailSetProfileImageOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentSignupEmailSetProfileImageOptionBinding>()
    private lateinit var currentTakenPhotoPath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailSetProfileImageOptionBinding.inflate(inflater, container, false)
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

    private fun setImageSelectGalleryClickListener(){
        binding.layoutSignupEmailSetProfileImageOptionSelectGallery.setOnClickListener {
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
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        requestSelectGalleryActivity.launch(intent)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    val requestSelectGalleryActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val data: Intent? = activityResult.data
            // 호출된 갤러리에서 이미지 선택시, data의 data속성으로 해당 이미지의 Uri 전달
            val uri = data?.data!!
            // 이미지 파일과 함께, 파일 확장자도 같이 저장
            val fileExtension = requireContext().contentResolver.getType(uri).toString().split("/")[1]
            setMyProfileImage(uri.toString(), fileExtension)
        }
    }

    private fun setImageTakePhotoClickListener() {
        binding.layoutSignupEmailSetProfileImageOptionTakePhoto.setOnClickListener {
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
                val photoURI=FileProvider.getUriForFile(Objects.requireNonNull(requireContext().applicationContext), BuildConfig.APPLICATION_ID+".fileprovider", photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                requestTakePhotoActivity.launch(intent)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    val requestTakePhotoActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val photoFile = File(currentTakenPhotoPath)
            setMyProfileImage(Uri.fromFile(photoFile).toString(), "jpg")
        }
    }

    private fun setMyProfileImage(ImageString : String, fileExtension : String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("userProfileImageString", ImageString)
        findNavController().previousBackStackEntry?.savedStateHandle?.set("fileExtension", fileExtension)
        findNavController().popBackStack()
    }
}