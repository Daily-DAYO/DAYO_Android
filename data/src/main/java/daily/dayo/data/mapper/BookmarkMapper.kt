package daily.dayo.data.mapper

import daily.dayo.data.datasource.remote.bookmark.BookmarkPostDto
import daily.dayo.data.datasource.remote.bookmark.CreateBookmarkResponse
import daily.dayo.domain.model.BookmarkPost
import daily.dayo.domain.model.BookmarkPostResponse

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