package com.qsong.filemanagerdemo.data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.qsong.filemanagerdemo.R
import com.qsong.filemanagerdemo.domain.model.FileItem
import com.qsong.filemanagerdemo.domain.model.FileMetadata
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
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain") // MIME type
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "Documents/${context.getString(R.string.app_name)}/documents"
            ) // Public directory
        }

        val resolver = context.contentResolver
        val uri: Uri? = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let { fileUri ->
            resolver.openOutputStream(fileUri)?.use { outputStream ->
                outputStream.write("Hello!!\n This is sample text content".toByteArray()) // Write content to file
            }

            val realFileName = resolver.queryFileName(fileUri) ?: "${fileName}.txt"

            // Get the absolute path if needed
            val path = fileUri.path ?: ""
            Log.d("FileMetadata", "createTextFile: $realFileName")
            val metadata = FileMetadata(fileName = realFileName)
            saveMetadata(metadata) // Ensure metadata is saved when the file is created

            return FileItem(name = fileName, path = path, type = "text")
        }

        throw IOException("Failed to create file")
    }

    private fun ContentResolver.queryFileName(fileUri: Uri): String? {
        // Re-query the actual file name from the URI after inserting
        val cursor =
            this.query(fileUri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)
        val realFileName = if (cursor != null && cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
        } else {
            null // In case you cannot get the file name, reuse the original name
        }
        cursor?.close()
        return realFileName
    }

    // Create image files using MediaStore (Scoped Storage)
    override fun createImageFile(): FileItem {
        val fileName = System.currentTimeMillis()
        val contentValues = ContentValues().apply {
            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                "image_${fileName}"
            ) // File name
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") // MIME type
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "Pictures/${context.getString(R.string.app_name)}/pictures"
            ) // Public directory
        }

        val resolver = context.contentResolver
        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { fileUri ->
            resolver.openOutputStream(fileUri)?.use { outputStream ->
                // Record image data here, for example a Bitmap image if available
            }

            val realFileName = resolver.queryFileName(fileUri) ?: "image_${fileName}.jpg"

            // Get the absolute path if needed
            val path = fileUri.path ?: ""
            val metadata = FileMetadata(fileName = realFileName)
            saveMetadata(metadata)  // Ensure metadata is saved when the file is created

            return FileItem(name = "image_${fileName}.jpg", path = path, type = "image")
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
        val selectionArgs = arrayOf("Documents/${context.getString(R.string.app_name)}/documents%")

        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
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
                fileItems.add(FileItem(name = fileName, path = filePath, type = "text"))
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
        val selectionArgs = arrayOf("Pictures/${context.getString(R.string.app_name)}/pictures%")

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
                fileItems.add(FileItem(name = fileName, path = filePath, type = "image"))
            }
        }
        return fileItems
    }

    // Save metadata file
    private fun saveMetadata(metadata: FileMetadata) {
        val metaDir = context.getExternalFilesDir("metadata")
        ensureDirectoryExists(metaDir) // Make sure the directory exists
        val metaFile = File(metaDir, "${metadata.fileName}.meta")

        val json = Json.encodeToString(FileMetadata.serializer(), metadata)
        metaFile.writeText(json)
    }

    // Read metadata file
    private fun getMetadata(fileName: String): FileMetadata? {
        val metaDir = context.getExternalFilesDir("metadata")
        ensureDirectoryExists(metaDir)
        val metaFile = File(metaDir, "$fileName.meta")

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
        val file = File(context.getExternalFilesDir("documents"), fileName)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }
}