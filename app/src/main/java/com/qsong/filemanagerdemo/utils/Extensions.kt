package com.qsong.filemanagerdemo.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore

fun ContentResolver.queryFileName(fileUri: Uri): String? {
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