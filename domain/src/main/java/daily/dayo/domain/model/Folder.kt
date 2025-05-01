package daily.dayo.domain.model

enum class FolderOrder {
    NEW, OLD;

    override fun toString(): String {
        return name.lowercase()
    }
}

data class Folder(
    val folderId: Long?,
    val title: String,
    val memberId: String?,
    val privacy: Privacy,
    val subheading: String?,
    val thumbnailImage: String,
    val postCount: Int
)

data class Folders(
    val count: Int,
    val data: List<Folder>
)

data class FoldersMine(
    val count: Int,
    val data: List<Folder>
)

data class FolderPost(
    val createDate: String,
    val postId: Long,
    val thumbnailImage: String
)

data class FolderOrder(
    var folderId: Long,
    var orderIndex: Int
)

data class FolderCreateResponse(
    val folderId: Long
)

data class FolderCreateInPostResponse(
    val folderId: Long
)

data class FolderEditResponse(
    val folderId: Long
)

data class FolderInfo(
    val memberId: String,
    val name: String,
    val postCount: Int,
    val privacy: Privacy,
    var subheading: String,
    val thumbnailImage: String
)