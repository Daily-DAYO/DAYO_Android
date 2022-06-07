package com.daily.dayo.domain.usecase.folder

import com.daily.dayo.domain.repository.FolderRepository
import javax.inject.Inject

class RequestDetailListFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(folderId: Int) =
        folderRepository.requestDetailListFolder(folderId)
}