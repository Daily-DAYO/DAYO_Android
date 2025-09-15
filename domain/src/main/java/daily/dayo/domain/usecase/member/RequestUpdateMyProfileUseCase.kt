package daily.dayo.domain.usecase.member

import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.MemberRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class RequestUpdateMyProfileUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(
        nickname: String?,
        profileImg: File?,
        onBasicProfileImg: Boolean
    ): NetworkResponse<Void> {
        val requestProfileImage: MultipartBody.Part? = if (profileImg != null)
            MultipartBody.Part.createFormData(
                "profileImg",
                profileImg.name,
                profileImg.asRequestBody("image/*".toMediaTypeOrNull())
            )
        else null
        return memberRepository.requestUpdateMyProfile(
            nickname = nickname,
            profileImg = requestProfileImage,
            onBasicProfileImg = onBasicProfileImg
        )
    }
}