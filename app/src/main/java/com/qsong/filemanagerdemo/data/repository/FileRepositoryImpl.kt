package com.qsong.filemanagerdemo.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.qsong.filemanagerdemo.R
import com.qsong.filemanagerdemo.domain.model.FileItem
import com.qsong.filemanagerdemo.domain.model.FileMetadata
import com.qsong.filemanagerdemo.utils.AppEnvironment
import com.qsong.filemanagerdemo.utils.FileType
import com.qsong.filemanagerdemo.utils.MimeTypes
import com.qsong.filemanagerdemo.utils.VolumeName
import com.qsong.filemanagerdemo.utils.queryFileName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FileRepository {

    // Make sure the directory exists
    private fun ensureDirectoryExists(directory: File?) {
        if (directory != null && !directory.exists()) {
            directory.mkdirs()
        }
    }

    // Create text files using MediaStore (Scoped Storage)
    override fun createTextFile(fileName: String): FileItem {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName) // file name
            put(MediaStore.MediaColumns.MIME_TYPE, MimeTypes.Text.PLAIN.mimeType) // MIME type
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOCUMENTS}/${context.getString(R.string.app_name)}/${AppEnvironment.DOCUMENTS}"
            ) // Public directory
        }

        val resolver = context.contentResolver
        val uri: Uri? =
            resolver.insert(MediaStore.Files.getContentUri(VolumeName.EXTERNAL), contentValues)

        uri?.let { fileUri ->
            resolver.openOutputStream(fileUri)?.use { outputStream ->
                outputStream.write("Hello!!\n This is sample text content".toByteArray()) // Write content to file
            }

            val realFileName = resolver.queryFileName(fileUri)
                ?: "${fileName}${MimeTypes.Text.PLAIN.extensions[0]}"

            // Get the absolute path if needed
            val path = fileUri.path ?: ""
            Log.d("FileMetadata", "createTextFile: $realFileName")
            val metadata = FileMetadata(fileName = realFileName)
            saveMetadata(metadata) // Ensure metadata is saved when the file is created

            return FileItem(name = fileName, path = path, type = FileType.TEXT)
        }

        throw IOException("Failed to create file")
    }

    // Create image files using MediaStore (Scoped Storage)
    override fun createImageFile(): FileItem {
        val fileName = System.currentTimeMillis()
        val contentValues = ContentValues().apply {
            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                "image_${fileName}"
            ) // File name
            put(MediaStore.MediaColumns.MIME_TYPE, MimeTypes.Image.JPEG.mimeType) // MIME type
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/${context.getString(R.string.app_name)}/${AppEnvironment.PICTURES}"
            ) // Public directory
        }

        val resolver = context.contentResolver
        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { fileUri ->
            resolver.openOutputStream(fileUri)?.use { outputStream ->
                // Record image data here, for example a Bitmap image if available
            }

            val realFileName = resolver.queryFileName(fileUri)
                ?: "image_${fileName}${MimeTypes.Image.JPEG.extensions[1]}"

            // Get the absolute path if needed
            val path = fileUri.path ?: ""
            val metadata = FileMetadata(fileName = realFileName)
            saveMetadata(metadata)  // Ensure metadata is saved when the file is created

            return FileItem(
                name = "image_${fileName}${MimeTypes.Image.JPEG.extensions[1]}",
                path = path,
                type = FileType.IMAGE
            )
        }

        throw IOException("Failed to create image file")
    }

    // Get all text files from MediaStore
    override fun getAllTextFiles(): List<FileItem> {
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA // File path
        )

        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
        val selectionArgs =
            arrayOf("${Environment.DIRECTORY_DOCUMENTS}/${context.getString(R.string.app_name)}/${AppEnvironment.DOCUMENTS}%")

        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri(VolumeName.EXTERNAL),
            projection,
            selection,
            selectionArgs,
            null
        )

        val fileItems = mutableListOf<FileItem>()
        cursor?.use {
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

            while (it.moveToNext()) {
                val fileName = it.getString(nameColumn)
                val filePath = it.getString(dataColumn)
                fileItems.add(FileItem(name = fileName, path = filePath, type = FileType.TEXT))
            }
        }
        return fileItems
    }

    //  Get all image files from MediaStore
    override fun getAllImageFiles(): List<FileItem> {
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA // File path
        )

        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
        val selectionArgs =
            arrayOf("${Environment.DIRECTORY_PICTURES}/${context.getString(R.string.app_name)}/${AppEnvironment.PICTURES}%")

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        val fileItems = mutableListOf<FileItem>()
        cursor?.use {
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

            while (it.moveToNext()) {
                val fileName = it.getString(nameColumn)
                val filePath = it.getString(dataColumn)
                fileItems.add(FileItem(name = fileName, path = filePath, type = FileType.IMAGE))
            }
        }
        return fileItems
    }

    // Save metadata file
    private fun saveMetadata(metadata: FileMetadata) {
        val metaDir = context.getExternalFilesDir(AppEnvironment.META_DATA)
        ensureDirectoryExists(metaDir) // Make sure the directory exists
        val metaFile = File(metaDir, "${metadata.fileName}${MimeTypes.MetaData.META.extensions[0]}")

        val json = Json.encodeToString(FileMetadata.serializer(), metadata)
        metaFile.writeText(json)
    }

    // Read metadata file
    private fun getMetadata(fileName: String): FileMetadata? {
        val metaDir = context.getExternalFilesDir(AppEnvironment.META_DATA)
        ensureDirectoryExists(metaDir)
        val metaFile = File(metaDir, "${fileName}${MimeTypes.MetaData.META.extensions[0]}")

        return if (metaFile.exists()) {
            val json = metaFile.readText()
            Json.decodeFromString(json)
        } else {
            null
        }
    }

    // change 'isStared' status
    override fun toggleTag(fileName: String) {
        var metadata = getMetadata(fileName)

        // If 'metadata' not exist, create new 'metadata'
        if (metadata == null) {
            metadata = FileMetadata(
                fileName = fileName,
                isStared = true
            ) // Create new 'metadata', isTagged = true
            Log.d("Metadata", "Created new metadata for: $fileName")
        } else {
            // Change 'stared' status if 'metadata' existed'
            metadata.isStared = !metadata.isStared
            Log.d("Metadata", "toggleStar: ${metadata.isStared}")
        }

        // Save 'metadata' after update
        saveMetadata(metadata)
    }

    // check 'isStared'
    override fun isStared(fileName: String): Boolean {
        val metadata = getMetadata(fileName)
        Log.d("Metadata", "metadata-impl: $metadata")
        return metadata?.isStared ?: false
    }

    // share file, using FileProvider
    fun shareFile(fileName: String): Uri {
        val file = File(context.getExternalFilesDir(AppEnvironment.DOCUMENTS), fileName)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }
}