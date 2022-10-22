package com.daily.dayo.presentation.fragment.mypage.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.common.DefaultDialogConfigure
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentProfileEditImageOptionBinding

class ProfileEditImageOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentProfileEditImageOptionBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditImageOptionBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageSelectGalleryClickListener()
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

    private fun setImageResetClickListener() {
        binding.layoutMyProfileEditImageOptionReset.setOnDebounceClickListener {
            setMyProfileImage("resetMyProfileImage", "")
        }
    }

    private fun setImageSelectGalleryClickListener(){
        binding.layoutMyProfileEditImageOptionSelectGallery.setOnDebounceClickListener {
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
        // ACTION PICK 사용시, intent type에서 설정한 종류의 데이터를 MediaStore에서 불러와서 목록으로 나열 후 선택할 수 있는 앱 실행
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        requestActivity.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val data: Intent? = activityResult.data
                // 호출된 갤러리에서 이미지 선택시, data의 data속성으로 해당 이미지의 Uri 전달
                val uri = data?.data!!
                // 이미지 파일과 함께, 파일 확장자도 같이 저장
                val fileExtension = requireContext().contentResolver.getType(uri).toString().split("/")[1]
                setMyProfileImage(uri.toString(), fileExtension)
            } else{
                findNavController().popBackStack()
            }
        }

    private fun setMyProfileImage(ImageString : String, fileExtension : String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("userProfileImageString", ImageString)
        findNavController().previousBackStackEntry?.savedStateHandle?.set("fileExtension", fileExtension)
        findNavController().popBackStack()
    }
}