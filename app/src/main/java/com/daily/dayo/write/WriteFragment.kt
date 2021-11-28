package com.daily.dayo.write

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.dayo.databinding.FragmentWriteBinding
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.util.autoCleared

class WriteFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteBinding>()
    private var radioGroupCategoryLine1: RadioGroup? = null
    private var radioGroupCategoryLine2: RadioGroup? = null
    private lateinit var postTagList : List<String> // 현재 작성할 게시글에 저장된 태그 리스트
    private var uploadImageList = ArrayList<Uri>() // 갤러리에서 불러온 이미지 리스트
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
        var selectedCategoryName = ""
        binding.btnWritePostUpload.setOnClickListener {
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

            if(this::postTagList.isInitialized){
                val navigateWithDataPassAction = WriteFragmentDirections.actionWriteFragmentToWriteOptionFragment(postTagList.toTypedArray())
                findNavController().navigate(navigateWithDataPassAction)
            } else {
                val navigateWithDataPassAction = WriteFragmentDirections.actionWriteFragmentToWriteOptionFragment(emptyArray())
                findNavController().navigate(navigateWithDataPassAction)
            }
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

    val requestActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if(activityResult.resultCode == RESULT_OK) {
                binding.imgUploadFirst.visibility = View.INVISIBLE
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
                    }
                } else { // 단일 선택
                    data?.data?.let { uri ->
                        val imageUri : Uri?= data?.data
                        if (imageUri != null){
                            uploadImageList.add(imageUri)
                        }
                    }
                }
                uploadImageListAdapter.notifyDataSetChanged()
            }
        }
}