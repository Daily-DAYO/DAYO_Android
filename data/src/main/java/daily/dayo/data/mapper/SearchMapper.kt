package daily.dayo.data.mapper

import daily.dayo.data.datasource.remote.search.SearchDto
import daily.dayo.data.datasource.remote.search.SearchUserDto
import daily.dayo.domain.model.Search
import daily.dayo.domain.model.SearchUser

fun SearchDto.toSearch() : Search =
    Search(
        postId = postId,
        thumbnailImage = thumbnailImage
    )
fun SearchUserDto.toSearchUser() : SearchUser =
    SearchUser(
        memberId = memberId,
        profileImg = profileImg,
        nickname = nickname,
        isFollow = isFollow
    )