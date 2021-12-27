package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.ResponseFolder
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class FolderApiHelperImpl @Inject constructor(private val folderApiService: FolderApiService) : FolderApiHelper{
    override suspend fun requestCreateFolder(memberId : String, name:String, subheading:String?, thumbnailImage: MultipartBody.Part?): Response<ResponseFolder> =
        folderApiService.requestCreateFolder(memberId,name,subheading,thumbnailImage)
}