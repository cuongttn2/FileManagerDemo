package com.qsong.filemanagerdemo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qsong.filemanagerdemo.data.dao.FileMetadataDao
import com.qsong.filemanagerdemo.data.model.FileMetadata

@Database(entities = [FileMetadata::class], version = 1, exportSchema = true)
abstract class FileManagerDatabase : RoomDatabase() {
    abstract fun fileMetadataDao(): FileMetadataDao
}