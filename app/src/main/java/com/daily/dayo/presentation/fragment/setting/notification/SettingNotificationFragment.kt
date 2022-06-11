package com.daily.dayo.presentation.fragment.setting.notification

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentSettingNotificationBinding
import com.daily.dayo.presentation.viewmodel.SettingNotificationViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class SettingNotificationFragment : Fragment() {
    private var binding by autoCleared<FragmentSettingNotificationBinding>()
    private val settingNotificationViewModel by activityViewModels<SettingNotificationViewModel>()

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

    private fun setInitSwitchState() {
        if (DayoApplication.preferences.notiDevicePermit) {
            binding.switchSettingNotificationDevice.isChecked = true
            binding.switchSettingNotificationNotice.isEnabled = true
            binding.switchSettingNotificationReaction.isEnabled = true
            settingNotificationViewModel.requestReceiveAlarm()
            settingNotificationViewModel.notiReactionPermit.observe(viewLifecycleOwner){ reactionPermit ->
                binding.notiReactionPermit = reactionPermit
            }
            binding.switchSettingNotificationNotice.isChecked = DayoApplication.preferences.notiNoticePermit
        } else {
            binding.switchSettingNotificationDevice.isChecked = false
            binding.switchSettingNotificationNotice.isEnabled = false
            binding.switchSettingNotificationReaction.isEnabled = false
        }
    }

    private fun setDeviceNotification() {
        binding.switchSettingNotificationDevice.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) { // 디바이스 알림 켜기
                binding.switchSettingNotificationReaction.isEnabled = true
                binding.switchSettingNotificationNotice.isEnabled = true
                setNotificationOnState() // 디바이스 알림 켰을 때의 공지, 반응 알림 상태에 따라 설정
            } else { // 디바이스 알림 끄기
                binding.switchSettingNotificationNotice.isEnabled = false
                binding.switchSettingNotificationReaction.isEnabled = false
                setNotificationOffState() // 디바이스 알림 껐을 때의 공지, 반응 알림 설정 모두 끄기
            }
        }
    }

    private fun setNoticeNotification() {
        binding.switchSettingNotificationNotice.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) { // 공지 알림 켜기
                Firebase.messaging.subscribeToTopic(getString(R.string.notification_topic_notice))
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "NOTICE 수신")
                    }
                DayoApplication.preferences.notiNoticePermit = true
            } else { // 공지 알림 끄기
                Firebase.messaging.unsubscribeFromTopic("NOTICE")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "NOTICE 수신 거부")
                    }
                DayoApplication.preferences.notiNoticePermit = false
            }
        }
    }

    private fun setReactionNotification() {
        binding.switchSettingNotificationReaction.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) { // 반응 알림 켜기
                settingNotificationViewModel.requestReceiveChangeReceiveAlarm(onReceiveAlarm = true)
            } else { // 반응 알림 끄기
                settingNotificationViewModel.requestReceiveChangeReceiveAlarm(onReceiveAlarm = false)
            }
        }
    }

    private fun setNotificationOnState() { // 디바이스 알림 켰을 때의 공지, 반응 알림 상태에 따라 설정
        setFCM()
        DayoApplication.preferences.notiDevicePermit = true
        settingNotificationViewModel.requestReceiveAlarm()
        settingNotificationViewModel.notiReactionPermit.observe(viewLifecycleOwner){ reactionPermit ->
            binding.notiReactionPermit = reactionPermit
        }
        binding.switchSettingNotificationNotice.isChecked =
            DayoApplication.preferences.notiNoticePermit
    }

    private fun setNotificationOffState() { // 디바이스 알림 껐을 때의 공지, 반응 알림 설정 모두 끄기
        binding.switchSettingNotificationNotice.isChecked = false
        binding.switchSettingNotificationReaction.isChecked = false
        DayoApplication.preferences.notiDevicePermit = false
        DayoApplication.preferences.notiNoticePermit = false
        settingNotificationViewModel.unregisterFcmToken()
        settingNotificationViewModel.requestDeviceToken(null)
    }

    private fun setBackButtonClickListener() {
        binding.btnSettingNotificationBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFCM() {
        settingNotificationViewModel.registerFcmToken()
        settingNotificationViewModel.requestDeviceToken(DayoApplication.preferences.fcmDeviceToken)
    }
}