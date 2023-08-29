package daily.dayo.domain.model

data class User(
    var accessToken:String?=null,
    var refreshToken:String?=null,
    var email: String?=null,
    var memberId: String?=null,
    var nickname: String?=null,
    var profileImg: String?=null
)

data class UserTokens(
    val accessToken:String,
    val refreshToken:String
)

data class UserBlocked(
    val memberId: String,
    val profileImg: String,
    val nickname:String?
)
data class UsersBlocked(
    val count: Int,
    val data: List<UserBlocked>
)