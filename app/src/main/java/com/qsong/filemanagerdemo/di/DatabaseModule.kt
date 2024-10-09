package com.qsong.filemanagerdemo.di

import android.content.Context
import androidx.room.Room
import com.qsong.filemanagerdemo.data.database.FileManagerDatabase
import com.qsong.filemanagerdemo.data.dao.FileMetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): FileManagerDatabase {
        return Room.databaseBuilder(
            appContext,
            FileManagerDatabase::class.java,
            "qsong_file_manager_db"
        ).build()
    }

    @Provides
    fun provideFileMetadataDao(database: FileManagerDatabase): FileMetadataDao {
        return database.fileMetadataDao()
    }
}