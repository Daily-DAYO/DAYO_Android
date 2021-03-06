package com.daily.dayo.domain.usecase.search

import com.daily.dayo.domain.repository.SearchRepository
import javax.inject.Inject

class RequestSearchKeywordUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(keyword: String) =
        searchRepository.requestSearchKeyword(keyword)
}