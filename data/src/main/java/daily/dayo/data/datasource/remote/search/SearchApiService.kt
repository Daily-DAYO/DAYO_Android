package daily.dayo.data.datasource.remote.search

import daily.dayo.domain.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {

    @GET("/api/v2/search")
    suspend fun requestSearchTag(
        @Query("tag") tag: String,
        @Query("end") end: Int,
        @Query("order") order: String
    ): NetworkResponse<SearchResultResponse>

    @GET("/api/v1/search/member")
    suspend fun requestSearchUser(
        @Query("nickname") nickname: String,
        @Query("end") end: Int
    ): NetworkResponse<SearchUserResponse>

    @GET("/api/v1/search/comment/member")
    suspend fun requestSearchFollowUser(
        @Query("nickname") nickname: String,
        @Query("end") end: Int
    ): NetworkResponse<SearchUserResponse>
}