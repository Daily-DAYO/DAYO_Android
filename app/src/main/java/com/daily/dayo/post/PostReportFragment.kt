package com.daily.dayo.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentPostReportBinding
import com.daily.dayo.util.autoCleared

class PostReportFragment : Fragment() {
    private var binding by autoCleared<FragmentPostReportBinding>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostReportBinding.inflate(inflater, container, false)
        setOnClickPostReportOtherReason()
        setBackButtonClickListener()
        setPostReportButtonActivation()
        setPostReportButtonClickListener()
        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnPostReportBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOnClickPostReportOtherReason(){
        binding.radiogroupPostReportReason.setOnCheckedChangeListener { _, id ->
            if(binding.radiogroupPostReportReason.checkedRadioButtonId!=-1) binding.btnPostReport.isSelected = true
            when(id) {
                binding.radiobuttonPostReportReasonOther.id -> binding.etPostReportReasonOther.visibility = View.VISIBLE
                else -> binding.etPostReportReasonOther.visibility = View.INVISIBLE
            }
        }
    }

    private fun setPostReportButtonActivation(){
        if(binding.radiogroupPostReportReason.checkedRadioButtonId==-1) binding.btnPostReport.isSelected = false
    }

    private fun setPostReportButtonClickListener(){
        binding.btnPostReport.setOnClickListener {
            if(binding.btnPostReport.isSelected) {
                Toast.makeText(requireContext(), getString(R.string.post_report_alert_message), Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }
}