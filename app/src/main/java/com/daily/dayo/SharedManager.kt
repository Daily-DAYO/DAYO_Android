package com.daily.dayo
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.daily.dayo.PreferenceHelper.get
import com.daily.dayo.PreferenceHelper.set
import com.daily.dayo.login.LoginResponse
import com.daily.dayo.login.MemberResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import java.lang.reflect.Member
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = PreferenceHelper.defaultPrefs(context)

    fun saveCurrentUser(userInfo: Any?) = when (userInfo) {
        is LoginResponse -> {
            prefs["accessToken"] = userInfo.accessToken
            prefs["refreshToken"] = userInfo.refreshToken
        }
        is MemberResponse -> {
            prefs["email"] = userInfo.email
            prefs["memberId"] = userInfo.memberId
            prefs["nickname"] = userInfo.nickname
            prefs["profileImg"] = userInfo.profileImg
        }
        else -> {}
    }
    fun getCurrentUser() :User{
        return User().apply {
            accessToken= prefs["accessToken",""]
            refreshToken= prefs["refreshToken",""]
            email = prefs["email",""]
            memberId= prefs["memberId",""]
            nickname = prefs["nickname",""]
            profileImg = prefs["profileImg",""]
        }
    }
}
