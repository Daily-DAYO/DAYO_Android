package com.daily.dayo.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFolderSettingAddBinding
import com.daily.dayo.profile.viewmodel.FolderSettingAddViewModel
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.autoCleared
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class FolderSettingAddFragment  : Fragment() {
    private var binding by autoCleared<FragmentFolderSettingAddBinding>()
    private val folderSettingAddViewModel by activityViewModels<FolderSettingAddViewModel>()
    lateinit var imageUri : String
    var thumbnailImgBitmap : Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSettingAddBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setConfirmButtonClickListener()
        setFolderSettingThumbnailOptionClickListener()
        observeNavigationFolderSettingImageCallBack()
        verifyFolderName()
        return binding.root
    }
    private fun setBackButtonClickListener() {
        binding.btnFolderSettingAddBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun setConfirmButtonClickListener() {
        binding.tvFolderSettingAddConfirm.setOnClickListener {
            val name:String = binding.etFolderSettingAddSetTitle.text.toString()
            val subheading:String = binding.etFolderSettingAddSetSubheading.text.toString()
            val privacy:String = when(binding.radiogroupFolderSettingAddSetPrivate.checkedRadioButtonId){
                binding.radiobuttonFolderSettingAddSetPrivateAll.id -> "ALL"
                binding.radiobuttonFolderSettingAddSetPrivateOnlyMe.id -> "ONLY_ME"
                else -> "ALL"
            }
            val thumbnailImg = thumbnailImgBitmap?.let { bitmapToFile(it) }

            folderSettingAddViewModel.requestCreateFolder(name, privacy, subheading, thumbnailImg)
            folderSettingAddViewModel.folderAddAccess.observe(viewLifecycleOwner){
                if(it.getContentIfNotHandled() == true){
                    findNavController().popBackStack()
                } else if (it.getContentIfNotHandled() == false){
                    Toast.makeText(requireContext(), R.string.folder_add_message_fail, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setFolderSettingThumbnailOptionClickListener() {
        binding.ivFolderSettingThumbnail.setOnClickListener {
            findNavController().navigate(R.id.action_folderSettingAddFragment_to_folderSettingAddImageOptionFragment)
        }
    }

    private fun observeNavigationFolderSettingImageCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("imageUri")?.observe(viewLifecycleOwner) {
            imageUri = it
            if(this::imageUri.isInitialized){
                if(imageUri == "") {
                    Glide.with(requireContext())
                        .load(R.drawable.ic_folder_thumbnail_empty)
                        .centerCrop()
                        .into(binding.ivFolderSettingThumbnail)
                    thumbnailImgBitmap = null
                } else {
                    thumbnailImgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, imageUri.toUri()))
                    } else {
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri.toUri())
                    }
                    Glide.with(this)
                        .load(imageUri)
                        .into(binding.ivFolderSettingThumbnail)
                }
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA)
        val fileName = imageFileTimeFormat.format(Date(System.currentTimeMillis())).toString() + ".jpg"
        val cacheDir = requireContext().cacheDir.toString()
        val file = File("$cacheDir/$fileName")
        var out: OutputStream? = null
        try { file.createNewFile()
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } finally {
            out?.close()
        }
        return file
    }

    private fun verifyFolderName(){
        binding.etFolderSettingAddSetTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                when {
                    s.toString().trim().isEmpty() -> {
                        ButtonActivation.setTextViewConfirmButtonInactive(requireContext(), binding.tvFolderSettingAddConfirm)
                    }
                    Pattern.matches("[ㄱ-ㅎ|ㅏ-ㅣ|가-힣|a-z|A-Z|0-9|\\s]*", s.toString().trim()) -> {
                        ButtonActivation.setTextViewConfirmButtonActive(requireContext(), binding.tvFolderSettingAddConfirm)
                    }
                    else -> {
                        Toast.makeText(requireContext(), getString(R.string.folder_add_message_format_fail), Toast.LENGTH_SHORT).show()
                        ButtonActivation.setTextViewConfirmButtonInactive(requireContext(), binding.tvFolderSettingAddConfirm)
                    }
                }
            }
        })
    }
}