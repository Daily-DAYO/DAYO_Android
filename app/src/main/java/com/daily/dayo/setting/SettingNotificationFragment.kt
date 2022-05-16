package com.daily.dayo.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.DayoApplication
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentSettingNotificationBinding
import com.daily.dayo.util.FirebaseMessagingServiceUtil
import com.daily.dayo.util.autoCleared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingNotificationFragment: Fragment() {
    private var binding by autoCleared<FragmentSettingNotificationBinding>()
    private val settingViewModel by activityViewModels<SettingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingNotificationBinding.inflate(inflater, container, false)
        setInitSwitchState()
        setBackButtonClickListener()
        setDeviceNotification()
        setNoticeNotification()
        setReactionNotification()
        return binding.root
    }

    private fun setInitSwitchState(){
        Log.e("SettingNoti.kt", SharedManager(DayoApplication.applicationContext()).notificationPermit.toString())
        if(SharedManager(DayoApplication.applicationContext()).notificationPermit != "OFF") {
            binding.switchSettingNotificationDevice.isChecked = true
            binding.switchSettingNotificationNotice.isEnabled = true
            binding.switchSettingNotificationReaction.isEnabled = true

            binding.switchSettingNotificationNotice.isChecked =
                SharedManager(DayoApplication.applicationContext()).notificationPermit?.contains("NOTICE") == true
            binding.switchSettingNotificationReaction.isChecked =
                SharedManager(DayoApplication.applicationContext()).notificationPermit?.contains("REACTION") == true
        }
        else {
            binding.switchSettingNotificationDevice.isChecked = false
            binding.switchSettingNotificationNotice.isEnabled = false
            binding.switchSettingNotificationReaction.isEnabled = false
        }
    }

    private fun setDeviceNotification(){
        binding.switchSettingNotificationDevice.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                setFCM()
                binding.switchSettingNotificationNotice.isEnabled = true
                binding.switchSettingNotificationReaction.isEnabled = true
                setNotificationOnState()
            }
            else{
                binding.switchSettingNotificationNotice.isEnabled = false
                binding.switchSettingNotificationReaction.isEnabled = false
                FirebaseMessagingServiceUtil().unregisterFcmToken()
                settingViewModel.requestDeviceToken(null)
                SharedManager(DayoApplication.applicationContext()).notificationPermit = "OFF"
            }
        }
    }

    private fun setNoticeNotification(){
        binding.switchSettingNotificationNotice.setOnCheckedChangeListener { _, _ ->
            setNotificationOnState()
        }
    }

    private fun setReactionNotification(){
        binding.switchSettingNotificationReaction.setOnCheckedChangeListener { _, _ ->
            setNotificationOnState()
        }
    }

    fun setNotificationOnState(){
        if(binding.switchSettingNotificationNotice.isChecked && binding.switchSettingNotificationReaction.isChecked)
            SharedManager(DayoApplication.applicationContext()).notificationPermit = "NOTICE_REACTION"
        else if(!binding.switchSettingNotificationNotice.isChecked && binding.switchSettingNotificationReaction.isChecked)
            SharedManager(DayoApplication.applicationContext()).notificationPermit = "REACTION"
        else if(binding.switchSettingNotificationNotice.isChecked && !binding.switchSettingNotificationReaction.isChecked)
            SharedManager(DayoApplication.applicationContext()).notificationPermit = "NOTICE"
        else if(!binding.switchSettingNotificationNotice.isChecked && !binding.switchSettingNotificationReaction.isChecked)
            SharedManager(DayoApplication.applicationContext()).notificationPermit = "ON"
    }

    private fun setBackButtonClickListener(){
        binding.btnSettingNotificationBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFCM(){
        GlobalScope.launch(Dispatchers.IO) {
            FirebaseMessagingServiceUtil().registerFcmToken()
        }
        settingViewModel.requestDeviceToken(SharedManager(DayoApplication.applicationContext()).fcmDeviceToken)
    }
}