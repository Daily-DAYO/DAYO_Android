package com.daily.dayo.presentation.fragment.mypage.folder

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
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
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFolderSettingAddBinding
import com.daily.dayo.common.ButtonActivation
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.Privacy
import com.daily.dayo.presentation.viewmodel.FolderSettingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class FolderEditFragment : Fragment() {
    private var binding by autoCleared<FragmentFolderSettingAddBinding>()
    private val folderSettingViewModel by activityViewModels<FolderSettingViewModel>()
    private val args by navArgs<FolderEditFragmentArgs>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var initThumbnailImg: String
    lateinit var imageUri: String
    var thumbnailImgBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

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
        verifyFolderName()

        return binding.root
    }

    private fun setFolderInfoDescription() {
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.MATCH_PARENT,
            ViewGroup.MarginLayoutParams.MATCH_PARENT
        )

        folderSettingViewModel.requestDetailListFolder(args.folderId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderSettingViewModel.detailFolderList.observe(viewLifecycleOwner) { it ->
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { folder ->
                                binding.tvFolderSettingAddTitle.text =
                                    getString(R.string.folder_edit_title)
                                binding.etFolderSettingAddSetTitle.text =
                                    SpannableStringBuilder(folder.title)
                                folder.subheading?.let { subheading ->
                                    binding.etFolderSettingAddSetSubheading.text =
                                        SpannableStringBuilder(subheading)
                                }
                                when (folder.privacy) {
                                    Privacy.ALL -> binding.radiobuttonFolderSettingAddSetPrivateAll.isChecked =
                                        true
                                    Privacy.ONLY_ME -> binding.radiobuttonFolderSettingAddSetPrivateOnlyMe.isChecked =
                                        true
                                }
                                CoroutineScope(Dispatchers.Main).launch {
                                    val folderThumbnailImage = withContext(Dispatchers.IO) {
                                        loadImageBackground(
                                            requestManager = glideRequestManager,
                                            width = layoutParams.width,
                                            height = 40,
                                            imgName = folder.thumbnailImage
                                        )
                                    }
                                    loadImageView(
                                        requestManager = glideRequestManager,
                                        width = layoutParams.width,
                                        height = 148,
                                        img = folderThumbnailImage,
                                        imgView = binding.ivFolderSettingThumbnail
                                    )
                                }
                            }
                        }
                    }
                }
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
            val name: String = binding.etFolderSettingAddSetTitle.text.toString()
            val subheading: String = binding.etFolderSettingAddSetSubheading.text.toString()
            val privacy: Privacy =
                when (binding.radiogroupFolderSettingAddSetPrivate.checkedRadioButtonId) {
                    binding.radiobuttonFolderSettingAddSetPrivateAll.id -> Privacy.ALL
                    binding.radiobuttonFolderSettingAddSetPrivateOnlyMe.id -> Privacy.ONLY_ME
                    else -> Privacy.ALL
                }

            if (this::imageUri.isInitialized) {
                // ?????? ?????? ????????? ??????
                val thumbnailImg = thumbnailImgBitmap?.let { bitmapToFile(it) }
                folderSettingViewModel.requestEditFolder(
                    args.folderId,
                    name,
                    privacy,
                    subheading,
                    true,
                    thumbnailImg
                )
            } else {
                // ?????? ?????? ????????? ???????????? ??????
                folderSettingViewModel.requestEditFolder(
                    args.folderId,
                    name,
                    privacy,
                    subheading,
                    false,
                    null
                )
            }

            folderSettingViewModel.editSuccess.observe(viewLifecycleOwner) {
                if (it.getContentIfNotHandled() == true) {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setFolderSettingThumbnailOptionClickListener() {
        binding.ivFolderSettingThumbnail.setOnClickListener {
            findNavController().navigate(R.id.action_folderEditFragment_to_folderSettingAddImageOptionFragment)
        }
    }

    private fun observeNavigationFolderSettingImageCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("imageUri")
            ?.observe(viewLifecycleOwner) {
                imageUri = it
                if (this::imageUri.isInitialized) {
                    if (imageUri == "") {
                        glideRequestManager.load(R.drawable.ic_folder_thumbnail_empty).centerCrop()
                            .into(binding.ivFolderSettingThumbnail)
                        thumbnailImgBitmap = null
                    } else {
                        thumbnailImgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(
                                    requireContext().contentResolver,
                                    imageUri.toUri()
                                )
                            )
                        } else {
                            MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                imageUri.toUri()
                            )
                        }
                        glideRequestManager.load(imageUri).into(binding.ivFolderSettingThumbnail)
                    }
                }
            }
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA)
        val fileName =
            imageFileTimeFormat.format(Date(System.currentTimeMillis())).toString() + ".jpg"
        val cacheDir = requireContext().cacheDir.toString()
        val file = File("$cacheDir/$fileName")
        var out: OutputStream? = null
        try {
            file.createNewFile()
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } finally {
            out?.close()
        }
        return file
    }

    private fun verifyFolderName() {
        binding.etFolderSettingAddSetTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when {
                    s.toString().trim().isEmpty() -> {
                        ButtonActivation.setTextViewConfirmButtonInactive(
                            requireContext(),
                            binding.tvFolderSettingAddConfirm
                        )
                    }
                    Pattern.matches("[???-???|???-???|???-???|a-z|A-Z|0-9|\\s]*", s.toString().trim()) -> {
                        ButtonActivation.setTextViewConfirmButtonActive(
                            requireContext(),
                            binding.tvFolderSettingAddConfirm
                        )
                    }
                    else -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.folder_add_message_format_fail),
                            Toast.LENGTH_SHORT
                        ).show()
                        ButtonActivation.setTextViewConfirmButtonInactive(
                            requireContext(),
                            binding.tvFolderSettingAddConfirm
                        )
                    }
                }
            }
        })
    }
}