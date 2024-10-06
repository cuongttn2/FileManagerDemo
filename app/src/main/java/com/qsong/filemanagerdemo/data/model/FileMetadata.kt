package com.qsong.filemanagerdemo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_metadata")
data class FileMetadata(
    @PrimaryKey val filePath: String,
    @ColumnInfo("name")
    val fileName: String,
    @ColumnInfo("stared")
    val isStared: Boolean,
    @ColumnInfo("type")
    val fileType: String,
    @ColumnInfo("created_at")
    val createdTime: Long,
)