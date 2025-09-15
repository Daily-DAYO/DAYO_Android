package daily.dayo.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import daily.dayo.data.util.PreferenceHelper
import daily.dayo.data.util.PreferenceHelper.get
import daily.dayo.data.util.PreferenceHelper.set
import com.google.gson.JsonArray
import dagger.hilt.android.qualifiers.ApplicationContext
import daily.dayo.domain.model.SearchHistory
import daily.dayo.domain.model.SearchHistoryDetail
import daily.dayo.domain.model.User
import daily.dayo.domain.model.UserTokens
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = PreferenceHelper.defaultPrefs(context)
    private val KEY_SEARCH_HISTORY = "search_history"

    fun saveCurrentUser(userInfo: Any?) = when (userInfo) {
        is UserTokens -> {
            prefs["accessToken"] = userInfo.accessToken
            prefs["refreshToken"] = userInfo.refreshToken
        }
        is User -> {
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

    fun saveSearchHistory(searchHistory: SearchHistory) {
        val gson = Gson()
        val json = gson.toJson(searchHistory)
        val editor = prefs.edit()
        editor.putString(KEY_SEARCH_HISTORY, json)
        editor.apply()
    }

    fun getSearchKeywordRecent(): SearchHistory {
        val json = prefs.getString(KEY_SEARCH_HISTORY, null)
        val gson = Gson()
        return gson.fromJson(json, SearchHistory::class.java) ?: SearchHistory(0, mutableListOf())
    }

    fun updateSearchHistory(newItem: SearchHistoryDetail) {
        var searchHistory = getSearchKeywordRecent()
        // History에 Type 구분을 하지 않음에 따라 OR문으로 처리
        val existingItem = searchHistory.data.find {
            it.history == newItem.history || it.searchHistoryType == newItem.searchHistoryType
        }

        if (existingItem != null) {
            searchHistory = searchHistory.copy(
                count = searchHistory.count - 1,
                data = searchHistory.data.filter {
                    it.history != existingItem.history || it.searchHistoryType != existingItem.searchHistoryType
                }.toMutableList()
            )
        }

        searchHistory = searchHistory.copy(
            count = searchHistory.count + 1,
            data = listOf(newItem) + searchHistory.data
        )
        saveSearchHistory(searchHistory)
    }

    fun clearSearchHistory() {
        val editor = prefs.edit()
        editor.remove(KEY_SEARCH_HISTORY)
        editor.apply()
    }

    fun clearPreferences() {
        prefs.edit().clear().apply()
    }
}
