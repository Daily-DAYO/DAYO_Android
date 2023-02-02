package com.daily.dayo.domain.usecase.post

import com.daily.dayo.data.datasource.remote.post.CreatePostResponse
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.PostRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class RequestUploadPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        category: Category,
        contents: String,
        files: Array<File>,
        folderId: Int,
        tags: Array<String>
    ): NetworkResponse<CreatePostResponse> {
        val uploadFiles: ArrayList<MultipartBody.Part> = ArrayList()
        for (i in files.indices) {
            val imageFile = files.get(i)
            val fileNameDivideList: List<String> = imageFile.toString().split("/")
            val requestBodyFile: RequestBody = RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                imageFile
            )
            val uploadFile = MultipartBody.Part.createFormData(
                "files",
                fileNameDivideList[fileNameDivideList.size - 1],
                requestBodyFile
            )
            uploadFiles.add(uploadFile)
        }
        return postRepository.requestUploadPost(category, contents, uploadFiles, folderId, tags)
    }
}