package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.RequestCreateFolderInPost
import com.daily.dayo.profile.model.ResponseAllFolderList
import com.daily.dayo.profile.model.ResponseAllMyFolderList
import com.daily.dayo.profile.model.ResponseFolderId
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class FolderApiHelperImpl @Inject constructor(private val folderApiService: FolderApiService) : FolderApiHelper{
    override suspend fun requestCreateFolder(memberId : String, name:String, subheading:String?, thumbnailImage: MultipartBody.Part?): Response<ResponseFolderId> =
        folderApiService.requestCreateFolder(memberId,name,subheading,thumbnailImage)

    override suspend fun requestAllMyFolderList(): Response<ResponseAllMyFolderList> =
        folderApiService.requestAllMyFolderList()

    override suspend fun requestAllFolderList(memberId: String): Response<ResponseAllFolderList> =
        folderApiService.requestAllFolderList(memberId)

    override suspend fun requestCreateFolderInPost(request : RequestCreateFolderInPost) : Response<ResponseFolderId> =
        folderApiService.requestCreateFolderInPost(request)
}