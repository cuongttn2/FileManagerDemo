package com.qsong.filemanagerdemo.data.repository

import com.qsong.filemanagerdemo.domain.model.FileItem

interface FileRepository {
    fun createTextFile(fileName: String): FileItem
    fun createImageFile(): FileItem
    fun getAllTextFiles(): List<FileItem>
    fun getAllImageFiles(): List<FileItem>
    fun toggleTag(fileName: String)
    fun isStared(fileName: String): Boolean
}