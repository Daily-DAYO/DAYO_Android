package daily.dayo.data.di

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
    fun provideBlockApiService(retrofit: Retrofit) =
        retrofit.create(BlockApiService::class.java)

    @Singleton
    @Provides
    fun provideBookmarkApiService(retrofit: Retrofit) =
        retrofit.create(BookmarkApiService::class.java)


    @Singleton
    @Provides
    fun provideCommentApiService(retrofit: Retrofit) =
        retrofit.create(CommentApiService::class.java)


    @Singleton
    @Provides
    fun provideFolderApiService(retrofit: Retrofit) =
        retrofit.create(FolderApiService::class.java)

    @Singleton
    @Provides
    fun provideFollowApiService(retrofit: Retrofit) =
        retrofit.create(FollowApiService::class.java)

    @Singleton
    @Provides
    fun provideHeartApiService(retrofit: Retrofit) =
        retrofit.create(HeartApiService::class.java)

    @Singleton
    @Provides
    fun provideMemberApiService(retrofit: Retrofit) =
        retrofit.create(MemberApiService::class.java)

    @Singleton
    @Provides
    fun providePostApiService(retrofit: Retrofit) =
        retrofit.create(PostApiService::class.java)

    @Singleton
    @Provides
    fun provideSearchApiService(retrofit: Retrofit) =
        retrofit.create(SearchApiService::class.java)

    @Singleton
    @Provides
    fun provideImageApiService(retrofit: Retrofit) =
        retrofit.create(ImageApiService::class.java)

    @Singleton
    @Provides
    fun provideAlarmApiService(retrofit: Retrofit) =
        retrofit.create(AlarmApiService::class.java)

    @Singleton
    @Provides
    fun provideReportApiService(retrofit: Retrofit) =
        retrofit.create(ReportApiService::class.java)
}