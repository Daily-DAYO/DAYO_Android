package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.*
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

    override suspend fun requestDeleteFolder(folderId:Int):Response<Void> =
        folderApiService.requestDeleteFolder(folderId)

    override suspend fun requestDetailListFolder(folderId: Int) : Response<ResponseDetailListFolder> =
        folderApiService.requestDetailListFolder(folderId)

    override suspend fun requestEditFolder(folderId:Int, name:String, privacy:String, subheading:String?, isFileChange:Boolean, thumbnailImage: MultipartBody.Part?): Response<ResponseFolderId> =
        folderApiService.requestEditFolder(folderId,name,privacy,subheading,isFileChange,thumbnailImage)

    override suspend fun requestOrderFolder(body: List<FolderOrder>):Response<Void> =
        folderApiService.requestOrderFolder(body)
}