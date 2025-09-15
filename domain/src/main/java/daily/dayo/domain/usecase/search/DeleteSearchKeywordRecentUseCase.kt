package daily.dayo.domain.usecase.search

import daily.dayo.domain.model.SearchHistoryType
import daily.dayo.domain.repository.SearchRepository
import javax.inject.Inject

class DeleteSearchKeywordRecentUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(keyword: String, keywordType: SearchHistoryType) =
        searchRepository.deleteSearchKeywordRecent(keyword, keywordType)
}