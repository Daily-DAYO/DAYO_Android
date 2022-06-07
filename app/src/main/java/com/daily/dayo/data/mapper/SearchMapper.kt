package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.search.SearchDto
import com.daily.dayo.domain.model.Search

fun SearchDto.toSearch() : Search =
    Search(
        postId = postId,
        thumbnailImage = thumbnailImage
    )