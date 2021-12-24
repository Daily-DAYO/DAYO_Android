package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.ResponseFolder
import retrofit2.Response
import retrofit2.http.POST

interface FolderApiService {

    @POST("/api/v1/folders")
    suspend fun requestCreateFolder(): Response<ResponseFolder>
}