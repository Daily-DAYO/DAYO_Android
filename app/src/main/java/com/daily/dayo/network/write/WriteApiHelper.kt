package com.daily.dayo.network.write

import com.daily.dayo.write.model.RequestEditWrite
import com.daily.dayo.write.model.ResponseEditWrite
import com.daily.dayo.write.model.ResponseWrite
import okhttp3.MultipartBody
import retrofit2.Response

interface WriteApiHelper {
    suspend fun requestUploadPost(category: String, contents: String, files: List<MultipartBody.Part>, folderId: Int, tags: Array<String>): Response<ResponseWrite>
    suspend fun requestEditPost(postId: Int, request: RequestEditWrite): Response<ResponseEditWrite>
}