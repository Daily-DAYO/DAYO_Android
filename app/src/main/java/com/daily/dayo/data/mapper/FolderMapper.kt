package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.folder.EditOrderDto
import com.daily.dayo.data.datasource.remote.folder.FolderDto
import com.daily.dayo.data.datasource.remote.folder.FolderInfoResponse
import com.daily.dayo.data.datasource.remote.folder.FolderPostDto
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.domain.model.FolderOrder
import com.daily.dayo.domain.model.FolderPost

fun FolderInfoResponse.toFolder() : Folder =
    Folder(
        folderId = null,
        title = name,
        memberId = null,
        privacy = privacy,
        subheading = subheading,
        thumbnailImage = thumbnailImage,
        postCount = postCount
    )

fun FolderDto.toFolder() : Folder {
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

fun FolderPostDto.toFolderPost() : FolderPost =
    FolderPost(
        createDate = createDate,
        postId = postId,
        thumbnailImage = thumbnailImage
    )

fun FolderOrder.toEditOrderDto() : EditOrderDto =
    EditOrderDto(
        folderId = folderId,
        orderIndex = orderIndex
    )