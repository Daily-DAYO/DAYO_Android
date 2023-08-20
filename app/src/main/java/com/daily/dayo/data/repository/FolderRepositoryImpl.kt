package com.daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.daily.dayo.data.datasource.remote.folder.*
import com.daily.dayo.data.mapper.toEditOrderDto
import com.daily.dayo.data.mapper.toFolderCreateInPostResponse
import com.daily.dayo.data.mapper.toFolderCreateResponse
import com.daily.dayo.data.mapper.toFolderEditResponse
import com.daily.dayo.data.mapper.toFolderInfo
import com.daily.dayo.data.mapper.toFolders
import com.daily.dayo.data.mapper.toFolersMine
import com.daily.dayo.domain.model.FolderCreateInPostResponse
import com.daily.dayo.domain.model.FolderCreateResponse
import com.daily.dayo.domain.model.FolderEditResponse
import com.daily.dayo.domain.model.FolderInfo
import com.daily.dayo.domain.model.FolderOrder
import com.daily.dayo.domain.model.Folders
import com.daily.dayo.domain.model.FoldersMine
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Privacy
import com.daily.dayo.domain.repository.FolderRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val folderApiService: FolderApiService
) : FolderRepository {

    override suspend fun requestCreateFolder(
        name: String,
        privacy: Privacy,
        subheading: String?,
        thumbnailImage: MultipartBody.Part?
    ): NetworkResponse<FolderCreateResponse> =
        when (val response =
            folderApiService.requestCreateFolder(name, privacy.name, subheading, thumbnailImage)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toFolderCreateResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestEditFolder(
        folderId: Int,
        name: String,
        privacy: Privacy,
        subheading: String?,
        isFileChange: Boolean,
        thumbnailImage: MultipartBody.Part?
    ): NetworkResponse<FolderEditResponse> =
        when (val response = folderApiService.requestEditFolder(
            folderId,
            name,
            privacy.name,
            subheading,
            isFileChange,
            thumbnailImage
        )) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toFolderEditResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestCreateFolderInPost(name: String, privacy: Privacy): NetworkResponse<FolderCreateInPostResponse> =
        when (val response =
            folderApiService.requestCreateFolderInPost(CreateFolderInPostRequest(name = name, privacy = privacy))) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toFolderCreateInPostResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestDeleteFolder(folderId: Int): NetworkResponse<Void> =
        folderApiService.requestDeleteFolder(folderId)

    override suspend fun requestOrderFolder(folderOrders: List<FolderOrder>): NetworkResponse<Void> =
        folderApiService.requestOrderFolder(folderOrders.map { it.toEditOrderDto() })

    override suspend fun requestAllFolderList(memberId: String): NetworkResponse<Folders> =
        when (val response = folderApiService.requestAllFolderList(memberId)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toFolders())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestAllMyFolderList(): NetworkResponse<FoldersMine> =
        when (val response = folderApiService.requestAllMyFolderList()) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toFolersMine())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestFolderInfo(folderId: Int): NetworkResponse<FolderInfo> =
        when (val response = folderApiService.requestFolderInfo(folderId)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toFolderInfo())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestDetailListFolder(folderId: Int) =
        Pager(PagingConfig(pageSize = FOLDER_POST_PAGE_SIZE)) {
            FolderPagingSource(folderApiService, FOLDER_POST_PAGE_SIZE, folderId)
        }.flow

    companion object {
        private const val FOLDER_POST_PAGE_SIZE = 10
    }
}