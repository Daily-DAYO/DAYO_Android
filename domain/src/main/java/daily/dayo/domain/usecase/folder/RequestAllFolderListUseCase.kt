package daily.dayo.domain.usecase.folder

import daily.dayo.domain.repository.FolderRepository
import javax.inject.Inject

class RequestAllFolderListUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(memberId: String) =
        folderRepository.requestAllFolderList(memberId)
}