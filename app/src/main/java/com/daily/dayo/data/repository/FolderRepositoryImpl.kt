package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.folder.*
import com.daily.dayo.domain.model.Privacy
import com.daily.dayo.domain.repository.FolderRepository
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val folderApiService: FolderApiService
) : FolderRepository {

    override suspend fun requestCreateFolder(
        name: String,
        privacy: Privacy,
        subheading: String?,
        thumbnailImage: MultipartBody.Part?
    ): Response<CreateFolderResponse> =
        folderApiService.requestCreateFolder(name, privacy.name, subheading, thumbnailImage)

    override suspend fun requestEditFolder(
        folderId: Int,
        name: String,
        privacy: Privacy,
        subheading: String?,
        isFileChange: Boolean,
        thumbnailImage: MultipartBody.Part?
    ): Response<EditFolderResponse> =
        folderApiService.requestEditFolder(
            folderId,
            name,
            privacy.name,
            subheading,
            isFileChange,
            thumbnailImage
        )

    override suspend fun requestAllMyFolderList(): Response<ListAllMyFolderResponse> =
        folderApiService.requestAllMyFolderList()

    override suspend fun requestAllFolderList(memberId: String): Response<ListAllFolderResponse> =
        folderApiService.requestAllFolderList(memberId)

    override suspend fun requestCreateFolderInPost(body: CreateFolderInPostRequest): Response<CreateFolderInPostResponse> =
        folderApiService.requestCreateFolderInPost(body)

    override suspend fun requestDeleteFolder(folderId: Int): Response<Void> =
        folderApiService.requestDeleteFolder(folderId)

    override suspend fun requestDetailListFolder(folderId: Int): Response<DetailFolderResponse> =
        folderApiService.requestDetailListFolder(folderId)

    override suspend fun requestOrderFolder(body: List<EditOrderDto>): Response<Void> =
        folderApiService.requestOrderFolder(body)
}