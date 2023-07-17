package com.daily.dayo.common

import android.content.res.Resources


val Int.Px: Int
    get() {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density).toInt()
    }