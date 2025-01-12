package daily.dayo.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.Folder
import daily.dayo.domain.model.FolderInfo
import daily.dayo.domain.model.FolderPost
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Privacy
import daily.dayo.domain.usecase.folder.RequestAllMyFolderListUseCase
import daily.dayo.domain.usecase.folder.RequestCreateFolderUseCase
import daily.dayo.domain.usecase.folder.RequestDeleteFolderUseCase
import daily.dayo.domain.usecase.folder.RequestEditFolderUseCase
import daily.dayo.domain.usecase.folder.RequestFolderInfoUseCase
import daily.dayo.domain.usecase.folder.RequestFolderPostListUseCase
import daily.dayo.domain.usecase.folder.RequestOrderFolderUseCase
import daily.dayo.domain.usecase.post.RequestDeletePostUseCase
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val requestCreateFolderUseCase: RequestCreateFolderUseCase,
    private val requestEditFolderUseCase: RequestEditFolderUseCase,
    private val requestDeleteFolderUseCase: RequestDeleteFolderUseCase,
    private val requestOrderFolderUseCase: RequestOrderFolderUseCase,
    private val requestAllMyFolderListUseCase: RequestAllMyFolderListUseCase,
    private val requestFolderInfoUseCase: RequestFolderInfoUseCase,
    private val requestFolderPostListUseCase: RequestFolderPostListUseCase,
    private val requestDeletePostUseCase: RequestDeletePostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FolderUiState())
    val uiState: StateFlow<FolderUiState> = _uiState.asStateFlow()

    private val _createSuccess = MutableSharedFlow<Boolean>()
    val createSuccess = _createSuccess.asSharedFlow()

    private val _editSuccess = MutableSharedFlow<Boolean>()
    val editSuccess = _editSuccess.asSharedFlow()

    private val _folderDeleteSuccess = MutableSharedFlow<Boolean>()
    val folderDeleteSuccess = _folderDeleteSuccess.asSharedFlow()

    private val _postDeleteSuccess = MutableSharedFlow<Boolean>()
    val postDeleteSuccess = _postDeleteSuccess.asSharedFlow()

    private val _postMoveSuccess = MutableSharedFlow<Boolean>()
    val postMoveSuccess = _postMoveSuccess.asSharedFlow()

    private val _folderList = MutableLiveData<Resource<List<Folder>>>()
    val folderList: LiveData<Resource<List<Folder>>> get() = _folderList

    private val _orderFolderSuccess = MutableLiveData<Event<Boolean>>()
    val orderFolderSuccess: LiveData<Event<Boolean>> get() = _orderFolderSuccess

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode, selectedPosts = emptySet()) }
    }

    fun toggleSelection(postId: Int) {
        _uiState.update {
            val currentSelection = it.selectedPosts
            val newSelection = if (currentSelection.contains(postId)) {
                currentSelection - postId
            } else {
                currentSelection + postId
            }
            it.copy(selectedPosts = newSelection)
        }
    }

    fun deletePosts() {
        viewModelScope.launch {
            try {
                val deleteJobs = uiState.value.selectedPosts.map { postId ->
                    async {
                        requestDeletePostUseCase(postId).let { response ->
                            when (response) {
                                is NetworkResponse.Success -> true
                                is NetworkResponse.ApiError -> throw CancellationException("API Error: PostId ${postId}, ${response.error}")
                                is NetworkResponse.NetworkError -> throw CancellationException("Network Error: PostId ${postId}, ${response.exception.message}")
                                is NetworkResponse.UnknownError -> throw CancellationException("Unknown Error: PostId ${postId}, ${response.throwable?.message}")
                            }
                        }
                    }
                }
                deleteJobs.awaitAll()
                _postDeleteSuccess.emit(true)
            } catch (e: CancellationException) {
                Log.e("Delete Post", "${e.message}")
                _postDeleteSuccess.emit(false)
            }
        }
    }

    fun requestCreateFolder(name: String, subheading: String, privacy: Privacy) {
        viewModelScope.launch {
            requestCreateFolderUseCase(
                name = name,
                subheading = subheading,
                privacy = privacy,
                thumbnailImg = null
            ).let { response ->
                when (response) {
                    is NetworkResponse.Success -> _createSuccess.emit(true)
                    else -> _createSuccess.emit(false)
                }
            }
        }
    }

    fun requestEditFolder(folderId: Int, name: String, subheading: String, privacy: Privacy) {
        viewModelScope.launch {
            requestEditFolderUseCase(
                folderId = folderId,
                name = name,
                subheading = subheading,
                privacy = privacy,
                isFileChange = false,
                thumbnailImg = null
            ).let { response ->
                when (response) {
                    is NetworkResponse.Success -> _editSuccess.emit(true)
                    else -> _editSuccess.emit(false)
                }
            }
        }
    }

    fun requestDeleteFolder(folderId: Int) {
        viewModelScope.launch {
            requestDeleteFolderUseCase(folderId = folderId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> _folderDeleteSuccess.emit(true)
                    else -> _folderDeleteSuccess.emit(false)
                }
            }
        }
    }

    fun requestAllMyFolderList() = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        requestAllMyFolderListUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _folderList.postValue(Resource.success(ApiResponse.body?.data))
                }

                is NetworkResponse.NetworkError -> {
                    _folderList.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }

                is NetworkResponse.ApiError -> {
                    _folderList.postValue(Resource.error(ApiResponse.error.toString(), null))
                }

                is NetworkResponse.UnknownError -> {
                    _folderList.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    fun requestFolderInfo(folderId: Int) {
        viewModelScope.launch {
            val result = when (val response = requestFolderInfoUseCase(folderId)) {
                is NetworkResponse.Success -> response.body ?: DEFAULT_FOLDER_INFO
                else -> DEFAULT_FOLDER_INFO
            }

            _uiState.update {
                it.copy(folderInfo = result)
            }
        }
    }

    fun requestFolderPostList(folderId: Int) {
        viewModelScope.launch {
            val folderPosts = requestFolderPostListUseCase(folderId)
                .cachedIn(viewModelScope)

            _uiState.update {
                it.copy(folderPosts = folderPosts)
            }
        }
    }

    fun moveSelectedPost() {
        viewModelScope.launch {
            // TODO API 필요
            _postMoveSuccess.emit(true)
        }
    }
}

data class FolderUiState(
    val folderInfo: FolderInfo = DEFAULT_FOLDER_INFO,
    val folderPosts: Flow<PagingData<FolderPost>> = flow { emit(PagingData.empty()) },
    val isEditMode: Boolean = false,
    val selectedPosts: Set<Int> = emptySet()
)

val DEFAULT_FOLDER_INFO = FolderInfo(
    memberId = "",
    name = "",
    postCount = 0,
    privacy = Privacy.ALL,
    subheading = "",
    thumbnailImage = ""
)

const val FOLDER_NAME_MAX_LENGTH = 12
const val FOLDER_DESCRIPTION_MAX_LENGTH = 20
