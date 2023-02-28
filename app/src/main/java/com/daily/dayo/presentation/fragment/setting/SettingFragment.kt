package com.daily.dayo.presentation.fragment.setting

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.dialog.DefaultDialogConfigure
import com.daily.dayo.common.dialog.DefaultDialogConfirm
import com.daily.dayo.common.dialog.DefaultDialogExplanationConfirm
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.dialog.LoadingAlertDialog
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentSettingBinding
import com.daily.dayo.presentation.activity.LoginActivity
import com.daily.dayo.presentation.viewmodel.AccountViewModel

class SettingFragment : Fragment() {
    private var binding by autoCleared<FragmentSettingBinding>()
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        setChangePasswordClickListener()
        setBackButtonClickListener()
        setLogoutButtonClickListener()
        setContactButtonClickListener()
        setWithdrawButtonClickListener()
        setNotificationButtonClickListener()
        setBlockButtonClickListener()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun setChangePasswordClickListener() {
        binding.layoutSettingPasswordChange.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_settingChangePasswordCurrentFragment)
        }
    }

    private fun setBackButtonClickListener() {
        binding.btnSettingBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNotificationButtonClickListener() {
        binding.layoutSettingNotification.setOnDebounceClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToSettingNotificationFragment())
        }
    }

    private fun setBlockButtonClickListener() {
        binding.layoutSettingBlock.setOnDebounceClickListener {
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToSettingBlockFragment())
        }
    }

    private fun setContactButtonClickListener() {
        binding.layoutSettingContact.setOnDebounceClickListener {
            val contactAlertDialog = DefaultDialogExplanationConfirm.createDialog(requireContext(),
                R.string.setting_contact_message,
                R.string.setting_contact_explanation_message,
                true,
                false,
                R.string.confirm,
                R.string.cancel,
                { doContact() },
                {})
            if (!contactAlertDialog.isShowing) {
                contactAlertDialog.show()
                DefaultDialogConfigure.dialogResize(
                    requireContext(),
                    contactAlertDialog,
                    0.7f,
                    0.23f
                )
            }
        }
    }

    private fun doContact() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Contact E-Mail", "gemij.dev@gmail.com")
        clipboard.setPrimaryClip(clip)
    }

    private fun setLogoutButtonClickListener() {
        binding.layoutSettingLogout.setOnDebounceClickListener {
            val logoutAlertDialog =
                 DefaultDialogConfirm.createDialog(requireContext(), R.string.setting_logout_message,
                    true, true, R.string.confirm, R.string.cancel, { doLogout() }, {})
            if (!logoutAlertDialog.isShowing) {
                logoutAlertDialog.show()
                DefaultDialogConfigure.dialogResize(
                    requireContext(),
                    logoutAlertDialog,
                    0.7f,
                    0.23f
                )
            }
            logoutAlertDialog.setOnCancelListener {
                logoutAlertDialog.dismiss()
            }
        }
    }

    private fun setWithdrawButtonClickListener() {
        binding.layoutSettingWithdraw.setOnDebounceClickListener {
            val withdrawAlertDialog = DefaultDialogExplanationConfirm.createDialog(requireContext(),
                R.string.setting_withdraw_message,
                R.string.setting_withdraw_explanation_message,
                true,
                true,
                R.string.confirm,
                R.string.cancel,
                { doWithdraw() },
                {})
            if (!withdrawAlertDialog.isShowing) {
                withdrawAlertDialog.show()
                DefaultDialogConfigure.dialogResize(
                    requireContext(),
                    withdrawAlertDialog,
                    0.7f,
                    0.23f
                )
            }
            withdrawAlertDialog.setOnCancelListener {
                withdrawAlertDialog.dismiss()
            }
        }
    }

    private fun doWithdraw() {
        findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToWithdrawFragment())
    }

    private fun doLogout() {
        LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
        accountViewModel.requestLogout()
        accountViewModel.logoutSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                DayoApplication.preferences.clearPreferences()
                val intent = Intent(this.activity, LoginActivity::class.java)
                startActivity(intent)
                this.requireActivity().finish()
            }
        }
    }
}

