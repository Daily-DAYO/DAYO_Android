package daily.dayo.domain.model

data class Search(
    val postId: Long,
    val thumbnailImage: String
)

data class SearchUser(
    val isFollow: Boolean,
    val memberId: String,
    val nickname: String,
    val profileImg: String,
)

data class SearchHistory(
    val count: Int,
    val data: List<SearchHistoryDetail>
)

data class SearchHistoryDetail(
    val history: String,
    val searchId: Int,
    val searchHistoryType: SearchHistoryType
)


enum class SearchHistoryType {
    USER,
    TAG
}

enum class SearchOrder {
    NEW, OLD;

    override fun toString(): String {
        return name.lowercase()
    }
}
