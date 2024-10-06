package com.qsong.filemanagerdemo.domain.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Immutable
@Parcelize
data class FileItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val path: String,
    val type: String,
    val isStared: Boolean,
) : Parcelable