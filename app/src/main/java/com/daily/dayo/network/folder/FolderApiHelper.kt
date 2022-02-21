package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.*
import okhttp3.MultipartBody
import retrofit2.Response

interface FolderApiHelper {
    suspend fun requestCreateFolder(memberId : String, name:String, subheading:String?, thumbnailImage: MultipartBody.Part?): Response<ResponseFolderId>
    suspend fun requestAllMyFolderList() : Response<ResponseAllMyFolderList>
    suspend fun requestAllFolderList(memberId: String) : Response<ResponseAllFolderList>
    suspend fun requestCreateFolderInPost(request: RequestCreateFolderInPost) : Response<ResponseFolderId>
    suspend fun requestDeleteFolder(folderId : Int) : Response<Void>
    suspend fun requestDetailListFolder(folderId: Int) : Response<ResponseDetailListFolder>
    suspend fun requestEditFolder(folderId:Int, name:String, privacy:String, subheading:String?, isFileChange:Boolean, thumbnailImage: MultipartBody.Part?): Response<ResponseFolderId>
    suspend fun requestOrderFolder(body: List<FolderOrder>):Response<Void>
}