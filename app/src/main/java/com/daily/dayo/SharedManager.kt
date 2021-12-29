package com.daily.dayo
import android.content.Context
import android.content.SharedPreferences
import com.daily.dayo.PreferenceHelper.get
import com.daily.dayo.PreferenceHelper.set
import javax.inject.Inject

class SharedManager @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = PreferenceHelper.defaultPrefs(context)

    fun saveCurrentUser(user: User){
        prefs["id"]=user.id
    }
    fun getCurrentUser() :User{
        return User().apply {
            id= prefs["id",""]
        }
    }
}
