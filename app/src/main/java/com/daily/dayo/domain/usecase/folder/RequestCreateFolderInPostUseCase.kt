package com.daily.dayo.domain.usecase.folder

import com.daily.dayo.domain.model.Privacy
import com.daily.dayo.domain.repository.FolderRepository
import javax.inject.Inject

class RequestCreateFolderInPostUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(name: String, privacy: Privacy) =
        folderRepository.requestCreateFolderInPost(name = name, privacy = privacy)
}