package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.ResponseAllFolderList
import com.daily.dayo.profile.model.ResponseCreateFolder
import okhttp3.MultipartBody
import retrofit2.Response

interface FolderApiHelper {
    suspend fun requestCreateFolder(memberId : String, name:String, subheading:String?, thumbnailImage: MultipartBody.Part?): Response<ResponseCreateFolder>

    suspend fun requestAllFolderList(memberId: String) : Response<ResponseAllFolderList>
}