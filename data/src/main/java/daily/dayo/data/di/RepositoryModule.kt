package daily.dayo.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import daily.dayo.data.datasource.remote.alarm.AlarmApiService
import daily.dayo.data.datasource.remote.block.BlockApiService
import daily.dayo.data.datasource.remote.bookmark.BookmarkApiService
import daily.dayo.data.datasource.remote.comment.CommentApiService
import daily.dayo.data.datasource.remote.folder.FolderApiService
import daily.dayo.data.datasource.remote.follow.FollowApiService
import daily.dayo.data.datasource.remote.heart.HeartApiService
import daily.dayo.data.datasource.remote.image.ImageApiService
import daily.dayo.data.datasource.remote.member.MemberApiService
import daily.dayo.data.datasource.remote.notice.NoticeApiService
import daily.dayo.data.datasource.remote.post.PostApiService
import daily.dayo.data.datasource.remote.report.ReportApiService
import daily.dayo.data.datasource.remote.search.SearchApiService
import daily.dayo.data.repository.AlarmRepositoryImpl
import daily.dayo.data.repository.BlockRepositoryImpl
import daily.dayo.data.repository.BookmarkRepositoryImpl
import daily.dayo.data.repository.CommentRepositoryImpl
import daily.dayo.data.repository.FolderRepositoryImpl
import daily.dayo.data.repository.FollowRepositoryImpl
import daily.dayo.data.repository.HeartRepositoryImpl
import daily.dayo.data.repository.ImageRepositoryImpl
import daily.dayo.data.repository.MemberRepositoryImpl
import daily.dayo.data.repository.NoticeRepositoryImpl
import daily.dayo.data.repository.PostRepositoryImpl
import daily.dayo.data.repository.ReportRepositoryImpl
import daily.dayo.data.repository.SearchRepositoryImpl
import daily.dayo.domain.repository.AlarmRepository
import daily.dayo.domain.repository.BlockRepository
import daily.dayo.domain.repository.BookmarkRepository
import daily.dayo.domain.repository.CommentRepository
import daily.dayo.domain.repository.FolderRepository
import daily.dayo.domain.repository.FollowRepository
import daily.dayo.domain.repository.HeartRepository
import daily.dayo.domain.repository.ImageRepository
import daily.dayo.domain.repository.MemberRepository
import daily.dayo.domain.repository.NoticeRepository
import daily.dayo.domain.repository.PostRepository
import daily.dayo.domain.repository.ReportRepository
import daily.dayo.domain.repository.SearchRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun provideNoticeRepository(noticeApiService: NoticeApiService): NoticeRepository =
        NoticeRepositoryImpl(noticeApiService)

    @Singleton
    @Provides
    fun provideBlockRepository(blockApiService: BlockApiService): BlockRepository =
        BlockRepositoryImpl(blockApiService)

    @Singleton
    @Provides
    fun provideBookmarkRepository(bookmarkApiService: BookmarkApiService): BookmarkRepository =
        BookmarkRepositoryImpl(bookmarkApiService)

    @Singleton
    @Provides
    fun provideCommentRepository(commentApiService: CommentApiService): CommentRepository =
        CommentRepositoryImpl(commentApiService)

    @Singleton
    @Provides
    fun provideFolderRepository(folderApiService: FolderApiService): FolderRepository =
        FolderRepositoryImpl(folderApiService)

    @Singleton
    @Provides
    fun provideFollowRepository(followApiService: FollowApiService): FollowRepository =
        FollowRepositoryImpl(followApiService)

    @Singleton
    @Provides
    fun provideHeartRepository(heartApiService: HeartApiService): HeartRepository =
        HeartRepositoryImpl(heartApiService)

    @Singleton
    @Provides
    fun provideMemberRepository(
        memberApiService: MemberApiService,
        @ApplicationContext context: Context
    ): MemberRepository =
        MemberRepositoryImpl(memberApiService, context)

    @Singleton
    @Provides
    fun providePostRepository(postApiService: PostApiService): PostRepository =
        PostRepositoryImpl(postApiService)

    @Singleton
    @Provides
    fun provideSearchRepository(
        searchApiService: SearchApiService,
        @ApplicationContext context: Context
    ): SearchRepository =
        SearchRepositoryImpl(searchApiService, context)

    @Singleton
    @Provides
    fun provideImageRepository(imageApiService: ImageApiService): ImageRepository =
        ImageRepositoryImpl(imageApiService)

    @Singleton
    @Provides
    fun provideAlarmRepository(alarmApiService: AlarmApiService): AlarmRepository =
        AlarmRepositoryImpl(alarmApiService)

    @Singleton
    @Provides
    fun provideReportRepository(reportApiService: ReportApiService): ReportRepository =
        ReportRepositoryImpl(reportApiService)
}