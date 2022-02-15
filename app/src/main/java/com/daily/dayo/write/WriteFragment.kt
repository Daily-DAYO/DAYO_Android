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
import androidx.annotation.RequiresApi
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
import com.daily.dayo.util.autoCleared
import com.daily.dayo.util.getNavigationResult
import com.daily.dayo.write.adapter.WriteUploadImageListAdapter
import com.daily.dayo.write.viewmodel.WriteOptionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import java.io.ByteArrayOutputStream
import java.util.*

@AndroidEntryPoint
class WriteFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteBinding>()
    private val writeOptionViewModel by activityViewModels<WriteOptionViewModel>()
    private val args by navArgs<WriteFragmentArgs>()

    private var radioGroupCategoryLine1: RadioGroup? = null
    private var radioGroupCategoryLine2: RadioGroup? = null
    private lateinit var postTagList : List<String> // 현재 작성할 게시글에 저장된 태그 리스트
    private var uploadImageList = ArrayList<Uri>() // 갤러리에서 불러온 이미지 리스트
    private var uploadImageListString = ArrayList<String>() // Bitmap을 String으로 변환
    private lateinit var uploadImageListAdapter : WriteUploadImageListAdapter

    private var isCategorySelected : Boolean = false
    private var isContentsFilled :Boolean= false
    private var isImageUploaded : Boolean= false

    private var postFolderId:String = ""
    private var postFolderName:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWritingContents()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentWriteBinding.inflate(inflater, container, false)
        observeNavigationTagListCallBack()
        setRadioButtonGrouping()
        setBackButtonClickListener()
        setEditTextCountLimit()
        setImageUploadButtonClickListener()
        observeNavigationWritingImageCallBack()
        setImageDeleteClickListener()
        setUploadButtonActivation()
        observeUploadStateCallBack()
        observeNavigationFolderCallBack()
        return binding.root
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

    private fun setBackButtonClickListener() {
        binding.btnWritePostBack.setOnClickListener {
            findNavController().navigateUp()
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
                    } else if(s.toString().length == 0){
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
        if(args.postId != 0) {
            writeOptionViewModel.requestPostDetail(args.postId).invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Getting Post", "CANCELLED")
                    null -> {
                        writeOptionViewModel.getCurrentPostSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
                            if(isSuccess.getContentIfNotHandled() == true) {
                                isCategorySelected = true
                                isContentsFilled = true
                                isImageUploaded = true

                                writeOptionViewModel.writeCurrentPostDetail.value?.getContentIfNotHandled()?.let { postData ->
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
                                        uploadImageList.add(Uri.parse("http://117.17.198.45:8080/images/$element"))
                                        lateinit var imageBitmap : Bitmap
                                        Glide.with(requireContext())
                                            .asBitmap()
                                            .load("http://117.17.198.45:8080/images/$element")
                                            .into(object: CustomTarget<Bitmap>() {
                                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                                    imageBitmap = resource
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        uploadImageListString.add(imageBitmap.toBase64String())
                                                    }
                                                }
                                                override fun onLoadCleared(placeholder: Drawable?) { }
                                            })
                                    }
                                    uploadImageListAdapter.notifyDataSetChanged()
                                    postTagList = postData.hashtags
                                    // TODO : 폴더 및 공개설정 처리 여부 결
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

    private fun observeNavigationTagListCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<List<String>>("postTagList")?.observe(viewLifecycleOwner) {
            postTagList = it
        }
    }

    private fun observeNavigationFolderCallBack(){
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("postFolderId")?.observe(viewLifecycleOwner){
            postFolderId = it
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("postFolderName")?.observe(viewLifecycleOwner){
            postFolderName = it
        }
    }

    private fun setImageUploadButtonClickListener() {
        val btnUploadImage = binding.btnUploadImage
        val rvUploadImageList = binding.rvImgUploadList
        uploadImageListAdapter = WriteUploadImageListAdapter(uploadImageList, requireContext())

        btnUploadImage.setOnClickListener {
            findNavController().navigate(R.id.action_writeFragment_to_writeImageOptionFragment)
        }
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvUploadImageList.layoutManager = layoutManager
        rvUploadImageList.adapter = uploadImageListAdapter
    }
    private fun observeNavigationWritingImageCallBack() {
        getNavigationResult<List<Uri>>(R.id.WriteFragment, "userWritingPostImageUri") {
            for(element in it) {
                uploadImageList.add(element)
                val imageBitmap = element.toBitmap()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    uploadImageListString.add(imageBitmap.toBase64String())
                }
                if(uploadImageList.size >= 5) {
                    Toast.makeText(requireContext(), getString(R.string.write_post_upload_alert_message_image_fail_max), Toast.LENGTH_SHORT).show()
                    binding.btnUploadImage.visibility = View.GONE
                    break
                }
                if(uploadImageList.size != 0) {
                    isImageUploaded = true
                    setUploadButtonActivation()
                }
            }
            uploadImageListAdapter.notifyDataSetChanged()
        }
    }

    private fun setImageDeleteClickListener() {
        uploadImageListAdapter.setOnItemClickListener(object : WriteUploadImageListAdapter.OnItemClickListener{
            override fun deleteUploadImageClick(pos: Int) {
                uploadImageList.removeAt(pos)
                uploadImageListString.removeAt(pos)
                uploadImageListAdapter.notifyDataSetChanged()
                binding.btnUploadImage.visibility = View.VISIBLE
                if (uploadImageList.size == 0) {
                    isImageUploaded = false
                    setUploadButtonActivation()
                }
            }
        })
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
                if (this::postTagList.isInitialized) {
                    val navigateWithDataPassAction =
                        WriteFragmentDirections.actionWriteFragmentToWriteOptionFragment(selectedCategoryName, binding.etWriteDetail.text.toString(), uploadImageListString.toTypedArray(), postTagList.toTypedArray(), postFolderId, postFolderName)
                    findNavController().navigate(navigateWithDataPassAction)
                } else {
                    val navigateWithDataPassAction =
                        WriteFragmentDirections.actionWriteFragmentToWriteOptionFragment(selectedCategoryName, binding.etWriteDetail.text.toString(), uploadImageListString.toTypedArray(), emptyArray(), postFolderId, postFolderName)
                    findNavController().navigate(navigateWithDataPassAction)
                }
            }
        }
    }

    private fun observeUploadStateCallBack() {
        writeOptionViewModel.writeSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if(isSuccess.getContentIfNotHandled() == true) {
                writeOptionViewModel.writePostId.value?.getContentIfNotHandled()?.let { writePostId ->
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

    fun Uri.toBitmap(): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, this) )
        } else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, this)
        }
    }
}