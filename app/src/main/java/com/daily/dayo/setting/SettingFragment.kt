package com.daily.dayo.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentSettingBinding
import com.daily.dayo.login.LoginActivity
import com.daily.dayo.util.DefaultDialogConfigure
import com.daily.dayo.util.DefaultDialogConfirm
import com.daily.dayo.util.DefaultDialogExplanationConfirm
import com.daily.dayo.util.autoCleared

class SettingFragment: Fragment() {
    private var binding by autoCleared<FragmentSettingBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setLogoutButtonClickListener()
        setWithdrawButtonClickListener()
        return binding.root
    }

    private fun setBackButtonClickListener(){
        binding.btnSettingBack.setOnClickListener {
            findNavController().navigateUp()
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
        SharedManager(DayoApplication.applicationContext()).clearPreferences()
        val intent = Intent(this.activity, LoginActivity::class.java)
        startActivity(intent)
        this.requireActivity().finish()
    }
}

