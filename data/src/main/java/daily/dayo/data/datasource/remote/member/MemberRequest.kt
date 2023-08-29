package daily.dayo.data.datasource.remote.member

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class MemberOAuthRequest(
    @SerializedName("accessToken")
    val accessToken: String
)

data class MemberSignInRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class DeviceTokenRequest(
    @SerializedName("deviceToken")
    val deviceToken: String?
)

data class ChangePasswordRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class CheckPasswordRequest(
    @SerializedName("password")
    val password: String
)

data class SignupEmailRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("password")
    val password: String,
    val profileImg: MultipartBody.Part
)

data class ChangeReceiveAlarmRequest(
    @SerializedName("onReceiveAlarm")
    val onReceiveAlarm: Boolean
)