package daily.dayo.presentation.fragment.setting.withdraw

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import daily.dayo.presentation.R
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.LoadingAlertDialog
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentWithdrawBinding
import daily.dayo.presentation.activity.LoginActivity
import daily.dayo.presentation.viewmodel.AccountViewModel

class WithdrawFragment : Fragment() {
    private var binding by autoCleared<FragmentWithdrawBinding> {
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        setFinalMessageTextSpan()
        setOnClickWithdrawOtherReason()
        setBackButtonClickListener()
        setWithdrawButtonActivation()
        setWithdrawButtonClickListener()
        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnWithdrawBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOnClickWithdrawOtherReason() {
        binding.radiogroupWithdrawReason.setOnCheckedChangeListener { _, id ->
            if (binding.radiogroupWithdrawReason.checkedRadioButtonId != -1) binding.btnWithdraw.isEnabled =
                true
            when (id) {
                binding.radiobuttonWithdrawReasonOther.id -> binding.etWithdrawReasonOther.visibility =
                    View.VISIBLE
                else -> binding.etWithdrawReasonOther.visibility = View.GONE
            }
        }
    }

    private fun setWithdrawButtonActivation() {
        if (binding.radiogroupWithdrawReason.checkedRadioButtonId == -1) {
            binding.btnWithdraw.isEnabled = false
        }
    }

    private fun setWithdrawButtonClickListener() {
        binding.btnWithdraw.setOnDebounceClickListener {
            LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
            val reason = when (binding.radiogroupWithdrawReason.checkedRadioButtonId) {
                R.id.radiobutton_withdraw_reason_1 -> binding.radiobuttonWithdrawReason1.text.toString()
                R.id.radiobutton_withdraw_reason_2 -> binding.radiobuttonWithdrawReason2.text.toString()
                R.id.radiobutton_withdraw_reason_3 -> binding.radiobuttonWithdrawReason3.text.toString()
                R.id.radiobutton_withdraw_reason_4 -> binding.radiobuttonWithdrawReason4.text.toString()
                R.id.radiobutton_withdraw_reason_5 -> binding.radiobuttonWithdrawReason5.text.toString()
                else -> binding.radiobuttonWithdrawReasonOther.text.toString()
            }
            accountViewModel.requestWithdraw(content = reason)
            accountViewModel.withdrawSuccess.observe(viewLifecycleOwner) {
                if (it.getContentIfNotHandled() == true) {
                    accountViewModel.clearCurrentUser()
                    val intent = Intent(this.activity, LoginActivity::class.java)
                    startActivity(intent)
                    this.requireActivity().finish()
                }
            }
        }
    }

    private fun setFinalMessageTextSpan() {
        val spannableText: Spannable = binding.tvWithdrawFinalMessage.text as Spannable
        spannableText.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary_green_23C882
                )
            ), 16, 35, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}