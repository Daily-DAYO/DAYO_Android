package com.daily.dayo
import android.content.Context
import android.content.SharedPreferences
import com.daily.dayo.PreferenceHelper.get
import com.daily.dayo.PreferenceHelper.set
import com.daily.dayo.login.model.LoginResponse
import com.daily.dayo.login.model.MemberResponse
import com.google.gson.JsonArray
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
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
    fun setAccessToken(accessToken:String) {
        prefs["accessToken"] = accessToken
    }

    var fcmDeviceToken: String
        get() = prefs["deviceToken"]
        set(value) { prefs["deviceToken"] = value }

    fun setSearchKeywordRecent(searchKeywordRecent:ArrayList<String>) {
        var jsonArr = JsonArray()
        for(i in searchKeywordRecent) {
            jsonArr.add(i)
        }

        var result = jsonArr.toString()
        prefs["recentSearchKeyword"] = result
    }
    fun getSearchKeywordRecent(): ArrayList<String> {
        val result = prefs["recentSearchKeyword", ""]
        var resultArr = ArrayList<String>()
        val jsonArr = JSONArray(result)

        if(jsonArr.length() != 0) {
            for(i in 0 until jsonArr.length()){
                resultArr.add(jsonArr.optString(i))
            }
        }
        return resultArr
    }
}
