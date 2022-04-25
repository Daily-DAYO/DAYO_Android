package com.daily.dayo.network.search

import com.daily.dayo.search.model.RequestSearchTag
import com.daily.dayo.search.model.ResponseSearchTag
import retrofit2.Response

interface SearchApiHelper {
    suspend fun requestSearchTag(requestSearchTag: RequestSearchTag): Response<ResponseSearchTag>
}