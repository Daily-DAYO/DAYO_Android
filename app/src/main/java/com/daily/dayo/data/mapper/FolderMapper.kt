package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.folder.CreateFolderInPostResponse
import com.daily.dayo.data.datasource.remote.folder.CreateFolderResponse
import com.daily.dayo.data.datasource.remote.folder.EditFolderResponse
import com.daily.dayo.data.datasource.remote.folder.EditOrderDto
import com.daily.dayo.data.datasource.remote.folder.FolderDto
import com.daily.dayo.data.datasource.remote.folder.FolderInfoResponse
import com.daily.dayo.data.datasource.remote.folder.FolderPostDto
import com.daily.dayo.data.datasource.remote.folder.ListAllFolderResponse
import com.daily.dayo.data.datasource.remote.folder.ListAllMyFolderResponse
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.domain.model.FolderCreateInPostResponse
import com.daily.dayo.domain.model.FolderCreateResponse
import com.daily.dayo.domain.model.FolderEditResponse
import com.daily.dayo.domain.model.FolderInfo
import com.daily.dayo.domain.model.FolderOrder
import com.daily.dayo.domain.model.FolderPost
import com.daily.dayo.domain.model.Folders
import com.daily.dayo.domain.model.FoldersMine

fun CreateFolderResponse.toFolderCreateResponse(): FolderCreateResponse =
    FolderCreateResponse(folderId = id)

fun CreateFolderInPostResponse.toFolderCreateInPostResponse(): FolderCreateInPostResponse =
    FolderCreateInPostResponse(folderId = folderId)

fun EditFolderResponse.toFolderEditResponse(): FolderEditResponse =
    FolderEditResponse(folderId = folderId)

fun FolderInfoResponse.toFolderInfo(): FolderInfo =
    FolderInfo(
        memberId = memberId,
        name = name,
        postCount = postCount,
        privacy = privacy,
        subheading = subheading,
        thumbnailImage = thumbnailImage
    )

fun FolderInfoResponse.toFolder(): Folder =
    Folder(
        folderId = null,
        title = name,
        memberId = memberId,
        privacy = privacy,
        subheading = subheading,
        thumbnailImage = thumbnailImage,
        postCount = postCount
    )

fun FolderDto.toFolder(): Folder {
    if (subheading == null) subheading = ""
    return Folder(
        folderId = folderId,
        title = name,
        memberId = null,
        privacy = privacy,
        subheading = subheading,
        thumbnailImage = thumbnailImage,
        postCount = postCount
    )
}

fun ListAllFolderResponse.toFolders(): Folders {
    return Folders(
        count = count,
        data = data.map { it.toFolder() }
    )
}

fun ListAllMyFolderResponse.toFolersMine(): FoldersMine {
    return FoldersMine(
        count = count,
        data = data.map { it.toFolder() }
    )
}

fun FolderPostDto.toFolderPost(): FolderPost =
    FolderPost(
        createDate = createDate,
        postId = postId,
        thumbnailImage = thumbnailImage
    )

fun FolderOrder.toEditOrderDto(): EditOrderDto =
    EditOrderDto(
        folderId = folderId,
        orderIndex = orderIndex
    )

fun EditOrderDto.toFolderOrder(): FolderOrder =
    FolderOrder(
        folderId = folderId,
        orderIndex = orderIndex
    )