package com.daily.dayo.di

import com.daily.dayo.data.datasource.remote.alarm.AlarmApiService
import com.daily.dayo.data.datasource.remote.block.BlockApiService
import com.daily.dayo.data.datasource.remote.bookmark.BookmarkApiService
import com.daily.dayo.data.datasource.remote.comment.CommentApiService
import com.daily.dayo.data.datasource.remote.folder.FolderApiService
import com.daily.dayo.data.datasource.remote.follow.FollowApiService
import com.daily.dayo.data.datasource.remote.heart.HeartApiService
import com.daily.dayo.data.datasource.remote.image.ImageApiService
import com.daily.dayo.data.datasource.remote.member.MemberApiService
import com.daily.dayo.data.datasource.remote.notice.NoticeApiService
import com.daily.dayo.data.datasource.remote.post.PostApiService
import com.daily.dayo.data.datasource.remote.report.ReportApiService
import com.daily.dayo.data.datasource.remote.search.SearchApiService
import com.daily.dayo.data.repository.*
import com.daily.dayo.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun provideNoticeApiService(retrofit: Retrofit) =
        retrofit.create(NoticeApiService::class.java)

    @Singleton
    @Provides
    fun provideNoticeRepository(noticeApiService: NoticeApiService): NoticeRepository =
        NoticeRepositoryImpl(noticeApiService)

    @Singleton
    @Provides
    fun provideBlockApiService(retrofit: Retrofit) =
        retrofit.create(BlockApiService::class.java)

    @Singleton
    @Provides
    fun provideBlockRepository(blockApiService: BlockApiService): BlockRepository =
        BlockRepositoryImpl(blockApiService)

    @Singleton
    @Provides
    fun provideBookmarkApiService(retrofit: Retrofit) =
        retrofit.create(BookmarkApiService::class.java)

    @Singleton
    @Provides
    fun provideBookmarkRepository(bookmarkApiService: BookmarkApiService): BookmarkRepository =
        BookmarkRepositoryImpl(bookmarkApiService)

    @Singleton
    @Provides
    fun provideCommentApiService(retrofit: Retrofit) =
        retrofit.create(CommentApiService::class.java)

    @Singleton
    @Provides
    fun provideCommentRepository(commentApiService: CommentApiService): CommentRepository =
        CommentRepositoryImpl(commentApiService)

    @Singleton
    @Provides
    fun provideFolderApiService(retrofit: Retrofit) = retrofit.create(FolderApiService::class.java)

    @Singleton
    @Provides
    fun provideFolderRepository(folderApiService: FolderApiService): FolderRepository =
        FolderRepositoryImpl(folderApiService)

    @Singleton
    @Provides
    fun provideFollowApiService(retrofit: Retrofit) = retrofit.create(FollowApiService::class.java)

    @Singleton
    @Provides
    fun provideFollowRepository(followApiService: FollowApiService): FollowRepository =
        FollowRepositoryImpl(followApiService)

    @Singleton
    @Provides
    fun provideHeartApiService(retrofit: Retrofit) = retrofit.create(HeartApiService::class.java)

    @Singleton
    @Provides
    fun provideHeartRepository(heartApiService: HeartApiService): HeartRepository =
        HeartRepositoryImpl(heartApiService)

    @Singleton
    @Provides
    fun provideMemberApiService(retrofit: Retrofit) = retrofit.create(MemberApiService::class.java)

    @Singleton
    @Provides
    fun provideMemberRepository(memberApiService: MemberApiService): MemberRepository =
        MemberRepositoryImpl(memberApiService)

    @Singleton
    @Provides
    fun providePostApiService(retrofit: Retrofit) = retrofit.create(PostApiService::class.java)

    @Singleton
    @Provides
    fun providePostRepository(postApiService: PostApiService): PostRepository =
        PostRepositoryImpl(postApiService)

    @Singleton
    @Provides
    fun provideSearchApiService(retrofit: Retrofit) = retrofit.create(SearchApiService::class.java)

    @Singleton
    @Provides
    fun provideSearchRepository(searchApiService: SearchApiService): SearchRepository =
        SearchRepositoryImpl(searchApiService)

    @Singleton
    @Provides
    fun provideImageApiService(retrofit: Retrofit) = retrofit.create(ImageApiService::class.java)

    @Singleton
    @Provides
    fun provideImageRepository(imageApiService: ImageApiService): ImageRepository =
        ImageRepositoryImpl(imageApiService)

    @Singleton
    @Provides
    fun provideAlarmApiService(retrofit: Retrofit) = retrofit.create(AlarmApiService::class.java)

    @Singleton
    @Provides
    fun provideAlarmRepository(alarmApiService: AlarmApiService): AlarmRepository =
        AlarmRepositoryImpl(alarmApiService)

    @Singleton
    @Provides
    fun provideReportApiService(retrofit: Retrofit) =
        retrofit.create(ReportApiService::class.java)

    @Singleton
    @Provides
    fun provideReportRepository(reportApiService: ReportApiService): ReportRepository =
        ReportRepositoryImpl(reportApiService)
}