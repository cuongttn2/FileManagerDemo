package com.qsong.filemanagerdemo.data.repository

import android.graphics.Bitmap
import com.qsong.filemanagerdemo.data.model.FileMetadata
import com.qsong.filemanagerdemo.domain.model.FileItem
import java.io.File

interface FileRepository {
    suspend fun createTextFile(fileName: String): FileItem
    suspend fun insertMetadata(metadata: FileMetadata)
    suspend fun getAllMetadata(): List<FileMetadata>
    suspend fun cleanUpOrphanedMetadata()
    suspend fun toggleStar(fileName: String)
    suspend fun isStared(fileName: String): Boolean
    suspend fun saveImageFile(bitmap: Bitmap, fileName: String): File?
}