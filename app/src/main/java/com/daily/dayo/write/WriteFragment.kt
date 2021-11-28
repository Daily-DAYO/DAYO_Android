package com.daily.dayo.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.dayo.databinding.FragmentWriteBinding
import android.widget.RadioGroup
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.util.autoCleared

class WriteFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteBinding>()
    private var radioGroupCategoryLine1: RadioGroup? = null
    private var radioGroupCategoryLine2: RadioGroup? = null
    private lateinit var postTagList : List<String>

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentWriteBinding.inflate(inflater, container, false)
        observeNavigationTagListCallBack()
        setRadioButtonGrouping()
        setBackButtonClickListener()
        setUploadButtonClickListener()
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
}