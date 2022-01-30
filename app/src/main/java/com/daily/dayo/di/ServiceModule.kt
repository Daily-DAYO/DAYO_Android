package com.daily.dayo.di

import com.daily.dayo.network.post.PostApiHelper
import com.daily.dayo.network.post.PostApiHelperImpl
import com.daily.dayo.network.post.PostApiService
import com.daily.dayo.network.folder.FolderApiHelper
import com.daily.dayo.network.folder.FolderApiHelperImpl
import com.daily.dayo.network.folder.FolderApiService
import com.daily.dayo.network.follow.FollowApiHelper
import com.daily.dayo.network.follow.FollowApiHelperImpl
import com.daily.dayo.network.follow.FollowApiService
import com.daily.dayo.network.home.HomeApiHelper
import com.daily.dayo.network.home.HomeApiHelperImpl
import com.daily.dayo.network.home.HomeApiService
import com.daily.dayo.network.login.LoginApiHelper
import com.daily.dayo.network.login.LoginApiHelperImpl
import com.daily.dayo.network.login.LoginApiService
import com.daily.dayo.network.profile.ProfileApiHelper
import com.daily.dayo.network.profile.ProfileApiHelperImpl
import com.daily.dayo.network.profile.ProfileApiService
import com.daily.dayo.network.write.WriteApiHelper
import com.daily.dayo.network.write.WriteApiHelperImpl
import com.daily.dayo.network.write.WriteApiService
import com.daily.dayo.repository.*
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
    fun provideLoginApiService(retrofit: Retrofit) = retrofit.create(LoginApiService::class.java)

    @Singleton
    @Provides
    fun provideLoginApiHelper(loginApiHelperImpl: LoginApiHelperImpl): LoginApiHelper = loginApiHelperImpl

    @Singleton
    @Provides
    fun provideHomeApiService(retrofit: Retrofit) = retrofit.create(HomeApiService::class.java)

    @Singleton
    @Provides
    fun provideHomeApiHelper(homeApiHelperImpl: HomeApiHelperImpl): HomeApiHelper = homeApiHelperImpl

    // Repository를 @Provides를 통해 제공하여 ViewModel에서 Constructor에 @Inject 어노테이션과 함께 작성하기만하면 해당 Repository 객체를 자유자재로 접근 가능
    @Singleton
    @Provides
    fun provideHomeRepository(homeApiHelper: HomeApiHelper, postApiHelper: PostApiHelper) : HomeRepository = HomeRepository(homeApiHelper, postApiHelper)

    @Singleton
    @Provides
    fun provideFolderApiService(retrofit: Retrofit) = retrofit.create(FolderApiService::class.java)

    @Singleton
    @Provides
    fun provideFolderApiHelper(folderApiHelperImpl: FolderApiHelperImpl): FolderApiHelper = folderApiHelperImpl

    @Singleton
    @Provides
    fun provideFolderRepository(folderApiHelper: FolderApiHelper) : FolderRepository = FolderRepository(folderApiHelper)

    @Singleton
    @Provides
    fun providePostApiService(retrofit: Retrofit) = retrofit.create(PostApiService::class.java)

    @Singleton
    @Provides
    fun providePostApiHelper(postApiHelperImpl: PostApiHelperImpl): PostApiHelper = postApiHelperImpl

    // Repository를 @Provides를 통해 제공하여 ViewModel에서 Constructor에 @Inject 어노테이션과 함께 작성하기만하면 해당 Repository 객체를 자유자재로 접근 가능
    @Singleton
    @Provides
    fun providePostRepository(postApiHelper: PostApiHelper) : PostRepository = PostRepository(postApiHelper)

    @Singleton
    @Provides
    fun provideWriteApiService(retrofit: Retrofit) = retrofit.create(WriteApiService::class.java)

    @Singleton
    @Provides
    fun provideWriteApiHelper(writeApiHelperImpl: WriteApiHelperImpl): WriteApiHelper = writeApiHelperImpl

    @Singleton
    @Provides
    fun provideWriteRepository(writeApiHelper: WriteApiHelper) : WriteRepository = WriteRepository(writeApiHelper)

    @Singleton
    @Provides
    fun provideProfileApiService(retrofit: Retrofit) = retrofit.create(ProfileApiService::class.java)

    @Singleton
    @Provides
    fun provideProfileApiHelper(profileApiHelperImpl: ProfileApiHelperImpl): ProfileApiHelper = profileApiHelperImpl

    @Singleton
    @Provides
    fun provideProfileRepository(profileApiHelper: ProfileApiHelper) : ProfileRepository = ProfileRepository(profileApiHelper)

    @Singleton
    @Provides
    fun provideFollowApiService(retrofit: Retrofit) = retrofit.create(FollowApiService::class.java)

    @Singleton
    @Provides
    fun provideFollowApiHelper(followApiHelperImpl: FollowApiHelperImpl): FollowApiHelper = followApiHelperImpl

    @Singleton
    @Provides
    fun provideFollowRepository(followApiHelper: FollowApiHelper) : FollowRepository = FollowRepository(followApiHelper)


}