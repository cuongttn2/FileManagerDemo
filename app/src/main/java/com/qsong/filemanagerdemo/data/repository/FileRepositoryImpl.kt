package com.qsong.filemanagerdemo.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.qsong.filemanagerdemo.data.dao.FileMetadataDao
import com.qsong.filemanagerdemo.data.model.FileMetadata
import com.qsong.filemanagerdemo.domain.model.FileItem
import com.qsong.filemanagerdemo.utils.AppEnvironment
import com.qsong.filemanagerdemo.utils.FileType
import com.qsong.filemanagerdemo.utils.MimeTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val context: Context,
    private val fileMetadataDao: FileMetadataDao,
) : FileRepository {

    override suspend fun createTextFile(fileName: String): FileItem = withContext(Dispatchers.IO) {
        val path =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/${fileName}${MimeTypes.Text.PLAIN.extensions[0]}"
        val file = File(path)
        file.writeText("Sample content for $fileName")

        val fileItem =
            FileItem(name = fileName, path = path, type = FileType.TEXT, isStared = false)

        // Call the suspend insertMetadata function
        insertMetadata(
            FileMetadata(
                file.path, fileName, false, FileType.TEXT, System.currentTimeMillis()
            )
        )
        fileItem
    }

    override suspend fun insertMetadata(metadata: FileMetadata) {
        fileMetadataDao.insert(metadata)
    }

    override suspend fun getAllMetadata(): List<FileMetadata> {
        return fileMetadataDao.getAllMetadata()
    }

    override suspend fun cleanUpOrphanedMetadata() = withContext(Dispatchers.IO) {
        val metadataList = fileMetadataDao.getAllMetadata()
        metadataList.forEach {
            val file = File(it.filePath)
            if (!file.exists()) {
                fileMetadataDao.deleteMetadataByPath(it.filePath)
            }
        }
    }

    override suspend fun toggleStar(fileName: String): Unit = withContext(Dispatchers.IO) {
        fileMetadataDao.getMetadataByFileName(fileName)?.let {
            val metadata = it.copy(isStared = !it.isStared)
            fileMetadataDao.update(metadata)
        }
    }

    override suspend fun isStared(fileName: String): Boolean = withContext(Dispatchers.IO) {
        val metadata = fileMetadataDao.getMetadataByFileName(fileName)
        metadata?.isStared ?: false
    }

    override suspend fun saveImageFile(bitmap: Bitmap, fileName: String): File? =
        withContext(Dispatchers.IO) {
            val directory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "${context.packageName}/${AppEnvironment.PICTURES}"
            )
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val imageFile = File(directory, "${fileName}${MimeTypes.Image.JPEG.extensions[1]}")
            try {
                val outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // Insert metadata after saving the image
                insertMetadata(
                    FileMetadata(
                        imageFile.path, fileName, false, FileType.IMAGE, System.currentTimeMillis()
                    )
                )

                imageFile
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
}
