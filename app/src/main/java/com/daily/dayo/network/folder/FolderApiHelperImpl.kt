package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.RequestCreateFolder
import com.daily.dayo.profile.model.ResponseFolder
import retrofit2.Response
import javax.inject.Inject

class FolderApiHelperImpl @Inject constructor(private val folderApiService: FolderApiService) : FolderApiHelper{
    override suspend fun requestCreateFolder(request: RequestCreateFolder): Response<ResponseFolder> = folderApiService.requestCreateFolder()
}