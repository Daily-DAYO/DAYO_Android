package com.daily.dayo.domain.usecase.folder

import com.daily.dayo.domain.repository.FolderRepository
import javax.inject.Inject

class RequestDeleteFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(folderId: Int) =
        folderRepository.requestDeleteFolder(folderId)
}