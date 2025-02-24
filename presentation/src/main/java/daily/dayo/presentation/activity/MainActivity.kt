package daily.dayo.presentation.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.databinding.ActivityMainBinding
import daily.dayo.presentation.fragment.home.HomeFragmentDirections
import daily.dayo.presentation.screen.main.MainScreen
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.SettingNotificationViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val accountViewModel by viewModels<AccountViewModel>()
    private val settingNotificationViewModel by viewModels<SettingNotificationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        // setContentView(binding.root)
        setSystemBackClickListener()
        checkCurrentNotification()
        initBottomNavigation()
        setBottomNaviVisibility()
        disableBottomNaviTooltip()
        getNotificationData()
        askNotificationPermission()
        setContent {
            DayoTheme {
                MainScreen()
            }
        }
    }

    private fun setSystemBackClickListener() {
        this.onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    val backStackEntryCount =
                        navHostFragment?.childFragmentManager?.backStackEntryCount

                    if (backStackEntryCount == 0) {
                        this@MainActivity.finish()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
        )
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
            if (postId != null) findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToPostFragment(
                    postId = postId
                )
            )
            else if (memberId != null) findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProfileFragment(
                    memberId = memberId
                )
            )
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
                    accountViewModel.requestCurrentUserNotiNoticePermit(false)
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
                    accountViewModel.requestCurrentUserNotiNoticePermit(true)
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

    private fun initBottomNavigation() {
        binding.bottomNavigationMainBar.setupWithNavController(findNavController())
    }

    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setBottomNaviVisibility() {
        binding.bottomNavigationMainBar.itemIconTintList = null
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            binding.layoutBottomNavigationMain.visibility = when (destination.id) {
                R.id.HomeFragment -> View.VISIBLE
                R.id.FeedFragment -> View.VISIBLE
                R.id.MyPageFragment -> View.VISIBLE
                else -> View.GONE
            }
        }
        // WriteFragment
        binding.bottomNavigationMainBar.setItemOnTouchListener(R.id.WriteFragment,
            object : View.OnTouchListener {
                var rect = Rect()
                var isInside = true
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            binding.bottomNavigationMainBar.menu.findItem(R.id.WriteFragment)
                                .setIcon(R.drawable.ic_write_filled)
                            rect = Rect(v!!.left, v.top, v.right, v.bottom)
                            isInside = true
                            return true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            isInside =
                                rect.contains(v!!.left + event.x.toInt(), v.top + event.y.toInt())
                            binding.bottomNavigationMainBar.clearFocus()
                            return false
                        }

                        MotionEvent.ACTION_UP -> {
                            binding.bottomNavigationMainBar.menu.findItem(R.id.WriteFragment)
                                .setIcon(R.drawable.ic_write)
                            if (isInside) {
                                when (findNavController().currentDestination!!.id) {
                                    R.id.HomeFragment -> findNavController().navigate(R.id.action_homeFragment_to_writeFragment)
                                    R.id.FeedFragment -> findNavController().navigate(R.id.action_feedFragment_to_writeFragment)
                                    R.id.MyPageFragment -> findNavController().navigate(R.id.action_myPageFragment_to_writeFragment)
                                }
                            }
                            return true
                        }

                        else -> return true
                    }
                }
            })
    }

    private fun disableBottomNaviTooltip() {
        binding.bottomNavigationMainBar.menu.forEach {
            val view = binding.bottomNavigationMainBar.findViewById<View>(it.itemId)
            view.setOnLongClickListener {
                true
            }
        }
    }

    fun setBottomNavigationIconClickListener(reselectedIconId: Int, reselectAction: () -> Unit) {
        binding.bottomNavigationMainBar.setOnItemReselectedListener {
            when (it.itemId) {
                reselectedIconId -> reselectAction()
            }
        }
    }

    companion object {
        val notificationPermission = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    }
}