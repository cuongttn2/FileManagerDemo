package com.qsong.filemanagerdemo.di

import android.content.Context
import com.qsong.filemanagerdemo.data.dao.FileMetadataDao
import com.qsong.filemanagerdemo.data.repository.FileRepository
import com.qsong.filemanagerdemo.data.repository.FileRepositoryImpl
import com.qsong.filemanagerdemo.domain.usecase.FileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFileRepository(
        @ApplicationContext context: Context,
        fileMetadataDao: FileMetadataDao,
    ): FileRepository {
        return FileRepositoryImpl(context, fileMetadataDao)
    }

    @Provides
    @Singleton
    fun provideFileUseCase(repository: FileRepository): FileUseCase {
        return FileUseCase(repository)
    }

}