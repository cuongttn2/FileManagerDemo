package com.qsong.filemanagerdemo.domain.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class FileItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val path: String,
    val type: String,
)