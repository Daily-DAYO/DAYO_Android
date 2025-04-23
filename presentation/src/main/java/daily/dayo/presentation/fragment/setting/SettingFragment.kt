package daily.dayo.presentation.fragment.setting

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
import daily.dayo.presentation.R
import daily.dayo.presentation.activity.LoginActivity
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.DefaultDialogConfigure
import daily.dayo.presentation.common.dialog.DefaultDialogConfirm
import daily.dayo.presentation.common.dialog.DefaultDialogExplanationConfirm
import daily.dayo.presentation.common.dialog.LoadingAlertDialog
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentSettingBinding
import daily.dayo.presentation.viewmodel.AccountViewModel

class SettingFragment : Fragment() {
    private var binding by autoCleared<FragmentSettingBinding> {
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setChangePasswordClickListener()
        setBackButtonClickListener()
        setLogoutButtonClickListener()
        setContactButtonClickListener()
        setWithdrawButtonClickListener()
        setNotificationButtonClickListener()
        setBlockButtonClickListener()
        setInformationButtonClickListener()
        setNoticeButtonClickListener()
    }

    private fun setChangePasswordClickListener() {
        binding.layoutSettingPasswordChange.setOnDebounceClickListener {

        }
    }

    private fun setBackButtonClickListener() {
        binding.btnSettingBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNotificationButtonClickListener() {
        binding.layoutSettingNotification.setOnDebounceClickListener {
        }
    }

    private fun setBlockButtonClickListener() {
        binding.layoutSettingBlock.setOnDebounceClickListener {

        }
    }

    private fun setInformationButtonClickListener() {
        binding.layoutSettingInformation.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.SettingFragment,
                action = R.id.action_settingFragment_to_informationFragment
            )
        }
    }

    private fun setNoticeButtonClickListener() {
        binding.layoutSettingNotice.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.SettingFragment,
                action = R.id.action_settingFragment_to_noticeFragment
            )
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
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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
        findNavController().navigateSafe(
            currentDestinationId = R.id.SettingFragment,
            action = R.id.action_settingFragment_to_withdrawFragment
        )
    }

    private fun doLogout() {
        LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
        accountViewModel.requestLogout()
        accountViewModel.logoutSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                accountViewModel.clearCurrentUser()
                val intent = Intent(this.activity, LoginActivity::class.java)
                startActivity(intent)
                this.requireActivity().finish()
            }
        }
    }
}

