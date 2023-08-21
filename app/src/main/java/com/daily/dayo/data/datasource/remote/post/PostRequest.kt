package com.daily.dayo.data.datasource.remote.post

import daily.dayo.domain.model.Category
import com.google.gson.annotations.SerializedName

data class EditPostRequest (
    @SerializedName("category")
    val category: Category,
    @SerializedName("contents")
    val contents : String,
    @SerializedName("folderId")
    val folderId : Int,
    @SerializedName("hashtags")
    val hashtags : List<String>
)