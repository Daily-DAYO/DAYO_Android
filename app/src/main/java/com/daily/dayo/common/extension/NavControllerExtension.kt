package com.daily.dayo.common.extension

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.navigation.NavController

@SuppressLint("UnsafeNavigation")
fun NavController.navigateSafe(@IdRes currentDestinationId: Int, @IdRes action: Int, args: Bundle? = null): Boolean {
    return try {
        if (currentDestination?.id == currentDestinationId) {
            navigate(action, args)
        }
        true
    } catch (t: Throwable) {
        Log.e("NAVIGATION_SAFE_TAG", "navigation error for action $action")
        false
    }
}
