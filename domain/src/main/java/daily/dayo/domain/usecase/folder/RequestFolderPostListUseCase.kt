package daily.dayo.domain.usecase.folder

import daily.dayo.domain.repository.FolderRepository
import javax.inject.Inject

class RequestFolderPostListUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(folderId: Long) =
        folderRepository.requestDetailListFolder(folderId)
}