package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.folder.DetailFolderResponse
import com.daily.dayo.data.datasource.remote.folder.EditOrderDto
import com.daily.dayo.data.datasource.remote.folder.FolderDto
import com.daily.dayo.data.datasource.remote.folder.FolderPostDto
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.domain.model.FolderOrder
import com.daily.dayo.domain.model.FolderPost

fun FolderDto.toFolder() : Folder {
    if (subheading == null) subheading = ""
    return Folder(
        folderId = folderId,
        name = name,
        memberId = null,
        privacy = privacy,
        subheading = subheading,
        thumbnailImage = thumbnailImage,
        postCount = postCount,
        posts = null
    )
}

fun DetailFolderResponse.toFolder() : Folder {
    val posts = posts.map { it.toFolderPost() }
    return Folder(
        folderId = null,
        name = name,
        memberId = memberId,
        privacy = privacy,
        subheading = subheading,
        thumbnailImage = thumbnailImage,
        postCount = count,
        posts = posts
    )
}

fun FolderPostDto.toFolderPost() : FolderPost =
    FolderPost(
        postId = postId,
        thumbnailImage = thumbnailImage
    )

fun FolderOrder.toEditOrderDto() : EditOrderDto =
    EditOrderDto(
        folderId = folderId,
        orderIndex = orderIndex
    )