package com.daily.dayo.domain.model

import android.graphics.Bitmap

data class LikePost(
    val postId: Int,
    val thumbnailImage: String,
    var preLoadThumbnail: Bitmap? = null
)