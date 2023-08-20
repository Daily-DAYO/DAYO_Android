package com.daily.dayo.domain.usecase.folder

import com.daily.dayo.domain.model.FolderOrder
import com.daily.dayo.domain.repository.FolderRepository
import javax.inject.Inject

class RequestOrderFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(folderOrder: List<FolderOrder>) =
        folderRepository.requestOrderFolder(folderOrder)
}