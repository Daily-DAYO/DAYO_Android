package com.daily.dayo.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFolderSettingAddBinding
import com.daily.dayo.profile.viewmodel.FolderEditViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class FolderEditFragment  : Fragment() {
    private var binding by autoCleared<FragmentFolderSettingAddBinding>()
    private val folderEditViewModel by activityViewModels<FolderEditViewModel>()
    private val args by navArgs<FolderEditFragmentArgs>()
    private lateinit var initThumbnailImg:String
    lateinit var imageUri : String
    var thumbnailImgBitmap : Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSettingAddBinding.inflate(inflater, container, false)

        setFolderInfoDescription()
        setBackButtonClickListener()
        setConfirmButtonClickListener()
        setFolderSettingThumbnailOptionClickListener()
        observeNavigationFolderSettingImageCallBack()
        return binding.root
    }

    private fun setFolderInfoDescription(){
        folderEditViewModel.requestDetailListFolder(args.folderId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderEditViewModel.detailFolderList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { detailFolderList ->
                                binding.etFolderSettingAddSetTitle.text = SpannableStringBuilder(detailFolderList.name)
                                binding.etFolderSettingAddSetSubheading.text = SpannableStringBuilder(detailFolderList.subheading?:"")
                                when(detailFolderList.privacy){
                                    "ALL" -> binding.radiobuttonFolderSettingAddSetPrivateAll.isChecked = true
                                    "FOLLOWING" -> binding.radiobuttonFolderSettingAddSetPrivateFollowing.isChecked = true
                                    "ONLY_ME" -> binding.radiobuttonFolderSettingAddSetPrivateOnlyMe.isChecked = true
                                    else -> binding.radiobuttonFolderSettingAddSetPrivateFollowing.isChecked = true
                                }
                                initThumbnailImg = "http://117.17.198.45:8080/images/" + detailFolderList.thumbnailImage
                                Glide.with(binding.ivFolderSettingThumbnail.context)
                                    .load(initThumbnailImg)
                                    .into(binding.ivFolderSettingThumbnail)
                            }
                        }
                    }
                })
            }
        }
    }

    private fun setBackButtonClickListener() {
        binding.btnFolderSettingAddBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun setConfirmButtonClickListener() {
        binding.tvFolderSettingAddConfirm.setOnClickListener { it ->
            val name:String = binding.etFolderSettingAddSetTitle.text.toString()
            val subheading:String = binding.etFolderSettingAddSetSubheading.text.toString()
            val privacy:String = when(binding.radiogroupFolderSettingAddSetPrivate.checkedRadioButtonId){
                binding.radiobuttonFolderSettingAddSetPrivateAll.id -> "ALL"
                binding.radiobuttonFolderSettingAddSetPrivateFollowing.id -> "FOLLOWING"
                binding.radiobuttonFolderSettingAddSetPrivateOnlyMe.id -> "ONLY_ME"
                else -> "FOLLOWING"
            }

            if(this::imageUri.isInitialized){
                // 폴더 커버 이미지 변경
                val thumbnailImg = thumbnailImgBitmap?.let { bitmapToFile(it) }
                folderEditViewModel.requestEditFolder(args.folderId,name,privacy,subheading,true, thumbnailImg)
            }else{
                // 폴더 커버 이미지 변경되지 않음
                folderEditViewModel.requestEditFolder(args.folderId,name,privacy,subheading,false, null)
            }

            folderEditViewModel.editSuccess.observe(viewLifecycleOwner, Observer {
                if(it){
                    folderEditViewModel.editSuccess.value = false
                    findNavController().navigateUp()
                }
            })
        }
    }

    private fun setFolderSettingThumbnailOptionClickListener() {
        binding.ivFolderSettingThumbnail.setOnClickListener {
            findNavController().navigate(R.id.action_folderEditFragment_to_folderSettingAddImageOptionFragment)
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
}