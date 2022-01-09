package com.daily.dayo.network.write

import com.daily.dayo.write.model.ResponseWrite
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WriteApiService {
    @Multipart
    @POST("/api/v1/posts")
    suspend fun requestUploadPost(@Part("category") category: String, @Part("contents") contents: String,
                                  @Part files: List<MultipartBody.Part>, @Part("folderId") folderId: Int,
                                  @Part("memberId") memberId: String, @Part("privacy") privacy: String,
                                  @Part("tags") tags: Array<String>,): Response<ResponseWrite>
}