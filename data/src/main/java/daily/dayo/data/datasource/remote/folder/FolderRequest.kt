package daily.dayo.data.datasource.remote.folder

import com.google.gson.annotations.SerializedName
import daily.dayo.domain.model.Privacy

data class CreateFolderInPostRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("subheading")
    val subheading: String,
    @SerializedName("privacy")
    val privacy: Privacy
)

data class FolderMoveRequest(
    @SerializedName("postIdList")
    val postIdList: List<Long>,
    @SerializedName("targetFolderId")
    val targetFolderId: Long
)