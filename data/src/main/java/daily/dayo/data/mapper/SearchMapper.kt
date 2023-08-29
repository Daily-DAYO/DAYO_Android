package daily.dayo.data.mapper

import daily.dayo.data.datasource.remote.search.SearchDto
import daily.dayo.domain.model.Search

fun SearchDto.toSearch() : Search =
    Search(
        postId = postId,
        thumbnailImage = thumbnailImage
    )