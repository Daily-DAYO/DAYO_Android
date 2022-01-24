package com.daily.dayo.repository

import com.daily.dayo.network.folder.FolderApiHelper
import com.daily.dayo.profile.model.RequestCreateFolderInPost
import com.daily.dayo.profile.model.ResponseFolderId
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class FolderRepository @Inject constructor(private val folderApiHelper: FolderApiHelper){
    suspend fun requestCreateFolder(name:String, privacy:String, subheading:String?, thumbnailImage: File?) : Response<ResponseFolderId> {
        val requestThumbnailImage : MultipartBody.Part = if(thumbnailImage!=null) {
            MultipartBody.Part.createFormData("thumbnailImage",thumbnailImage.name, RequestBody.create("image/*".toMediaTypeOrNull(),thumbnailImage) )
        } else{
            MultipartBody.Part.createFormData("thumbnailImage","", RequestBody.create("image/*".toMediaTypeOrNull(), ""))
        }

        return folderApiHelper.requestCreateFolder(name,privacy,subheading,requestThumbnailImage)
    }

    suspend fun requestAllMyFolderList() = folderApiHelper.requestAllMyFolderList()

    suspend fun requestAllFolderList(memberId: String) = folderApiHelper.requestAllFolderList(memberId)

    suspend fun requestCreateFolderInPost(request: RequestCreateFolderInPost) = folderApiHelper.requestCreateFolderInPost(request)

    suspend fun requestDetailListFolder(folderId:Int) = folderApiHelper.requestDetailListFolder(folderId)

    suspend fun requestDeleteFolder(folderId: Int) = folderApiHelper.requestDeleteFolder(folderId)

    suspend fun requestEditFolder(folderId:Int, name:String, privacy:String, subheading:String?, thumbnailImage: File?) : Response<ResponseFolderId>{
        return if(thumbnailImage!=null) {
            val requestThumbnailImage : MultipartBody.Part =
                MultipartBody.Part.createFormData("thumbnailImage",thumbnailImage.name, RequestBody.create("image/*".toMediaTypeOrNull(),thumbnailImage) )
            folderApiHelper.requestEditFolder(folderId,name,privacy,subheading,requestThumbnailImage)
        } else{
            folderApiHelper.requestEditFolder(folderId,name,privacy,subheading,null)
        }
    }
}
