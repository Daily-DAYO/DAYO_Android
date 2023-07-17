package com.daily.dayo.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import com.daily.dayo.common.PreferenceHelper
import com.daily.dayo.common.PreferenceHelper.get
import com.daily.dayo.common.PreferenceHelper.set
import com.daily.dayo.data.datasource.remote.member.MemberInfoResponse
import com.daily.dayo.data.datasource.remote.member.MemberOAuthResponse
import com.daily.dayo.data.datasource.remote.member.MemberSignInResponse
import com.daily.dayo.domain.model.User
import com.google.gson.JsonArray
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedManager @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = PreferenceHelper.defaultPrefs(context)

    fun saveCurrentUser(userInfo: Any?) = when (userInfo) {
        is MemberOAuthResponse -> {
            prefs["accessToken"] = userInfo.accessToken
            prefs["refreshToken"] = userInfo.refreshToken
        }
        is MemberSignInResponse -> {
            prefs["accessToken"] = userInfo.accessToken
            prefs["refreshToken"] = userInfo.refreshToken
        }
        is MemberInfoResponse -> {
            prefs["email"] = userInfo.email
            prefs["memberId"] = userInfo.memberId
            prefs["nickname"] = userInfo.nickname
            prefs["profileImg"] = userInfo.profileImg
        }
        else -> {}
    }

    fun getCurrentUser(): User {
        return User().apply {
            accessToken = prefs["accessToken", ""]
            refreshToken = prefs["refreshToken", ""]
            email = prefs["email", ""]
            memberId = prefs["memberId", ""]
            nickname = prefs["nickname", ""]
            profileImg = prefs["profileImg", ""]
        }
    }

    fun setAccessToken(accessToken: String) {
        prefs["accessToken"] = accessToken
    }

    var notiDevicePermit: Boolean
        get() = prefs["notiDevicePermit", true]
        set(value) {
            prefs["notiDevicePermit"] = value
        }

    var notiNoticePermit: Boolean
        get() = prefs["notiNoticePermit", true]
        set(value) {
            prefs["notiNoticePermit"] = value
        }

    fun setSearchKeywordRecent(searchKeywordRecent: ArrayList<String>) {
        val jsonArr = JsonArray()
        for (i in searchKeywordRecent) {
            jsonArr.add(i)
        }

        var result = jsonArr.toString()
        prefs["recentSearchKeyword"] = result
    }

    fun getSearchKeywordRecent(): ArrayList<String> {
        val result = prefs["recentSearchKeyword", ""]
        val resultArr = ArrayList<String>()
        val jsonArr: JSONArray = if (result.isEmpty()) {
            JSONArray()
        } else {
            JSONArray(result)
        }

        if (jsonArr.length() != 0) {
            for (i in 0 until jsonArr.length()) {
                resultArr.add(jsonArr.optString(i))
            }
        }
        return resultArr
    }

    fun clearPreferences() {
        prefs.edit().clear().apply()
    }
}
