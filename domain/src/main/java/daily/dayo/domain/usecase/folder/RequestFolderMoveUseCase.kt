package daily.dayo.domain.usecase.folder

import daily.dayo.domain.repository.FolderRepository
import javax.inject.Inject

class RequestFolderMoveUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(postIdList: List<Long>, targetFolderId: Long) =
        folderRepository.requestFolderMove(postIdList, targetFolderId)
}
