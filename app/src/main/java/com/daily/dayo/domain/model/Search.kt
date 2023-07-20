package com.daily.dayo.domain.model

import android.graphics.Bitmap

data class Search(
    val postId: Int,
    val thumbnailImage: String,
    var preLoadThumbnail: Bitmap? = null
)