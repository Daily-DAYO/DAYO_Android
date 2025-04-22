package daily.dayo.domain.repository

import androidx.paging.PagingData
import daily.dayo.domain.model.FolderCreateInPostResponse
import daily.dayo.domain.model.FolderCreateResponse
import daily.dayo.domain.model.FolderEditResponse
import daily.dayo.domain.model.FolderInfo
import daily.dayo.domain.model.FolderOrder
import daily.dayo.domain.model.FolderPost
import daily.dayo.domain.model.Folders
import daily.dayo.domain.model.FoldersMine
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Privacy
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface FolderRepository {

    suspend fun requestCreateFolder(
        name: String,
        privacy: Privacy,
        subheading: String?,
        thumbnailImage: MultipartBody.Part?
    ): NetworkResponse<FolderCreateResponse>

    suspend fun requestEditFolder(
        folderId: Long,
        name: String,
        privacy: Privacy,
        subheading: String?,
        isFileChange: Boolean,
        thumbnailImage: MultipartBody.Part?
    ): NetworkResponse<FolderEditResponse>

    suspend fun requestCreateFolderInPost(
        name: String,
        description: String,
        privacy: Privacy
    ): NetworkResponse<FolderCreateInPostResponse>

    suspend fun requestDeleteFolder(folderId: Long): NetworkResponse<Void>
    suspend fun requestOrderFolder(folderOrders: List<FolderOrder>): NetworkResponse<Void>
    suspend fun requestAllFolderList(memberId: String): NetworkResponse<Folders>
    suspend fun requestAllMyFolderList(): NetworkResponse<FoldersMine>
    suspend fun requestFolderInfo(folderId: Long): NetworkResponse<FolderInfo>
    suspend fun requestDetailListFolder(folderId: Long): Flow<PagingData<FolderPost>>
}