package com.daily.dayo.network.write

import com.daily.dayo.write.model.RequestEditWrite
import com.daily.dayo.write.model.ResponseEditWrite
import com.daily.dayo.write.model.ResponseWrite
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface WriteApiService {
    @Multipart
    @POST("/api/v1/posts")
    suspend fun requestUploadPost(@Part("category") category: String, @Part("contents") contents: String,
                                  @Part files: List<MultipartBody.Part>, @Part("folderId") folderId: Int,
                                  @Part("tags") tags: Array<String>): Response<ResponseWrite>
    @POST("/api/v1/posts/{postId}/edit")
    suspend fun requestEditPost(@Path("postId") postId:Int, @Body body : RequestEditWrite) : Response<ResponseEditWrite>
}