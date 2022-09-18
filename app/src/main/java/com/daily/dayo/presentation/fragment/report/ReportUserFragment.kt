package com.daily.dayo.presentation.fragment.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentReportUserBinding
import com.daily.dayo.presentation.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportUserFragment : Fragment() {
    private var binding by autoCleared<FragmentReportUserBinding>()
    private val reportViewModel by activityViewModels<ReportViewModel>()
    private val args by navArgs<ReportUserFragmentArgs>()
    private lateinit var reportUserOptionMap: Map<Int, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportUserBinding.inflate(inflater, container, false)

        initReportUserOption()
        setOnClickReportUserOtherReason()
        setBackButtonClickListener()
        setReportUserButtonActivation()
        setReportUserButtonClickListener()
        return binding.root
    }
    
    private fun initReportUserOption() {
        reportUserOptionMap = mapOf(
            binding.radiobuttonReportUserReason1.id to binding.radiobuttonReportUserReason1.text.toString(),
            binding.radiobuttonReportUserReason2.id to binding.radiobuttonReportUserReason2.text.toString(),
            binding.radiobuttonReportUserReason3.id to binding.radiobuttonReportUserReason3.text.toString(),
            binding.radiobuttonReportUserReason4.id to binding.radiobuttonReportUserReason4.text.toString(),
            binding.radiobuttonReportUserReason5.id to binding.radiobuttonReportUserReason5.text.toString(),
            binding.radiobuttonReportUserReason6.id to binding.radiobuttonReportUserReason6.text.toString(),
            binding.radiobuttonReportUserReason7.id to binding.radiobuttonReportUserReason7.text.toString()
        )
    }
    
    private fun setBackButtonClickListener() {
        binding.btnReportUserBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOnClickReportUserOtherReason() {
        binding.radiogroupReportUserReason.setOnCheckedChangeListener { _, id ->
            if (binding.radiogroupReportUserReason.checkedRadioButtonId != -1) binding.btnReportUser.isSelected =
                true
            when (id) {
                binding.radiobuttonReportUserReasonOther.id -> binding.etReportUserReasonOther.visibility =
                    View.VISIBLE
                else -> binding.etReportUserReasonOther.visibility = View.INVISIBLE
            }
        }
    }

    private fun setReportUserButtonActivation() {
        if (binding.radiogroupReportUserReason.checkedRadioButtonId == -1) binding.btnReportUser.isSelected =
            false
    }

    private fun setReportUserButtonClickListener() {
        binding.btnReportUser.setOnClickListener {
            if (isOtherOption()) {
                reportViewModel.requestSaveMemberReport(
                    comment = binding.etReportUserReasonOther.text.toString(),
                    memberId = args.memberId
                )
            } else {
                reportUserOptionMap[binding.radiogroupReportUserReason.checkedRadioButtonId]?.let {
                    reportViewModel.requestSaveMemberReport(
                        comment = it,
                        memberId = args.memberId
                    )
                }
            }
            reportViewModel.reportMemberSuccess.observe(viewLifecycleOwner) {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.report_post_alert_message),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun isOtherOption() =
        binding.radiogroupReportUserReason.checkedRadioButtonId == binding.radiobuttonReportUserReasonOther.id

}