package com.qsong.filemanagerdemo.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FileMetadata(
    val fileName: String,
    var isStared: Boolean = false,
)