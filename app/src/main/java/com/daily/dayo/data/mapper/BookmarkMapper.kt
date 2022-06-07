package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.bookmark.BookmarkPostDto
import com.daily.dayo.domain.model.BookmarkPost

fun BookmarkPostDto.toBookmarkPost() : BookmarkPost =
    BookmarkPost(
        postId = postId,
        thumbnailImage = thumbnailImage
    )