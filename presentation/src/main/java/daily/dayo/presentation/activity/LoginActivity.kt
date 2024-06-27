package daily.dayo.presentation.activity

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import daily.dayo.presentation.databinding.ActivityLoginBinding
import daily.dayo.presentation.viewmodel.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.common.dialog.DefaultDialogAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<AccountViewModel>()
    private var isReady = false
    private lateinit var updateDialog: AlertDialog
    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        createDialogUpdate()
        checkUpdate()
        setContentView(binding.root)
        setSystemBackClickListener()
        observeNetworkException()
        observeApiException()
        setSplash()
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
                        this@LoginActivity.finish()
                    } else {
                        navHostFragment?.childFragmentManager?.popBackStack()
                    }
                }
            }
        )
    }

    private fun setFCM() {
        GlobalScope.launch(Dispatchers.IO) {
            loginViewModel.getCurrentFcmToken().let { deviceToken ->
                loginViewModel.requestDeviceToken(deviceToken = deviceToken)
            }
        }
    }

    private fun autoLogin() {
        // refresh token 존재 여부 확인
        if (loginViewModel.getCurrentUserInfo().refreshToken != null) {
            // refresh token 존재 시 유효성 확인
            loginViewModel.requestRefreshToken()
        }
        loginSuccess()
    }

    private fun loginSuccess() {
        loginViewModel.loginSuccess.observe(this) { isSuccess ->
            if (isSuccess.peekContent()) {
                setFCM()
                if (loginViewModel.getCurrentUserInfo().nickname == "") {
                    isReady = true
                } else {
                    skipToNextActivity()
                }
            } else {
                Log.e(ContentValues.TAG, "자동 로그인 실패")
                isReady = true // 로그인 실패할 경우, Splash 화면을 지우고 Login Activity 내용물이 보이도록 설정
            }
        }
    }

    private fun skipToNextActivity() {
        isReady = false // 로그인이 성공한 경우에는 Splash 화면을 없애지 않고 바로 넘어가게 하기 위해 false 설정
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        this@LoginActivity.finish()
    }

    private fun setSplash() {
        binding.root.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isReady) {
                        binding.root.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else
                        false
                }
            }
        )
    }

    private fun checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnFailureListener {
                autoLogin()
        }

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                isReady = true
                updateDialog.show()
            } else {
                autoLogin()
            }
        }
    }

    private fun observeNetworkException() {
        loginViewModel.isErrorExceptionOccurred.observe(this) { isOccurred ->
            if (isOccurred.getContentIfNotHandled() == true) {
                Toast.makeText(this, "네트워크 연결 상태가 불안정합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeApiException() {
        loginViewModel.isApiErrorExceptionOccurred.observe(this) { isOccurred ->
            if (isOccurred.getContentIfNotHandled() == true)
                Toast.makeText(this, "서버와의 연결상태가 좋지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createDialogUpdate() {
        updateDialog = DefaultDialogAlert.createDialog(
            context = this,
            dialogDescriptionResId = R.string.app_version_force_update,
            confirmButtonFunction = {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                startActivity(intent)
                finish()
            }
        )
    }
}