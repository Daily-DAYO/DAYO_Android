package com.daily.dayo.network.write

import com.daily.dayo.write.model.ResponseWrite
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class WriteApiHelperImpl @Inject constructor(private val writeApiService: WriteApiService) : WriteApiHelper {
    override suspend fun requestUploadPost(category: String, contents: String, files: List<MultipartBody.Part>, folderId: Int,
        memberId: String, privacy: String, tags: Array<String>): Response<ResponseWrite> = writeApiService.requestUploadPost(category, contents, files, folderId, memberId, privacy, tags)
}