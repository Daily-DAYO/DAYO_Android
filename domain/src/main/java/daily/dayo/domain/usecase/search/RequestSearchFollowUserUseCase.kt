package daily.dayo.domain.usecase.search

import androidx.paging.PagingData
import daily.dayo.domain.model.SearchUser
import daily.dayo.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestSearchFollowUserUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(nickname: String): Flow<PagingData<SearchUser>> =
        searchRepository.requestSearchFollowUser(nickname)
}