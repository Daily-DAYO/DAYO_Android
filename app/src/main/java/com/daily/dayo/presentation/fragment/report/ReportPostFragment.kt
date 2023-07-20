package com.daily.dayo.presentation.fragment.report

import android.app.AlertDialog
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
import com.daily.dayo.common.dialog.LoadingAlertDialog
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentReportPostBinding
import com.daily.dayo.presentation.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportPostFragment : Fragment() {
    private var binding by autoCleared<FragmentReportPostBinding> {
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }
    private val reportViewModel by activityViewModels<ReportViewModel>()
    private val args by navArgs<ReportPostFragmentArgs>()
    private lateinit var reportPostOptionMap: Map<Int, String>
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportPostBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())

        initReportPostOption()
        setOnClickReportPostOtherReason()
        setBackButtonClickListener()
        setReportPostButtonActivation()
        setReportPostButtonClickListener()
        return binding.root
    }

    private fun initReportPostOption() {
        reportPostOptionMap = mapOf(
            binding.radiobuttonReportPostReason1.id to binding.radiobuttonReportPostReason1.text.toString(),
            binding.radiobuttonReportPostReason2.id to binding.radiobuttonReportPostReason2.text.toString(),
            binding.radiobuttonReportPostReason3.id to binding.radiobuttonReportPostReason3.text.toString(),
            binding.radiobuttonReportPostReason4.id to binding.radiobuttonReportPostReason4.text.toString(),
            binding.radiobuttonReportPostReason5.id to binding.radiobuttonReportPostReason5.text.toString(),
            binding.radiobuttonReportPostReason6.id to binding.radiobuttonReportPostReason6.text.toString(),
            binding.radiobuttonReportPostReason7.id to binding.radiobuttonReportPostReason7.text.toString()
        )
    }

    private fun setBackButtonClickListener() {
        binding.btnReportPostBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOnClickReportPostOtherReason() {
        binding.radiogroupReportPostReason.setOnCheckedChangeListener { _, id ->
            if (binding.radiogroupReportPostReason.checkedRadioButtonId != -1) binding.btnReportPost.isSelected =
                true
            when (id) {
                binding.radiobuttonReportPostReasonOther.id -> binding.etReportPostReasonOther.visibility =
                    View.VISIBLE
                else -> binding.etReportPostReasonOther.visibility = View.INVISIBLE
            }
        }
    }

    private fun setReportPostButtonActivation() {
        if (binding.radiogroupReportPostReason.checkedRadioButtonId == -1) binding.btnReportPost.isSelected =
            false
    }

    private fun setReportPostButtonClickListener() {
        binding.btnReportPost.setOnDebounceClickListener {
            LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
            if (isOtherOption()) {
                reportViewModel.requestSavePostReport(
                    comment = binding.etReportPostReasonOther.text.toString(),
                    postId = args.postId
                )
            } else {
                reportPostOptionMap[binding.radiogroupReportPostReason.checkedRadioButtonId]?.let {
                    reportViewModel.requestSavePostReport(
                        comment = it,
                        postId = args.postId
                    )
                }
            }
            reportViewModel.reportPostSuccess.observe(viewLifecycleOwner) {
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
        binding.radiogroupReportPostReason.checkedRadioButtonId == binding.radiobuttonReportPostReasonOther.id
}