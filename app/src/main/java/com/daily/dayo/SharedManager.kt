package com.daily.dayo
import android.content.Context
import android.content.SharedPreferences
import com.daily.dayo.PreferenceHelper.get
import com.daily.dayo.PreferenceHelper.set
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = PreferenceHelper.defaultPrefs(context)

    fun saveCurrentUser(user: User){
        prefs["id"]=user.id
        prefs["accessToken"]=user.accessToken
        prefs["refreshToken"]=user.refreshToken
    }
    fun getCurrentUser() :User{
        return User().apply {
            id= prefs["id",""]
            accessToken= prefs["accessToken",""]
            refreshToken= prefs["refreshToken",""]
        }
    }
}
