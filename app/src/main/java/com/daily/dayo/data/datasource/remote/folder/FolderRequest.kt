package com.daily.dayo.data.datasource.remote.folder

import daily.dayo.domain.model.Privacy
import com.google.gson.annotations.SerializedName

data class CreateFolderInPostRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("privacy")
    val privacy: Privacy
)