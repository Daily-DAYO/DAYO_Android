package daily.dayo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.BookmarkPost
import daily.dayo.domain.repository.BookmarkRepository
import daily.dayo.domain.usecase.bookmark.RequestAllMyBookmarkPostListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val requestAllMyBookmarkPostListUseCase: RequestAllMyBookmarkPostListUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookmarkUiState())
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    init {
        requestBookmarkCount()
        requestAllMyBookmarkPostList()
    }

    private fun requestBookmarkCount() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    count = bookmarkRepository.requestBookmarkCount()
                )
            }
        }
    }

    private fun requestAllMyBookmarkPostList() {
        viewModelScope.launch {
            val bookmarkPosts = requestAllMyBookmarkPostListUseCase()
                .cachedIn(viewModelScope)

            _uiState.update {
                it.copy(bookmarks = bookmarkPosts)
            }
        }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode, selectedBookmarks = emptySet()) }
    }

    fun toggleSelection(postId: Int) {
        _uiState.update {
            val currentSelection = it.selectedBookmarks
            val newSelection = if (currentSelection.contains(postId)) {
                currentSelection - postId
            } else {
                currentSelection + postId
            }
            it.copy(selectedBookmarks = newSelection)
        }
    }
}

data class BookmarkUiState(
    val count: Int = 0,
    val bookmarks: Flow<PagingData<BookmarkPost>> = flow { emit(PagingData.empty()) },
    val isEditMode: Boolean = false,
    val selectedBookmarks: Set<Int> = emptySet()
)