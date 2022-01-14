package com.daily.dayo.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.daily.dayo.databinding.FragmentFolderSettingAddBinding
import com.daily.dayo.profile.viewmodel.FolderSettingAddViewModel
import com.daily.dayo.util.autoCleared
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class FolderSettingAddFragment  : Fragment() {
    private var binding by autoCleared<FragmentFolderSettingAddBinding>()
    private val folderSettingAddViewModel by activityViewModels<FolderSettingAddViewModel>()
    var thumbnailImgBitmap : Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSettingAddBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setConfirmButtonClickListener()
        setThumbnailImg()
        return binding.root
    }
    private fun setBackButtonClickListener() {
        binding.btnFolderSettingAddBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun setConfirmButtonClickListener() {
        binding.tvFolderSettingAddConfirm.setOnClickListener {
            createFolder()
            Toast.makeText(requireContext(), "확인 버튼 클릭", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }
    private fun setThumbnailImg(){
        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val uri = intent?.data!!
                thumbnailImgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, uri))
                } else {
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                }
                Glide.with(this)
                    .load(uri)
                    .into(binding.ivFolderSettingThumbnail)
            }
        }

        binding.ivFolderSettingThumbnail.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.type = "image/*"
            startForResult.launch(intent)
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): File{
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

    private fun createFolder(){
        val name:String = binding.etFolderSettingAddSetTitle.text.toString()
        val subheading:String = binding.etFolderSettingAddSetSubheading.text.toString()
        val privacy:String = when(binding.radiogroupFolderSettingAddSetPrivate.checkedRadioButtonId){
            binding.radiobuttonFolderSettingAddSetPrivateAll.id -> "ALL"
            binding.radiobuttonFolderSettingAddSetPrivateFollowing.id -> "FOLLOWING"
            binding.radiobuttonFolderSettingAddSetPrivateOnlyMe.id -> "ONLY_ME"
            else -> "FOLLOWING"
        }
        val thumbnailImg = thumbnailImgBitmap?.let {
            bitmapToFile(it)
        }

        folderSettingAddViewModel.requestCreateFolder(name, privacy, subheading, thumbnailImg)
    }
}