package com.qsong.filemanagerdemo.presentation.viewmodel.file_manger

import androidx.compose.runtime.Immutable
import com.qsong.filemanagerdemo.domain.model.FileItem
import java.util.UUID

@Immutable
data class FileManagerState(
    val id: String = UUID.randomUUID().toString(),
    val textFiles: List<FileItem> = emptyList(),
    val imageFiles: List<FileItem> = emptyList(),
)
