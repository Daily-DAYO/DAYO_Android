package daily.dayo.domain.usecase.search

import daily.dayo.domain.repository.SearchRepository
import javax.inject.Inject

class RequestSearchTagUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(tag: String) = searchRepository.requestSearchTag(tag)
}