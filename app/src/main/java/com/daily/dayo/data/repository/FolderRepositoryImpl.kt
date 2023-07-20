package com.daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.daily.dayo.data.datasource.remote.folder.*
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
    ): NetworkResponse<CreateFolderResponse> =
        folderApiService.requestCreateFolder(name, privacy.name, subheading, thumbnailImage)

    override suspend fun requestEditFolder(
        folderId: Int,
        name: String,
        privacy: Privacy,
        subheading: String?,
        isFileChange: Boolean,
        thumbnailImage: MultipartBody.Part?
    ): NetworkResponse<EditFolderResponse> =
        folderApiService.requestEditFolder(
            folderId,
            name,
            privacy.name,
            subheading,
            isFileChange,
            thumbnailImage
        )

    override suspend fun requestCreateFolderInPost(body: CreateFolderInPostRequest): NetworkResponse<CreateFolderInPostResponse> =
        folderApiService.requestCreateFolderInPost(body)

    override suspend fun requestDeleteFolder(folderId: Int): NetworkResponse<Void> =
        folderApiService.requestDeleteFolder(folderId)

    override suspend fun requestOrderFolder(body: List<EditOrderDto>): NetworkResponse<Void> =
        folderApiService.requestOrderFolder(body)

    override suspend fun requestAllFolderList(memberId: String): NetworkResponse<ListAllFolderResponse> =
        folderApiService.requestAllFolderList(memberId)

    override suspend fun requestAllMyFolderList(): NetworkResponse<ListAllMyFolderResponse> =
        folderApiService.requestAllMyFolderList()

    override suspend fun requestFolderInfo(folderId: Int): NetworkResponse<FolderInfoResponse> =
        folderApiService.requestFolderInfo(folderId)

    override suspend fun requestDetailListFolder(folderId: Int) = Pager(PagingConfig(pageSize = FOLDER_POST_PAGE_SIZE)) {
        FolderPagingSource(folderApiService, FOLDER_POST_PAGE_SIZE, folderId)
    }.flow

    companion object {
        private const val FOLDER_POST_PAGE_SIZE = 10
    }
}