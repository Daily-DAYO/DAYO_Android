package daily.dayo.domain.usecase.folder

import daily.dayo.domain.model.Privacy
import daily.dayo.domain.repository.FolderRepository
import javax.inject.Inject

class RequestCreateFolderInPostUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(name: String, description: String, privacy: Privacy) =
        folderRepository.requestCreateFolderInPost(
            name = name,
            description = description,
            privacy = privacy
        )
}