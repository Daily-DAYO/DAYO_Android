package com.daily.dayo.domain.usecase.folder

import com.daily.dayo.data.datasource.remote.folder.CreateFolderInPostRequest
import com.daily.dayo.domain.repository.FolderRepository
import javax.inject.Inject

class RequestCreateFolderInPostUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(body: CreateFolderInPostRequest) =
        folderRepository.requestCreateFolderInPost(body)
}