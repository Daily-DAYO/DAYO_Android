package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.folder.*
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Privacy
import okhttp3.MultipartBody

interface FolderRepository {

    suspend fun requestCreateFolder(
        name: String,
        privacy: Privacy,
        subheading: String?,
        thumbnailImage: MultipartBody.Part?
    ): NetworkResponse<CreateFolderResponse>

    suspend fun requestEditFolder(
        folderId: Int,
        name: String,
        privacy: Privacy,
        subheading: String?,
        isFileChange: Boolean,
        thumbnailImage: MultipartBody.Part?
    ): NetworkResponse<EditFolderResponse>

    suspend fun requestAllMyFolderList(): NetworkResponse<ListAllMyFolderResponse>
    suspend fun requestAllFolderList(memberId: String): NetworkResponse<ListAllFolderResponse>
    suspend fun requestCreateFolderInPost(body: CreateFolderInPostRequest): NetworkResponse<CreateFolderInPostResponse>
    suspend fun requestDeleteFolder(folderId: Int): NetworkResponse<Void>
    suspend fun requestDetailListFolder(folderId: Int): NetworkResponse<DetailFolderResponse>
    suspend fun requestOrderFolder(body: List<EditOrderDto>): NetworkResponse<Void>
}