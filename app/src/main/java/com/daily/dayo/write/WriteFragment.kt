package com.daily.dayo.write

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import com.daily.dayo.databinding.FragmentWriteBinding
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isEmpty
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.R
import com.daily.dayo.util.DefaultDialogAlert
import com.daily.dayo.util.DefaultDialogConfigure
import com.daily.dayo.util.autoCleared
import com.daily.dayo.write.adapter.WriteUploadImageListAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class WriteFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteBinding>()
    private var radioGroupCategoryLine1: RadioGroup? = null
    private var radioGroupCategoryLine2: RadioGroup? = null
    private lateinit var postTagList : List<String> // 현재 작성할 게시글에 저장된 태그 리스트
    private var uploadImageList = ArrayList<Uri>() // 갤러리에서 불러온 이미지 리스트
    private var uploadImageListString = ArrayList<String>() // Bitmap을 String으로 변환
    private lateinit var uploadImageListAdapter : WriteUploadImageListAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentWriteBinding.inflate(inflater, container, false)
        observeNavigationTagListCallBack()
        setRadioButtonGrouping()
        setBackButtonClickListener()
        setUploadButtonClickListener()
        setImageUploadButtonClickListener()
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

    private fun setUploadButtonClickListener() {
        binding.btnWritePostUpload.setOnClickListener {
            val selectedCategoryName = setCategoryName()
            if(binding.etWriteDetail.text.isNullOrBlank()) {
                val mAlertDialog = DefaultDialogAlert.createDialog(requireContext(), R.string.write_post_upload_alert_message_empty_content)
                if(!mAlertDialog.isShowing) {
                    mAlertDialog.show()
                    DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.5f)
                }
            } else if(binding.rvImgUploadList.isEmpty()) {
                var mAlertDialog = DefaultDialogAlert.createDialog(requireContext(), R.string.write_post_upload_alert_message_empty_image)
                if(!mAlertDialog.isShowing) {
                    mAlertDialog.show()
                    DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.5f)
                }
            } else if(selectedCategoryName.toString() == "") {
                var mAlertDialog = DefaultDialogAlert.createDialog(requireContext(), R.string.write_post_upload_alert_message_empty_category)
                if(!mAlertDialog.isShowing) {
                    mAlertDialog.show()
                    DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.5f)
                }
            } else {
                if(this::postTagList.isInitialized){
                    val navigateWithDataPassAction = WriteFragmentDirections.actionWriteFragmentToWriteOptionFragment(
                        selectedCategoryName, binding.etWriteDetail.text.toString(), uploadImageListString.toTypedArray(),postTagList.toTypedArray())
                    findNavController().navigate(navigateWithDataPassAction)
                } else {
                    val navigateWithDataPassAction = WriteFragmentDirections.actionWriteFragmentToWriteOptionFragment(
                        selectedCategoryName, binding.etWriteDetail.text.toString(),  uploadImageListString.toTypedArray(),emptyArray())
                    findNavController().navigate(navigateWithDataPassAction)
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
        when(selectedCategoryName) {
            "스케줄러" -> return "SCHEDULER"
            "스터디 플래너" -> return "STUDY_PLANNER"
            "굿노트" -> return "GOOD_NOTE"
            "포켓북" -> return "POCKET_BOOK"
            "6공 다이어리" -> return "SIX_DIARY"
            "스터디 플래너" -> return "ETC"
            else -> return ""
        }
    }

    private val listener1: RadioGroup.OnCheckedChangeListener = object : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
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

    private fun setImageUploadButtonClickListener() {
        val btnUploadImage = binding.btnUploadImage
        val rvUploadImageList = binding.rvImgUploadList
        uploadImageListAdapter = WriteUploadImageListAdapter(uploadImageList, requireContext())

        btnUploadImage.setOnClickListener {
            requestOpenGallery.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvUploadImageList.layoutManager = layoutManager
        rvUploadImageList.adapter = uploadImageListAdapter
    }

    private val requestOpenGallery =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (it.value == false) {
                    return@registerForActivityResult
                }
            }
            openGallery()
        }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        requestActivity.launch(intent)
    }

    val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if(activityResult.resultCode == RESULT_OK) {
                uploadImageList.clear()
                val data: Intent?= activityResult.data

                if (data?.clipData != null) { //사진 여러 개 선택 시
                    val count = data.clipData!!.itemCount
                    if (count > 10) {
                        Toast.makeText(requireContext(), "사진은 10장까지만 선택 가능합니다", Toast.LENGTH_SHORT).show()
                    }
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        uploadImageList.add(imageUri)
                        val imageBitmap = imageUri.toBitmap()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { uploadImageListString.add(imageBitmap.toBase64String()) }
                    }
                } else { // 단일 선택
                    data?.data?.let { uri ->
                        val imageUri : Uri?= data?.data
                        if (imageUri != null){
                            uploadImageList.add(imageUri)
                            val imageBitmap = imageUri.toBitmap()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { uploadImageListString.add(imageBitmap.toBase64String()) }
                        }
                    }
                }
                uploadImageListAdapter.notifyDataSetChanged()
            }
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