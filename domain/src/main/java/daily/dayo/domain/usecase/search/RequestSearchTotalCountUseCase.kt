package daily.dayo.domain.usecase.search

import daily.dayo.domain.model.SearchHistoryType
import daily.dayo.domain.repository.SearchRepository
import javax.inject.Inject

class RequestSearchTotalCountUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(tag: String, searchHistoryType: SearchHistoryType) =
        searchRepository.requestSearchTotalCount(tag, 0, searchHistoryType)
}