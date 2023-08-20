package com.daily.dayo.domain.usecase.member

import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.MemberRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class RequestSignUpEmailUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(
        email: String,
        nickname: String,
        password: String,
        profileImg: File?
    ): NetworkResponse<String> {
        val uploadFile: MultipartBody.Part?
        val fileNameDivideList: List<String> = profileImg.toString().split("/")
        val requestBodyFile: RequestBody? = if (profileImg != null) {
            RequestBody.create("image/*".toMediaTypeOrNull(), profileImg!!)
        } else null
        uploadFile = if (requestBodyFile!= null) {
            MultipartBody.Part.createFormData(
                "profileImg",
                fileNameDivideList[fileNameDivideList.size - 1],
                requestBodyFile
            )
        } else null
        return memberRepository.requestSignupEmail(
            email = email,
            nickname = nickname,
            password = password,
            profileImg = uploadFile
        )
    }
}