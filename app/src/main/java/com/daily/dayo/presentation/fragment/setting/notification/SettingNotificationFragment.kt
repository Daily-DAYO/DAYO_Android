package com.daily.dayo.setting

<<<<<<< HEAD
import android.content.ContentValues.TAG
=======
>>>>>>> origin/feature/issue-252
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
<<<<<<< HEAD
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
=======
>>>>>>> origin/feature/issue-252
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
<<<<<<< HEAD
=======
        Log.e("SettingNoti.kt", SharedManager(DayoApplication.applicationContext()).notificationPermit.toString())
>>>>>>> origin/feature/issue-252
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
<<<<<<< HEAD
        binding.switchSettingNotificationNotice.setOnCheckedChangeListener { _, isChecked ->
            setNotificationOnState()
            if(isChecked){
                Firebase.messaging.subscribeToTopic("NOTICE")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "NOTICE 수신")
                    }
            }
            else{
                Firebase.messaging.unsubscribeFromTopic("NOTICE")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "NOTICE 수신 거부")
                    }
            }
=======
        binding.switchSettingNotificationNotice.setOnCheckedChangeListener { _, _ ->
            setNotificationOnState()
>>>>>>> origin/feature/issue-252
        }
    }

    private fun setReactionNotification(){
<<<<<<< HEAD
        binding.switchSettingNotificationReaction.setOnCheckedChangeListener { _, isChecked ->
            setNotificationOnState()
            if(isChecked){
                Firebase.messaging.subscribeToTopic("HEART")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "HEART 수신")
                    }
                Firebase.messaging.subscribeToTopic("COMMENT")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "COMMENT 수신")
                    }
            }
            else{
                Firebase.messaging.unsubscribeFromTopic("HEART")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "HEART 수신 거부")
                    }
                Firebase.messaging.unsubscribeFromTopic("COMMENT")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "COMMENT 수신 거부")
                    }
            }
=======
        binding.switchSettingNotificationReaction.setOnCheckedChangeListener { _, _ ->
            setNotificationOnState()
>>>>>>> origin/feature/issue-252
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