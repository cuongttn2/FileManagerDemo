package com.qsong.filemanagerdemo.domain.usecase

import com.qsong.filemanagerdemo.data.repository.FileRepository
import com.qsong.filemanagerdemo.domain.model.FileItem
import javax.inject.Inject

class FileUseCase @Inject constructor(
    private val repository: FileRepository
) {

    fun createTextFile(name: String): FileItem {
        return repository.createTextFile(name)
    }

    fun createImageFile(): FileItem {
        return repository.createImageFile()
    }

    fun getAllTextFiles(): List<FileItem> {
        return repository.getAllTextFiles()
    }

    fun getAllImageFiles(): List<FileItem> {
        return repository.getAllImageFiles()
    }

    fun toggleTag(fileName: String) {
        repository.toggleTag(fileName)
    }

    fun isStared(fileName: String): Boolean {
        return repository.isStared(fileName)
    }
}