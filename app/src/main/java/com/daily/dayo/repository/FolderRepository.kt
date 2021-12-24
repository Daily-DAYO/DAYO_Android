package com.daily.dayo.repository

import com.daily.dayo.network.folder.FolderApiHelper
import com.daily.dayo.profile.model.RequestCreateFolder
import com.daily.dayo.profile.model.ResponseFolder
import retrofit2.Response
import javax.inject.Inject

class FolderRepository @Inject constructor(private val folderApiHelper: FolderApiHelper){
    suspend fun requestCreateFolder(request: RequestCreateFolder) : Response<ResponseFolder> =
        folderApiHelper.requestCreateFolder(request)
}