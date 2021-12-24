package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.RequestCreateFolder
import com.daily.dayo.profile.model.ResponseFolder
import retrofit2.Response

interface FolderApiHelper {
    suspend fun requestCreateFolder(request: RequestCreateFolder): Response<ResponseFolder>
}