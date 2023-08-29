package daily.dayo.domain.usecase.search

import daily.dayo.domain.repository.SearchRepository
import javax.inject.Inject

class ClearSearchKeywordRecentUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke() =
        searchRepository.clearSearchKeywordRecent()
}