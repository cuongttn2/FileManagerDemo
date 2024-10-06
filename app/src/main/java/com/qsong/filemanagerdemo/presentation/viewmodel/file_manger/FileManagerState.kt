package com.qsong.filemanagerdemo.presentation.viewmodel.file_manger

import androidx.compose.runtime.Immutable
import com.qsong.filemanagerdemo.domain.model.FileItem

@Immutable
data class FileManagerState(
    val textFiles: List<FileItem> = emptyList(),
    val imageFiles: List<FileItem> = emptyList(),
)
