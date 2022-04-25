package com.daily.dayo.network.search

import com.daily.dayo.search.model.RequestSearchTag
import com.daily.dayo.search.model.ResponseSearchTag
import retrofit2.Response
import javax.inject.Inject

class SearchApiHelperImpl @Inject constructor(private val searchApiService: SearchApiService) :
    SearchApiHelper {
    override suspend fun requestSearchTag(requestSearchTag: RequestSearchTag): Response<ResponseSearchTag> =
        searchApiService.requestSearchTag(requestSearchTag.tag)
}