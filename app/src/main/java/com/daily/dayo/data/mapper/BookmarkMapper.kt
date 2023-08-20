package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.bookmark.BookmarkPostDto
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkResponse
import com.daily.dayo.domain.model.BookmarkPost
import com.daily.dayo.domain.model.BookmarkPostResponse

fun BookmarkPostDto.toBookmarkPost() : BookmarkPost =
    BookmarkPost(
        postId = postId,
        thumbnailImage = thumbnailImage
    )

val CreateBookmarkResponse.toBookmarkPostResponse get() =
    BookmarkPostResponse(
        memberId = this.memberId,
        postId = this.postId
    )