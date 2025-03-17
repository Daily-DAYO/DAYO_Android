package daily.dayo.presentation.fragment.setting.notification

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import daily.dayo.presentation.R
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentSettingNotificationBinding
import daily.dayo.presentation.activity.MainActivity
import daily.dayo.presentation.viewmodel.SettingNotificationViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import daily.dayo.presentation.viewmodel.AccountViewModel

class SettingNotificationFragment : Fragment() {
    private var binding by autoCleared<FragmentSettingNotificationBinding>()
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private val settingNotificationViewModel by activityViewModels<SettingNotificationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingNotificationBinding.inflate(inflater, container, false)
        checkCurrentNotification()
        setInitSwitchState()
        setBackButtonClickListener()
        setDeviceNotification()
        setNoticeNotification()
        setReactionNotification()
        return binding.root
    }

    private fun checkCurrentNotification() {
        if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.permission_fail_message_notification),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setInitSwitchState() {
        if (accountViewModel.getCurrentUserNotiDevicePermit()) {
            binding.switchSettingNotificationDevice.isChecked = true
            binding.switchSettingNotificationNotice.isEnabled = true
            binding.switchSettingNotificationReaction.isEnabled = true
            settingNotificationViewModel.requestReceiveAlarm()
            settingNotificationViewModel.isReactionNotificationEnabled.asLiveData().observe(viewLifecycleOwner) { reactionPermit ->
                    binding.notiReactionPermit = reactionPermit
            }
            binding.switchSettingNotificationNotice.isChecked =
                accountViewModel.isNoticeNotificationEnabled.value
        } else {
            binding.switchSettingNotificationDevice.isChecked = false
            binding.switchSettingNotificationNotice.isEnabled = false
            binding.switchSettingNotificationReaction.isEnabled = false
        }
    }

    private fun setDeviceNotification() {
        binding.switchSettingNotificationDevice.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) { // 디바이스 알림 켜기
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
                accountViewModel.changeNoticeNotificationSetting(true)
            } else { // 공지 알림 끄기
                Firebase.messaging.unsubscribeFromTopic("NOTICE")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(TAG, "NOTICE 수신 거부")
                    }
                accountViewModel.changeNoticeNotificationSetting(false)
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
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
                binding.switchSettingNotificationReaction.isEnabled = true
                binding.switchSettingNotificationNotice.isEnabled = true

                setFCM()
                accountViewModel.requestCurrentUserNotiDevicePermit(true)
                settingNotificationViewModel.requestReceiveAlarm()
                settingNotificationViewModel.isReactionNotificationEnabled.asLiveData().observe(viewLifecycleOwner) { reactionPermit ->
                    if (reactionPermit != null) {
                        binding.notiReactionPermit = reactionPermit
                    }
                }
                binding.switchSettingNotificationNotice.isChecked =
                    accountViewModel.isNoticeNotificationEnabled.value
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                // by them granting the POST_NOTIFICATION permission. This UI should provide the user
                // "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                // If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                permissionLauncherNotification.launch(MainActivity.notificationPermission)
            }
        }
    }

    private fun setNotificationOffState() { // 디바이스 알림 껐을 때의 공지, 반응 알림 설정 모두 끄기
        binding.switchSettingNotificationNotice.isChecked = false
        binding.switchSettingNotificationReaction.isChecked = false
        accountViewModel.requestCurrentUserNotiDevicePermit(false)
        accountViewModel.changeNoticeNotificationSetting(false)
        settingNotificationViewModel.unregisterFcmToken()
    }

    private fun setBackButtonClickListener() {
        binding.btnSettingNotificationBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFCM() {
        settingNotificationViewModel.registerDeviceToken()
    }

    private val permissionLauncherNotification =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedList: List<String> = permissions.filter {
                !it.value
            }.map {
                it.key
            }

            when {
                deniedList.isNotEmpty() -> {
                    accountViewModel.requestCurrentUserNotiDevicePermit(false)
                    accountViewModel.changeNoticeNotificationSetting(false)
                    val map = deniedList.groupBy { permission ->
                        if (shouldShowRequestPermissionRationale(permission)) getString(R.string.permission_fail_second) else getString(
                            R.string.permission_fail_final
                        )
                    }
                    map[getString(R.string.permission_fail_second)]?.let {
                        // request denied , request again
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.permission_fail_message_notification),
                            Toast.LENGTH_SHORT
                        ).show()
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            MainActivity.notificationPermission,
                            1000
                        )
                    }
                    map[getString(R.string.permission_fail_final)]?.let {
                        Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.permission_fail_final_message_notification),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    binding.switchSettingNotificationDevice.isChecked = false
                    binding.switchSettingNotificationReaction.isEnabled = false
                    binding.switchSettingNotificationNotice.isEnabled = false
                }
                else -> {
                    //All request are permitted
                    // 알림 최초 허용시에 모든 알림 허용처리
                    accountViewModel.requestCurrentUserNotiDevicePermit(true)
                    accountViewModel.changeNoticeNotificationSetting(true)
                    settingNotificationViewModel.registerDeviceToken()
                    settingNotificationViewModel.requestReceiveAlarm()
                    setInitSwitchState()
                }
            }
        }
}