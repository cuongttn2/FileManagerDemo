package com.qsong.filemanagerdemo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.qsong.filemanagerdemo.data.model.FileMetadata

@Dao
interface FileMetadataDao {

    @Insert
    suspend fun insert(fileMetadata: FileMetadata)

    @Update
    suspend fun update(fileMetadata: FileMetadata)

    @Query("SELECT * FROM file_metadata")
    suspend fun getAllMetadata(): List<FileMetadata>

    @Query("SELECT * FROM file_metadata WHERE fileName = :fileName LIMIT 1")
    suspend fun getMetadataByFileName(fileName: String): FileMetadata?

    @Query("DELETE FROM file_metadata WHERE filePath = :filePath")
    suspend fun deleteMetadataByPath(filePath: String)
}
