package com.daily.dayo.write

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
import android.widget.RadioButton
import android.widget.RadioGroup
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
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentWriteBinding
import com.daily.dayo.util.Event
import com.daily.dayo.util.autoCleared
import com.daily.dayo.write.adapter.WriteUploadImageListAdapter
import com.daily.dayo.write.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import java.io.ByteArrayOutputStream
import java.util.*

@AndroidEntryPoint
class WriteFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private val args by navArgs<WriteFragmentArgs>()

    private var radioGroupCategoryLine1: RadioGroup? = null
    private var radioGroupCategoryLine2: RadioGroup? = null
    private var postImageBitmapToStringList = ArrayList<String>() // Bitmap을 String으로 변환
    private lateinit var uploadImageListAdapter : WriteUploadImageListAdapter

    private var isCategorySelected : Boolean = false
    private var isContentsFilled :Boolean= false
    private var isImageUploaded : Boolean= false
    private var isLoadedEditPost: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?)
    : View? {
        binding = FragmentWriteBinding.inflate(inflater, container, false)
        initWritingContents()
        setPostImageListAdapter()
        setRadioButtonGrouping()
        setBackButtonClickListener()
        setEditTextCountLimit()
        setImageUploadButtonClickListener()
        setImageDeleteClickListener()
        setUploadButtonActivation()
        observeUploadStateCallBack()
        showOptionDialog()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun showOptionDialog(){
        writeViewModel.showWriteOptionDialog.observe(viewLifecycleOwner, Observer {
            if(it.getContentIfNotHandled() == true) {
                writeViewModel.showWriteOptionDialog.value = Event(false)
                findNavController().navigate(WriteFragmentDirections.actionWriteFragmentToWriteOptionFragment())
            }
        })
    }

    private fun setRadioButtonGrouping() {
        radioGroupCategoryLine1 = binding.radiogroupWritePostCategoryLine1
        radioGroupCategoryLine2 = binding.radiogroupWritePostCategoryLine2
        radioGroupCategoryLine1?.let {
            it.clearCheck()
            it.setOnCheckedChangeListener(listener1)
        }
        radioGroupCategoryLine2?.let {
            it.clearCheck()
            it.setOnCheckedChangeListener(listener2)
        }
    }

    private fun setEditTextCountLimit() {
        val lengthFilter = InputFilter.LengthFilter(200)
        with(binding.etWriteDetail) {
            filters = arrayOf(lengthFilter)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    binding.editTextCount = s.toString().length
                    if(s.toString().length > 200) {
                        Toast.makeText(requireContext(), R.string.write_post_upload_alert_message_edittext_length_fail_max, Toast.LENGTH_SHORT).show()
                    } else if(s.toString().isEmpty()){
                        isContentsFilled = false
                        setUploadButtonActivation()
                    } else {
                        isContentsFilled = true
                        setUploadButtonActivation()
                    }
                }
            })
        }
    }

    private fun initWritingContents() {
        // TODO : 이미지 추가 버튼 GONE처리가 풀리는 현상 임시 해결
        if(args.postId != 0) {
            binding.btnUploadImage.visibility = View.GONE
        }
        // 기존 글 수정하는 경우
        if(args.postId != 0 && !isLoadedEditPost) {
            isLoadedEditPost = true
            binding.btnUploadImage.visibility = View.GONE // 새로운 이미지 추가 불가능 처리
            writeViewModel.requestPostDetail(args.postId).invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Getting Post", "CANCELLED")
                    null -> {
                        writeViewModel.getCurrentPostSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
                            if(isSuccess.getContentIfNotHandled() == true) {
                                isCategorySelected = true
                                isContentsFilled = true
                                isImageUploaded = true

                                writeViewModel.writeCurrentPostDetail.value?.getContentIfNotHandled()?.let { postData ->
                                    when(postData.category) {
                                        getString(R.string.scheduler_eng) -> binding.radiobuttonWritePostCategoryScheduler.isChecked = true
                                        getString(R.string.studyplanner_eng) -> binding.radiobuttonWritePostCategoryStudyplanner.isChecked = true
                                        getString(R.string.digital_eng) -> binding.radiobuttonWritePostCategoryDigital.isChecked = true
                                        getString(R.string.pocketbook_eng) -> binding.radiobuttonWritePostCategoryPocketbook.isChecked = true
                                        getString(R.string.sixHoleDiary_eng) -> binding.radiobuttonWritePostCategorySixHoleDiary.isChecked = true
                                        getString(R.string.etc_eng) -> binding.radiobuttonWritePostCategoryEtc.isChecked = true
                                    }

                                    binding.etWriteDetail.setText(postData.contents)

                                    for(element in postData.images) {
                                        writeViewModel.postImageUriList.add(Uri.parse("http://117.17.198.45:8080/images/$element").toString())
                                        lateinit var imageBitmap : Bitmap
                                        Glide.with(requireContext())
                                            .asBitmap()
                                            .load("http://117.17.198.45:8080/images/$element")
                                            .into(object: CustomTarget<Bitmap>() {
                                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                                    imageBitmap = resource
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        postImageBitmapToStringList.add(imageBitmap.toBase64String())
                                                    }
                                                }
                                                override fun onLoadCleared(placeholder: Drawable?) { }
                                            })
                                    }
                                    uploadImageListAdapter.notifyDataSetChanged()

                                    writeViewModel.postTagList.addAll(postData.hashtags)
                                    writeViewModel.postFolderId.value = postData.folderId.toString()
                                    writeViewModel.postFolderName.value = postData.folderName
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    private fun setCategoryName() : String {
        var selectedCategoryName = ""
        if (radioGroupCategoryLine1!!.checkedRadioButtonId > 0) {
            val radioButton = radioGroupCategoryLine1?.findViewById<View>(radioGroupCategoryLine1!!.checkedRadioButtonId)
            val radioId = radioGroupCategoryLine1!!.indexOfChild(radioButton)
            val btn = radioGroupCategoryLine1!!.getChildAt(radioId) as RadioButton
            selectedCategoryName = btn.text as String
        } else if (radioGroupCategoryLine2!!.checkedRadioButtonId > 0) {
            val radioButton = radioGroupCategoryLine2?.findViewById<View>(radioGroupCategoryLine2!!.checkedRadioButtonId)
            val radioId = radioGroupCategoryLine2!!.indexOfChild(radioButton)
            val btn = radioGroupCategoryLine2!!.getChildAt(radioId) as RadioButton
            selectedCategoryName = btn.text as String
        }
        return when(selectedCategoryName) {
            getString(R.string.scheduler) -> getString(R.string.scheduler_eng)
            getString(R.string.studyplanner) -> getString(R.string.studyplanner_eng)
            getString(R.string.digital) -> getString(R.string.digital_eng)
            getString(R.string.pocketbook) -> getString(R.string.pocketbook_eng)
            getString(R.string.sixHoleDiary) -> getString(R.string.sixHoleDiary_eng)
            getString(R.string.etc) -> getString(R.string.etc_eng)
            else -> ""
        }
    }

    private val listener1: RadioGroup.OnCheckedChangeListener = object : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            isCategorySelected = true
            setUploadButtonActivation()
            if (checkedId != -1) {
                radioGroupCategoryLine2!!.apply {
                    setOnCheckedChangeListener(null)
                    clearCheck()
                    setOnCheckedChangeListener(listener2)
                }
            }
        }
    }
    private val listener2: RadioGroup.OnCheckedChangeListener = object : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            isCategorySelected = true
            setUploadButtonActivation()
            if (checkedId != -1) {
                radioGroupCategoryLine1!!.apply {
                    setOnCheckedChangeListener(null)
                    clearCheck()
                    setOnCheckedChangeListener(listener1)
                }
            }
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
            uploadImageListAdapter = WriteUploadImageListAdapter(it, requireContext(), args.postId)
            val rvUploadImageList = binding.rvImgUploadList
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvUploadImageList.layoutManager = layoutManager
            rvUploadImageList.adapter = uploadImageListAdapter
            // 새로 작성하는 글
            if(args.postId == 0) {
                postImageBitmapToStringList.clear()
                for (element in it) {
                    val imageBitmap = element.toBitmap()
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
                Toast.makeText(requireContext(), R.string.write_post_upload_alert_message_empty_category, Toast.LENGTH_SHORT).show()
            }
        } else if (!isContentsFilled) {
            binding.btnWritePostUpload.isSelected = false
            binding.btnWritePostUpload.setOnClickListener {
                Toast.makeText(requireContext(), R.string.write_post_upload_alert_message_empty_content, Toast.LENGTH_SHORT).show()
            }
        } else if (!isImageUploaded) {
            binding.btnWritePostUpload.isSelected = false
            binding.btnWritePostUpload.setOnClickListener {
                Toast.makeText(requireContext(), R.string.write_post_upload_alert_message_empty_image, Toast.LENGTH_SHORT).show()
            }
        } else {
            var selectedCategoryName = setCategoryName()
            binding.btnWritePostUpload.isSelected = true
            binding.btnWritePostUpload.setOnClickListener {
                writeViewModel.postId.value = args.postId
                writeViewModel.postCategory.value = selectedCategoryName
                writeViewModel.postContents.value = binding.etWriteDetail.text.toString()
                writeViewModel.postImageUriList.observe(viewLifecycleOwner) {
                    Log.e("dayo", "write upload:${it}")
                }
                findNavController().navigate(WriteFragmentDirections.actionWriteFragmentToWriteOptionFragment())
            }
        }
    }

    private fun observeUploadStateCallBack() {
        writeViewModel.writeSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if(isSuccess.getContentIfNotHandled() == true) {
                writeViewModel.writePostId.value?.getContentIfNotHandled()?.let { writePostId ->
                    findNavController().navigate(WriteFragmentDirections.actionWriteFragmentToPostFragment
                        (writePostId, SharedManager(DayoApplication.applicationContext()).getCurrentUser().nickname.toString()))
                }
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(requireContext(), R.string.write_post_upload_alert_message_fail, Toast.LENGTH_SHORT).show()
            }
        })
        writeViewModel.writeEditSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if(isSuccess.getContentIfNotHandled() == true) {
                writeViewModel.writePostId.value?.getContentIfNotHandled()?.let { writePostId ->
                    findNavController().navigate(WriteFragmentDirections.actionWriteFragmentToPostFragment
                        (writePostId, SharedManager(DayoApplication.applicationContext()).getCurrentUser().nickname.toString()))
                }
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(requireContext(), R.string.write_post_upload_alert_message_fail, Toast.LENGTH_SHORT).show()
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
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, this.toUri()) )
        } else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, this.toUri())
        }
    }
}