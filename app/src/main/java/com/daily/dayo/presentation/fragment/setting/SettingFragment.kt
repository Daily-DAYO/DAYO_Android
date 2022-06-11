package com.daily.dayo.presentation.fragment.setting

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
import com.daily.dayo.common.DefaultDialogConfigure
import com.daily.dayo.common.DefaultDialogConfirm
import com.daily.dayo.common.DefaultDialogExplanationConfirm
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentSettingBinding
import com.daily.dayo.presentation.activity.LoginActivity
import com.daily.dayo.presentation.viewmodel.AccountViewModel

class SettingFragment: Fragment() {
    private var binding by autoCleared<FragmentSettingBinding>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setLogoutButtonClickListener()
        setWithdrawButtonClickListener()
        setNotificationButtonClickListener()
        return binding.root
    }

    private fun setBackButtonClickListener(){
        binding.btnSettingBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNotificationButtonClickListener(){
        binding.layoutSettingNotification.setOnClickListener{
            findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToSettingNotificationFragment())
        }
    }

    private fun setLogoutButtonClickListener(){
        binding.layoutSettingLogout.setOnClickListener {
            val logoutAlertDialog = DefaultDialogConfirm.createDialog(requireContext(), R.string.setting_logout_message,
                true, true, R.string.confirm, R.string.cancel, {doLogout()}, {})
            if(!logoutAlertDialog.isShowing) {
                logoutAlertDialog.show()
                DefaultDialogConfigure.dialogResize(requireContext(), logoutAlertDialog, 0.7f, 0.23f)
            }
            logoutAlertDialog.setOnCancelListener {
                logoutAlertDialog.dismiss()
            }
        }
    }

    private fun setWithdrawButtonClickListener(){
        binding.layoutSettingWithdraw.setOnClickListener {
            val withdrawAlertDialog = DefaultDialogExplanationConfirm.createDialog(requireContext(), R.string.setting_withdraw_message,
                R.string.setting_withdraw_explanation_message,true, true, R.string.confirm, R.string.cancel, {doWithdraw()}, {})
            if(!withdrawAlertDialog.isShowing) {
                withdrawAlertDialog.show()
                DefaultDialogConfigure.dialogResize(requireContext(), withdrawAlertDialog, 0.7f, 0.23f)
            }
            withdrawAlertDialog.setOnCancelListener {
                withdrawAlertDialog.dismiss()
            }
        }
    }

    private fun doWithdraw(){
        findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToWithdrawFragment())
    }

    private fun doLogout(){
        accountViewModel.requestLogout()
        accountViewModel.logoutSuccess.observe(viewLifecycleOwner){
            if(it.getContentIfNotHandled() == true){
                DayoApplication.preferences.clearPreferences()
                val intent = Intent(this.activity, LoginActivity::class.java)
                startActivity(intent)
                this.requireActivity().finish()
            }
        }
    }
}

