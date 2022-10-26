package com.daily.dayo.presentation.fragment.mypage.folder

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.databinding.FragmentFolderSettingEditImageOptionBinding
import com.daily.dayo.common.dialog.DefaultDialogConfigure
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener

class FolderSettingEditImageOptionFragment  : DialogFragment() {
    private var binding by autoCleared<FragmentFolderSettingEditImageOptionBinding>()

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
        setImageSelectGalleryClickListener()
        setImageResetClickListener()
        return binding.root
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

    private fun setImageSelectGalleryClickListener(){
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

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val uri = intent?.data!!
            setFolderCoverImage(uri.toString())
        } else{
            findNavController().popBackStack()
        }
    }

    private fun setFolderCoverImage(coverImageUri : String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("imageUri", coverImageUri)
        findNavController().popBackStack()
    }
}