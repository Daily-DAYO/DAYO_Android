package com.daily.dayo.domain.usecase.folder

import com.daily.dayo.data.datasource.remote.folder.CreateFolderResponse
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Privacy
import com.daily.dayo.domain.repository.FolderRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class RequestCreateFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(
        name: String,
        privacy: Privacy,
        subheading: String?,
        thumbnailImg: File?
    ): NetworkResponse<CreateFolderResponse> {
        val requestThumbnailImage : MultipartBody.Part? = if(thumbnailImg!=null) {
            MultipartBody.Part.createFormData("thumbnailImage",thumbnailImg.name, RequestBody.create("image/*".toMediaTypeOrNull(),thumbnailImg) )
        } else null
        return folderRepository.requestCreateFolder(
            name,
            privacy,
            subheading,
            requestThumbnailImage
        )
    }
}