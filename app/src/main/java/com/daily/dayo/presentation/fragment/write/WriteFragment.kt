package com.daily.dayo.presentation.fragment.write

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daily.dayo.R
import com.daily.dayo.common.Event
import com.daily.dayo.common.GlideApp
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.ImageResizeUtil.resizeBitmap
import com.daily.dayo.common.RadioGridGroup
import com.daily.dayo.common.ReplaceUnicode
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentWriteBinding
import com.daily.dayo.domain.model.Category
import com.daily.dayo.presentation.adapter.WriteUploadImageListAdapter
import com.daily.dayo.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

@AndroidEntryPoint
class WriteFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private val args by navArgs<WriteFragmentArgs>()
    private var postImageBitmapToStringList = ArrayList<String>() // Bitmap을 String으로 변환
    private lateinit var uploadImageListAdapter: WriteUploadImageListAdapter
    private lateinit var glideRequestManager: RequestManager

    private var isCategorySelected: Boolean = false
    private var isImageUploaded: Boolean = false
    private var isLoadedEditPost: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteBinding.inflate(inflater, container, false)
        initWritingContents()

        setPostImageListAdapter()
        setBackButtonClickListener()
        setEditTextCountLimit()
        setImageUploadButtonClickListener()
        setImageDeleteClickListener()
        setCategoryClickListener()
        setUploadButtonActivation()
        observeUploadStateCallBack()
        showOptionDialog()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                writeViewModel.setInitWriteInfoValue()
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setBackButtonClickListener() {
        binding.btnWritePostBack.setOnClickListener {
            writeViewModel.setInitWriteInfoValue()
            findNavController().navigateUp()
        }
    }

    private fun showOptionDialog() {
        writeViewModel.showWriteOptionDialog.observe(viewLifecycleOwner, Observer {
            if (it.getContentIfNotHandled() == true) {
                writeViewModel.showWriteOptionDialog.value = Event(false)
                findNavController().navigate(WriteFragmentDirections.actionWriteFragmentToWriteOptionFragment())
            }
        })
    }

    private fun setEditTextCountLimit() {
        val lengthFilter = InputFilter.LengthFilter(200)
        with(binding.etWriteDetail) {
            filters = arrayOf(lengthFilter)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    binding.editTextCount = s.toString().length
                    if (s.toString().length > 200) {
                        Toast.makeText(
                            requireContext(),
                            R.string.write_post_upload_alert_message_edittext_length_fail_max,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        setUploadButtonActivation()
                    }
                }
            })
        }
    }

    private fun initWritingContents() {
        // TODO : 이미지 추가 버튼 GONE처리가 풀리는 현상 임시 해결
        if (args.postId != 0) {
            binding.btnUploadImage.visibility = View.GONE
        }
        // 기존 글 수정하는 경우
        if (args.postId != 0 && !isLoadedEditPost) {
            isLoadedEditPost = true
            binding.btnUploadImage.visibility = View.GONE // 새로운 이미지 추가 불가능 처리
            writeViewModel.requestPostDetail(args.postId).invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Getting Post", "CANCELLED")
                    null -> {
                        writeViewModel.getCurrentPostSuccess.observe(
                            viewLifecycleOwner,
                            Observer { isSuccess ->
                                if (isSuccess.getContentIfNotHandled() == true) {
                                    isCategorySelected = true
                                    isImageUploaded = true

                                    writeViewModel.writeCurrentPostDetail.value?.getContentIfNotHandled()
                                        ?.let { postDetail ->
                                            when (postDetail.category) {
                                                Category.SCHEDULER -> binding.radiobuttonWritePostCategoryScheduler.isChecked =
                                                    true
                                                Category.STUDY_PLANNER -> binding.radiobuttonWritePostCategoryStudyplanner.isChecked =
                                                    true
                                                Category.GOOD_NOTE -> binding.radiobuttonWritePostCategoryGoodnote.isChecked =
                                                    true
                                                Category.POCKET_BOOK -> binding.radiobuttonWritePostCategoryPocketbook.isChecked =
                                                    true
                                                Category.SIX_DIARY -> binding.radiobuttonWritePostCategorySixHoleDiary.isChecked =
                                                    true
                                                Category.ETC -> binding.radiobuttonWritePostCategoryEtc.isChecked =
                                                    true
                                            }

                                            binding.etWriteDetail.setText(postDetail.contents)

                                            for (element in postDetail.postImages!!) {
                                                writeViewModel.postImageUriList.add(
                                                    Uri.parse("http://117.17.198.45:8080/images/$element")
                                                        .toString()
                                                )
                                                lateinit var imageBitmap: Bitmap
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val uploadImage = loadImageBackground(
                                                        requestManager = glideRequestManager,
                                                        width = 480,
                                                        height = 480,
                                                        imgName = element
                                                    )
                                                    imageBitmap = resizeBitmap(uploadImage)
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        postImageBitmapToStringList.add(
                                                            imageBitmap.toBase64String()
                                                        )
                                                    }
                                                }
                                            }
                                            uploadImageListAdapter.notifyDataSetChanged()

                                            postDetail.hashtags?.let {
                                                writeViewModel.postTagList.addAll(
                                                    it
                                                )
                                            }
                                            writeViewModel.postFolderId.value =
                                                postDetail.folderId.toString()
                                            writeViewModel.postFolderName.value =
                                                postDetail.folderName
                                        }
                                }
                            })
                    }
                }
            }
        }
    }

    private fun setCategoryClickListener() {
        binding.radiogroupWritePostCategory.setOnCheckedChangeListener(object :
            RadioGridGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGridGroup?, checkedId: Int) {
                isCategorySelected = true
                setUploadButtonActivation()
                setCheckedCategoryName()
            }
        })
    }

    private fun setCheckedCategoryName() {
        writeViewModel.postCategory.value =
            when (binding.radiogroupWritePostCategory.checkedCheckableImageButtonId) {
                R.id.radiobutton_write_post_category_scheduler -> Category.SCHEDULER
                R.id.radiobutton_write_post_category_studyplanner -> Category.STUDY_PLANNER
                R.id.radiobutton_write_post_category_goodnote -> Category.GOOD_NOTE
                R.id.radiobutton_write_post_category_pocketbook -> Category.POCKET_BOOK
                R.id.radiobutton_write_post_category_sixHoleDiary -> Category.SIX_DIARY
                R.id.radiobutton_write_post_category_etc -> Category.ETC
                else -> null
            }
    }

    private fun setImageUploadButtonClickListener() {
        val btnUploadImage = binding.btnUploadImage
        btnUploadImage.setOnClickListener {
            findNavController().navigate(R.id.action_writeFragment_to_writeImageOptionFragment)
        }
    }

    private fun setPostImageListAdapter() {
        writeViewModel.postImageUriList.observe(viewLifecycleOwner) {
            uploadImageListAdapter =
                WriteUploadImageListAdapter(it, requestManager = glideRequestManager, args.postId)
            val rvUploadImageList = binding.rvImgUploadList
            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvUploadImageList.layoutManager = layoutManager
            rvUploadImageList.adapter = uploadImageListAdapter
            // 새로 작성하는 글
            if (args.postId == 0) {
                postImageBitmapToStringList.clear()
                for (element in it) {
                    var imageBitmap = element.toBitmap()
                    imageBitmap = resizeBitmap(imageBitmap)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        postImageBitmapToStringList.add(imageBitmap.toBase64String())
                    }
                    if (postImageBitmapToStringList.size >= 5) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.write_post_upload_alert_message_image_fail_max),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnUploadImage.visibility = View.GONE
                        break
                    }
                    if (postImageBitmapToStringList.size != 0) {
                        isImageUploaded = true
                        setUploadButtonActivation()
                    }
                }
            }
        }
    }

    private fun setImageDeleteClickListener() {
        writeViewModel.postImageUriList.observe(viewLifecycleOwner) {
            uploadImageListAdapter.setOnItemClickListener(object :
                WriteUploadImageListAdapter.OnItemClickListener {
                override fun deleteUploadImageClick(pos: Int) {
                    it.removeAt(pos)
                    postImageBitmapToStringList.removeAt(pos)
                    uploadImageListAdapter.notifyDataSetChanged()
                    binding.btnUploadImage.visibility = View.VISIBLE
                    if (it.isNullOrEmpty()) {
                        isImageUploaded = false
                        setUploadButtonActivation()
                    }
                }
            })
        }
    }

    private fun setUploadButtonActivation() {
        if (!isCategorySelected) {
            binding.btnWritePostUpload.isSelected = false
            binding.btnWritePostUpload.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    R.string.write_post_upload_alert_message_empty_category,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (!isImageUploaded) {
            binding.btnWritePostUpload.isSelected = false
            binding.btnWritePostUpload.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    R.string.write_post_upload_alert_message_empty_image,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val selectedCategoryName = writeViewModel.postCategory.value
            binding.btnWritePostUpload.isSelected = true
            binding.btnWritePostUpload.setOnClickListener {
                writeViewModel.postId.value = args.postId
                writeViewModel.postCategory.value = selectedCategoryName
                writeViewModel.postContents.value =
                    ReplaceUnicode.trimBlankText(binding.etWriteDetail.text.toString())
                        .ifEmpty { "" }
                findNavController().navigate(WriteFragmentDirections.actionWriteFragmentToWriteOptionFragment())
            }
        }
    }

    private fun observeUploadStateCallBack() {
        writeViewModel.writeSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                writeViewModel.writePostId.value?.getContentIfNotHandled()?.let { writePostId ->
                    findNavController().navigate(
                        WriteFragmentDirections.actionWriteFragmentToPostFragment
                            (writePostId)
                    )
                }
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(
                    requireContext(),
                    R.string.write_post_upload_alert_message_fail,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        writeViewModel.writeEditSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                writeViewModel.writePostId.value?.getContentIfNotHandled()?.let { writePostId ->
                    findNavController().navigate(
                        WriteFragmentDirections.actionWriteFragmentToPostFragment
                            (writePostId)
                    )
                }
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(
                    requireContext(),
                    R.string.write_post_upload_alert_message_fail,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Bitmap.toBase64String(): String {
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG, 100, this)
            return Base64.getEncoder().encodeToString(toByteArray())
        }
    }

    fun String.toBitmap(): Bitmap {
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
}