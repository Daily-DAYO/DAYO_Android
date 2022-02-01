package com.daily.dayo.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSettingAddBinding.inflate(inflater, container, false)

        setThumbnailImageView()
        setFolderInfoDescription()
        setBackButtonClickListener()
        setConfirmButtonClickListener()
        setThumbnailImg()
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
        binding.tvFolderSettingAddConfirm.setOnClickListener {
            editFolder()
            Toast.makeText(requireContext(), "확인 버튼 클릭", Toast.LENGTH_SHORT).show()
            folderEditViewModel.editSuccess.observe(viewLifecycleOwner, Observer {
                if(it){
                    findNavController().popBackStack()
                }
            })
        }
    }

    private fun setThumbnailImg(){
        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val uri = intent?.data!!
                folderEditViewModel._thumbnailUri.value = uri.toString()
                setThumbnailImageView()
            }
        }

        binding.ivFolderSettingThumbnail.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.type = "image/*"
            startForResult.launch(intent)
        }
    }

    private fun setThumbnailImageView(){
        folderEditViewModel.thumbnailUri.observe(viewLifecycleOwner, Observer {
            Glide.with(binding.ivFolderSettingThumbnail)
                .load(it)
                .into(binding.ivFolderSettingThumbnail)
        })
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

    private fun editFolder(){
        val name:String = binding.etFolderSettingAddSetTitle.text.toString()
        val subheading:String = binding.etFolderSettingAddSetSubheading.text.toString()
        val privacy:String = when(binding.radiogroupFolderSettingAddSetPrivate.checkedRadioButtonId){
            binding.radiobuttonFolderSettingAddSetPrivateAll.id -> "ALL"
            binding.radiobuttonFolderSettingAddSetPrivateFollowing.id -> "FOLLOWING"
            binding.radiobuttonFolderSettingAddSetPrivateOnlyMe.id -> "ONLY_ME"
            else -> "FOLLOWING"
        }
        folderEditViewModel.thumbnailUri.observe(viewLifecycleOwner, Observer { it ->
            if(it != initThumbnailImg){
                val thumbnailImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                   ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, it.toUri()))
                   } else { MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it.toUri())
                   }?.let { bitmapToFile(it) }
                folderEditViewModel.requestEditFolder(args.folderId,name,privacy,subheading,thumbnailImage)
                }
            else{
                folderEditViewModel.requestEditFolder(args.folderId,name,privacy,subheading,null)
            }
        })
    }
}