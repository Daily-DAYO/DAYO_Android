package daily.dayo.domain.usecase.folder

import daily.dayo.domain.model.FolderEditResponse
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Privacy
import daily.dayo.domain.repository.FolderRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class RequestEditFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(
        folderId: Int,
        name: String,
        privacy: Privacy,
        subheading: String?,
        isFileChange: Boolean,
        thumbnailImg: File?
    ): NetworkResponse<FolderEditResponse> {
        val requestThumbnailImage: MultipartBody.Part? = if (thumbnailImg != null)
            MultipartBody.Part.createFormData(
                "thumbnailImage",
                thumbnailImg.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), thumbnailImg)
            )
        else null
        return folderRepository.requestEditFolder(
            folderId,
            name,
            privacy,
            subheading,
            isFileChange,
            requestThumbnailImage
        )
    }
}