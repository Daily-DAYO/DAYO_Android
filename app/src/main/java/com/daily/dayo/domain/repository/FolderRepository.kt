package com.daily.dayo.domain.repository

import androidx.paging.PagingData
import com.daily.dayo.data.datasource.remote.folder.*
import com.daily.dayo.domain.model.FolderPost
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Privacy
import kotlinx.coroutines.flow.Flow
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

    suspend fun requestCreateFolderInPost(body: CreateFolderInPostRequest): NetworkResponse<CreateFolderInPostResponse>
    suspend fun requestDeleteFolder(folderId: Int): NetworkResponse<Void>
    suspend fun requestOrderFolder(body: List<EditOrderDto>): NetworkResponse<Void>
    suspend fun requestAllFolderList(memberId: String): NetworkResponse<ListAllFolderResponse>
    suspend fun requestAllMyFolderList(): NetworkResponse<ListAllMyFolderResponse>
    suspend fun requestFolderInfo(folderId: Int): NetworkResponse<FolderInfoResponse>
    suspend fun requestDetailListFolder(folderId: Int): Flow<PagingData<FolderPost>>
}