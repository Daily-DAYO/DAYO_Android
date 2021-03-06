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
        binding.layoutMyProfileEditImageOptionReset.setOnClickListener {
            setMyProfileImage("resetMyProfileImage", "")
        }
    }

    private fun setImageSelectGalleryClickListener(){
        binding.layoutMyProfileEditImageOptionSelectGallery.setOnClickListener {
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
        // ACTION PICK ?????????, intent type?????? ????????? ????????? ???????????? MediaStore?????? ???????????? ???????????? ?????? ??? ????????? ??? ?????? ??? ??????
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        requestActivity.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val data: Intent? = activityResult.data
                // ????????? ??????????????? ????????? ?????????, data??? data???????????? ?????? ???????????? Uri ??????
                val uri = data?.data!!
                // ????????? ????????? ??????, ?????? ???????????? ?????? ??????
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