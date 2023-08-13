package com.daily.dayo.common.image

import android.net.Uri
import com.daily.dayo.DayoApplication

object ImageUploadUtil {
    private val PERMIT_IMAGE_EXTENSIONS = listOf("jpg", "jpeg", "png", "webp")

    // Get Image Extension
    val Uri.extension: String
        get() = DayoApplication.applicationContext().contentResolver
            .getType(this)
            .toString()
            .split("/")[1]

    val String.isPermitExtension: Boolean get() = this in PERMIT_IMAGE_EXTENSIONS
}