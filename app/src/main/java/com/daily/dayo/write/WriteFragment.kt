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

private var mRgLine1: RadioGroup? = null
private var mRgLine2: RadioGroup? = null

class WriteFragment : Fragment() {
    private var _binding: FragmentWriteBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteBinding.inflate(inflater, container, false)
        mRgLine1 = binding.radiogroupWritePostCategoryLine1
        mRgLine2 = binding.radiogroupWritePostCategoryLine2
        mRgLine1!!.clearCheck()
        mRgLine2!!.clearCheck()
        mRgLine1!!.setOnCheckedChangeListener(listener1)
        mRgLine2!!.setOnCheckedChangeListener(listener2)

        binding.btnWritePostBack.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    private val listener1: RadioGroup.OnCheckedChangeListener = object :
        RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            if (checkedId != -1) {
                mRgLine2!!.setOnCheckedChangeListener(null)
                mRgLine2!!.clearCheck()
                mRgLine2!!.setOnCheckedChangeListener(listener2)
            }
        }
    }

    private val listener2: RadioGroup.OnCheckedChangeListener = object :
        RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            if (checkedId != -1) {
                mRgLine1!!.setOnCheckedChangeListener(null)
                mRgLine1!!.clearCheck()
                mRgLine1!!.setOnCheckedChangeListener(listener1)
            }
        }
    }
    fun onClick(v: View) {
        // TODO Auto-generated method stub
        binding.btnWritePostUpload.setOnClickListener {
                var selectedResult = ""
                if (mRgLine1!!.checkedRadioButtonId > 0) {
                    val radioButton = mRgLine1?.findViewById<View>(
                        mRgLine1!!.checkedRadioButtonId
                    )
                    val radioId = mRgLine1!!.indexOfChild(radioButton)
                    val btn = mRgLine1!!.getChildAt(radioId) as RadioButton
                    selectedResult = btn.text as String
                } else if (mRgLine2!!.checkedRadioButtonId > 0) {
                    val radioButton = mRgLine2?.findViewById<View>(
                        mRgLine2!!.checkedRadioButtonId
                    )
                    val radioId = mRgLine2!!.indexOfChild(radioButton)
                    val btn = mRgLine2!!.getChildAt(radioId) as RadioButton
                    selectedResult = btn.text as String
                }
            }
        }
    }