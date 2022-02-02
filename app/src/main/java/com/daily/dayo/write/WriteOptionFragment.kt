package com.daily.dayo.write

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentWriteOptionBinding
import com.daily.dayo.util.autoCleared
import com.daily.dayo.write.viewmodel.WriteOptionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class WriteOptionFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentWriteOptionBinding>()
    private val args by navArgs<WriteOptionFragmentArgs>()
    private val writeOptionViewModel by activityViewModels<WriteOptionViewModel>()

    private val postCategory by lazy {args.postCategory}
    private val postContents by lazy {args.postContents}
    private val postTagList by lazy {args.postTagList}

    private val postImagesStringList by lazy {args.postImages}
    private var postImageFileList = ArrayList<File>()
    private val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteOptionBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { setUploadImageFileList() }
        setUploadButtonClickListener()
        setOptionTagListOriginalValue()
        setOptionTagClickListener()
        setOptionFolderClickListener()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUploadImageFileList() {
        postImagesStringList.forEach { postImageString ->
            val postImageBitmap = postImageString.toBitmap()
            postImageFileList.add(bitmapToFile(postImageBitmap, setUploadImagePath()))
        }
    }

    private fun setUploadButtonClickListener() {
        binding.btnWriteOptionConfirm.setOnClickListener {
            val privacy = setPrivacySetting()
            // TODO : folderId를 얻는 과정 작성 필요
            val memberId = SharedManager(DayoApplication.applicationContext()).getCurrentUser().memberId.toString()
            val folderId = binding.tvWriteOptionDescriptionFolder.id
            writeOptionViewModel.requestUploadPost(postCategory, postContents, postImageFileList.toTypedArray(),
                folderId, memberId, privacy, postTagList)
            Toast.makeText(requireContext(), R.string.write_post_upload_alert_message_loading, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun setPrivacySetting() : String {
        val radioButton = binding.radiogroupWriteOptionDescriptionPrivate?.findViewById<View>(binding.radiogroupWriteOptionDescriptionPrivate!!.checkedRadioButtonId)
        val radioId = binding.radiogroupWriteOptionDescriptionPrivate!!.indexOfChild(radioButton)
        val btn = binding.radiogroupWriteOptionDescriptionPrivate!!.getChildAt(radioId) as RadioButton
        return when(btn.text as String) {
            getString(R.string.privacy_all) -> getString(R.string.privacy_all_eng)
            getString(R.string.privacy_following) -> getString(R.string.privacy_following_eng)
            getString(R.string.privacy_private) -> getString(R.string.privacy_private_eng)
            else -> getString(R.string.privacy_private_eng)
        }
    }

    private fun setOptionTagListOriginalValue() {
        if(!postTagList.isNullOrEmpty()){
                binding.tvWriteOptionDescriptionTag.visibility = View.GONE
            (0 until postTagList.size).mapNotNull { index ->
                val chip = LayoutInflater.from(context).inflate(R.layout.item_write_post_tag_chip, null) as Chip
                val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
                with(chip) {
                    setTextAppearance(R.style.WritePostTagTextStyle)
                    isCloseIconVisible = false
                    isCheckable = false
                    ensureAccessibleTouchTarget(42.toPx())
                    text = "# ${postTagList[index].trim()}"
                }
                binding.chipgroupWriteOptionTagListSaved.addView(chip, layoutParams)
            }
        } else {
            binding.tvWriteOptionDescriptionTag.visibility = View.VISIBLE
        }
    }
    private fun setOptionTagClickListener() {
        binding.layoutWriteOptionTag.setOnClickListener {
            if(postTagList.isNullOrEmpty()) {
                val navigateWithDataPassAction = WriteOptionFragmentDirections.actionWriteOptionFragmentToWriteTagFragment(emptyArray())
                findNavController().navigate(navigateWithDataPassAction)
            } else {
                val navigateWithDataPassAction = WriteOptionFragmentDirections.actionWriteOptionFragmentToWriteTagFragment(postTagList)
                findNavController().navigate(navigateWithDataPassAction)
            }
        }
    }

    private fun setOptionFolderClickListener() {
        binding.layoutWriteOptionFolder.setOnClickListener {
            findNavController().navigate(R.id.action_writeOptionFragment_to_writeFolderFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.toBitmap(): Bitmap {
        Base64.getDecoder().decode(this).apply {
            return BitmapFactory.decodeByteArray(this, 0, size)
        }
    }

    private fun bitmapToFile(bitmap: Bitmap?, path: String?): File {
        var file = File(path)
        var out: OutputStream? = null
        try { file.createNewFile()
            out = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } finally {
            out?.close()
        }
        return file
    }

    private fun setUploadImagePath() : String {
        // uri를 통하여 불러온 이미지를 임시로 파일로 저장할 경로로 앱 내부 캐시 디렉토리로 설정,
        // 파일 이름은 불러온 시간 사용
        val fileName = imageFileTimeFormat.format(Date(System.currentTimeMillis())).toString() + ".jpg"
        val cacheDir = requireContext().cacheDir.toString()
        return "$cacheDir/$fileName"
    }

    fun Int.toPx() : Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}