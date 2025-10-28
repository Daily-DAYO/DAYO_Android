package daily.dayo.domain.usecase.search

import daily.dayo.domain.model.SearchOrder
import daily.dayo.domain.repository.SearchRepository
import javax.inject.Inject

class RequestSearchTagUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(tag: String, searchOrder: SearchOrder) = searchRepository.requestSearchTag(tag, searchOrder)
}