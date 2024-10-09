package com.qsong.filemanagerdemo.domain.usecase

import android.graphics.Bitmap
import com.qsong.filemanagerdemo.data.repository.FileRepository
import com.qsong.filemanagerdemo.domain.model.FileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FileUseCase @Inject constructor(
    private val fileRepository: FileRepository,
) {

    // Create a text file and insert metadata
    suspend fun createTextFile(fileName: String) = withContext(Dispatchers.IO) {
        fileRepository.createTextFile(fileName)
    }

    // Capture and save an image file
    suspend fun captureScreenAndSaveImageFile(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        fileRepository.saveImageFile(bitmap, generateFileName())
    }

    // Get all metadata (for both text and image files)
    suspend fun getAllFiles(): List<FileItem> = withContext(Dispatchers.IO) {
        val metadataList = fileRepository.getAllMetadata()
        metadataList.map { metadata ->
            FileItem(
                name = metadata.fileName,
                path = metadata.filePath,
                type = metadata.fileType,
                isStared = metadata.isStared
            )
        }
    }

    // Clean up orphaned metadata
    suspend fun cleanUpOrphanedMetadata() = withContext(Dispatchers.IO) {
        fileRepository.cleanUpOrphanedMetadata()
    }

    // Toggle the star status of a file
    suspend fun toggleStar(fileName: String) = withContext(Dispatchers.IO) {
        fileRepository.toggleStar(fileName)
    }

    // Check if a file is starred
    suspend fun isStared(fileName: String): Boolean = withContext(Dispatchers.IO) {
        fileRepository.isStared(fileName)
    }

    // Helper function to generate a unique file name for images
    private fun generateFileName(): String {
        return "image_${System.currentTimeMillis()}"
    }
}