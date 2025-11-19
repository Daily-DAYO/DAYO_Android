package daily.dayo.presentation.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.screen.main.MainScreen
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.SettingNotificationViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val accountViewModel by viewModels<AccountViewModel>()
    private val settingNotificationViewModel by viewModels<SettingNotificationViewModel>()
    private var rewardedAd: RewardedAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCurrentNotification()
        getNotificationData()
        askNotificationPermission()
        loadRewardedAd()
        setContent {
            DayoTheme {
                MainScreen(
                    onAdRequest = { onRewardSuccess ->
                        showAdIfAvailable(onRewardSuccess)
                    },
                    onExit = { finish() }
                )
            }
        }
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, BuildConfig.REWARDED_AD_UNIT_ID_FOLDER, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedAd = null
            }
        })
    }

    private fun showAdIfAvailable(onRewardSuccess: () -> Unit) {
        rewardedAd?.let { ad ->
            ad.show(this) {
                // 광고를 끝까지 봤을 때만 호출됨
                onRewardSuccess()

                // 광고 다시 로드
                rewardedAd = null
                loadRewardedAd()
            }
        } ?: run {
            Log.d("Ad", "The rewarded ad wasn't ready yet.")
            loadRewardedAd()
        }
    }

    private fun checkCurrentNotification() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Toast.makeText(
                this,
                getString(R.string.permission_fail_message_notification),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getNotificationData() {
        val extraFragment = intent.getStringExtra("ExtraFragment")
        if (extraFragment != null && extraFragment == "Notification") {
            val postId = intent.getStringExtra("PostId")?.toInt()
            val memberId = intent.getStringExtra("MemberId")
        }
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
                            this,
                            getString(R.string.permission_fail_message_notification),
                            Toast.LENGTH_SHORT
                        ).show()
                        ActivityCompat.requestPermissions(
                            this,
                            notificationPermission,
                            1000
                        )
                    }
                    map[getString(R.string.permission_fail_final)]?.let {
                        Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        }
                        Toast.makeText(
                            this,
                            getString(R.string.permission_fail_final_message_notification),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                else -> {
                    //All request are permitted
                    // 알림 최초 허용시에 모든 알림 허용처리
                    accountViewModel.requestCurrentUserNotiDevicePermit(true)
                    accountViewModel.changeNoticeNotificationSetting(true)
                    settingNotificationViewModel.registerDeviceToken()
                    settingNotificationViewModel.requestReceiveAlarm()
                }
            }
        }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                // by them granting the POST_NOTIFICATION permission. This UI should provide the user
                // "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                // If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                permissionLauncherNotification.launch(notificationPermission)
            }
        }
    }

    companion object {
        val notificationPermission = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    }
}