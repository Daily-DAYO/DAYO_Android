package com.daily.dayo.domain.usecase.search

import com.daily.dayo.domain.repository.SearchRepository
import javax.inject.Inject

class RequestSearchTotalCountUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(tag: String) =
        searchRepository.requestSearchTotalCount(tag, 0)
}