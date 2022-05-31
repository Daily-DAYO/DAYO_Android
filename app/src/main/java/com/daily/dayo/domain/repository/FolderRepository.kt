package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.folder.*
import com.daily.dayo.domain.model.Privacy
import okhttp3.MultipartBody
import retrofit2.Response

interface FolderRepository {

    suspend fun requestCreateFolder(
        name: String,
        privacy: Privacy,
        subheading: String?,
        thumbnailImage: MultipartBody.Part?
    ): Response<CreateFolderResponse>

    suspend fun requestEditFolder(
        folderId: Int,
        name: String,
        privacy: Privacy,
        subheading: String?,
        isFileChange: Boolean,
        thumbnailImage: MultipartBody.Part?
    ): Response<EditFolderResponse>

    suspend fun requestAllMyFolderList(): Response<ListAllMyFolderResponse>
    suspend fun requestAllFolderList(memberId: String): Response<ListAllFolderResponse>
    suspend fun requestCreateFolderInPost(body: CreateFolderInPostRequest): Response<CreateFolderInPostResponse>
    suspend fun requestDeleteFolder(folderId: Int): Response<Void>
    suspend fun requestDetailListFolder(folderId: Int): Response<DetailFolderResponse>
    suspend fun requestOrderFolder(body: List<EditOrderDto>): Response<Void>
}