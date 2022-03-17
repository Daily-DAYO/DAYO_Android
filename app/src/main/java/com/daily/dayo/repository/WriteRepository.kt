package com.daily.dayo.repository

import com.daily.dayo.network.write.WriteApiHelper
import com.daily.dayo.write.model.RequestEditWrite
import com.daily.dayo.write.model.ResponseEditWrite
import com.daily.dayo.write.model.ResponseWrite
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class WriteRepository @Inject constructor(private val writeApiHelper: WriteApiHelper) {
    suspend fun requestUploadPost(postCategory: String, postContents: String, files: Array<File>, postFolderId: Int, postTags: Array<String>): Response<ResponseWrite> {
        val uploadFiles: ArrayList<MultipartBody.Part> = ArrayList()
        for(i in 0 until files.size) {
            val imageFile = files.get(i)
            val fileNameDivideList: List<String> = imageFile.toString().split("/")
            var requestBodyFile : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)
            val uploadFile = MultipartBody.Part.createFormData("files",fileNameDivideList[fileNameDivideList.size - 1], requestBodyFile)
            uploadFiles.add(uploadFile)
        }
        return writeApiHelper.requestUploadPost(postCategory, postContents, uploadFiles, postFolderId, postTags)
    }
    suspend fun requestEditPost(postId: Int, request: RequestEditWrite) : Response<ResponseEditWrite> = writeApiHelper.requestEditPost(postId, request)
}