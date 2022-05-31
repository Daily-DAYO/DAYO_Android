package com.daily.dayo.domain.usecase.search

import com.daily.dayo.domain.repository.SearchRepository
import javax.inject.Inject

class DeleteSearchKeywordRecentUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(keyword: String) =
        searchRepository.deleteSearchKeywordRecent(keyword)
}