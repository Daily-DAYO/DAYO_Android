package com.daily.dayo.presentation.fragment.write

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentWriteOptionBinding
import com.daily.dayo.presentation.viewmodel.WriteViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class WriteOptionFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentWriteOptionBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private val postImageFileList = ArrayList<File>()
    private val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss-SSS", Locale.KOREA)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteOptionBinding.inflate(inflater, container, false)
        setUploadButtonClickListener()
        setOptionTagListOriginalValue()
        setOptionTagClickListener()
        setOptionFolderClickListener()
        setFolderDescription()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUploadImageFileList() {
        writeViewModel.postImageUriList.observe(viewLifecycleOwner) {
            postImageFileList.clear()
            it.forEach { item ->
                val postImageBitmap = item.toBitmap()
                postImageFileList.add(bitmapToFile(postImageBitmap, setUploadImagePath()))
            }
        }
    }

    private fun setUploadButtonClickListener() {
        binding.btnWriteOptionConfirm.setOnClickListener {
            when {
                writeViewModel.postFolderId.value == "" -> { // ?????? ???????????? ??? ????????? ??????
                    Toast.makeText(requireContext(), "????????? ????????? ?????????", Toast.LENGTH_SHORT)
                        .show()
                }
                writeViewModel.postId.value != 0 -> { // ?????? ???????????? ???????????? ??????
                    writeViewModel.requestEditPost(
                        writeViewModel.postId.value!!,
                        writeViewModel.postCategory.value!!,
                        writeViewModel.postContents.value!!,
                        writeViewModel.postFolderId.value!!.toInt(),
                        writeViewModel.postTagList.value!!.toTypedArray()
                    )
                    Toast.makeText(
                        requireContext(),
                        R.string.write_post_upload_alert_message_loading,
                        Toast.LENGTH_SHORT
                    ).show()
                    writeViewModel.setInitWriteInfoValue()
                    findNavController().navigateUp()
                }
                else -> { // ?????? ?????? ???????????? ??????
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        setUploadImageFileList()
                    }
                    writeViewModel.requestUploadPost(
                        writeViewModel.postCategory.value!!,
                        writeViewModel.postContents.value!!,
                        postImageFileList.toTypedArray(),
                        writeViewModel.postFolderId.value!!.toInt(),
                        writeViewModel.postTagList.value!!.toTypedArray()
                    )
                    Toast.makeText(
                        requireContext(),
                        R.string.write_post_upload_alert_message_loading,
                        Toast.LENGTH_SHORT
                    ).show()
                    writeViewModel.setInitWriteInfoValue()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setFolderDescription() {
        writeViewModel.postFolderId.observe(viewLifecycleOwner) {
            if (it != "") {
                binding.tvWriteOptionDescriptionFolder.text = writeViewModel.postFolderName.value
            }
        }
    }

    private fun setOptionTagListOriginalValue() {
        writeViewModel.postTagList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.tvWriteOptionDescriptionTag.visibility = View.GONE
                (it.indices).mapNotNull { index ->
                    val chip = LayoutInflater.from(context)
                        .inflate(R.layout.item_write_post_tag_chip, null) as Chip
                    val layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT
                    )
                    with(chip) {
                        setTextAppearance(R.style.WritePostTagTextStyle)
                        isCloseIconVisible = false
                        isCheckable = false
                        ensureAccessibleTouchTarget(42.toPx())
                        text = "# ${it[index].trim()}"
                    }
                    binding.chipgroupWriteOptionTagListSaved.addView(chip, layoutParams)
                }
            } else {
                binding.tvWriteOptionDescriptionTag.visibility = View.VISIBLE
            }
        }
    }

    private fun setOptionTagClickListener() {
        binding.layoutWriteOptionTag.setOnClickListener {
            if (writeViewModel.postTagList.value.isNullOrEmpty()) {
                val navigateWithDataPassAction =
                    WriteOptionFragmentDirections.actionWriteOptionFragmentToWriteTagFragment()
                findNavController().navigate(navigateWithDataPassAction)
            } else {
                val navigateWithDataPassAction =
                    WriteOptionFragmentDirections.actionWriteOptionFragmentToWriteTagFragment()
                findNavController().navigate(navigateWithDataPassAction)
            }
        }
    }

    private fun setOptionFolderClickListener() {
        binding.layoutWriteOptionFolder.setOnClickListener {
            findNavController().navigate(WriteOptionFragmentDirections.actionWriteOptionFragmentToWriteFolderFragment())
        }
    }

    private fun String.toBitmap(): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    requireContext().contentResolver,
                    this.toUri()
                )
            )
        } else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, this.toUri())
        }
    }

    private fun bitmapToFile(bitmap: Bitmap?, path: String): File {
        val file = File(path)
        var out: OutputStream? = null
        try {
            file.createNewFile()
            out = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } finally {
            out?.close()
        }
        return file
    }

    private fun setUploadImagePath(): String {
        // uri??? ????????? ????????? ???????????? ????????? ????????? ????????? ????????? ??? ?????? ?????? ??????????????? ??????,
        // ?????? ????????? ????????? ?????? ??????
        val fileName =
            imageFileTimeFormat.format(Date(System.currentTimeMillis())).toString() + ".jpg"
        val cacheDir = requireContext().cacheDir.toString()
        return "$cacheDir/$fileName"
    }

    fun Int.toPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}