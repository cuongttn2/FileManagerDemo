package com.qsong.filemanagerdemo.presentation.viewmodel.file_manger

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qsong.filemanagerdemo.domain.usecase.FileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileManagerViewModel @Inject constructor(
    private val fileUseCase: FileUseCase,
) : ViewModel() {

    private val _fileManagerState = MutableStateFlow(FileManagerState())
    val fileManagerState: StateFlow<FileManagerState> = _fileManagerState

    fun loadFiles() {
        viewModelScope.launch {
            val files = fileUseCase.getAllFiles()
            _fileManagerState.value = FileManagerState(
                textFiles = files.filter { it.type == "text" },
                imageFiles = files.filter { it.type == "image" }
            )
        }
    }

    fun createTextFile(fileName: String) {
        viewModelScope.launch {
            fileUseCase.createTextFile(fileName)
            loadFiles() // Reload files after creating a new one
        }
    }

    fun captureScreenAndSaveImageFile(bitmap: Bitmap) {
        viewModelScope.launch {
            fileUseCase.captureScreenAndSaveImageFile(bitmap)
            loadFiles() // Refresh the file list after saving the image
        }
    }

    fun toggleStar(fileName: String) {
        viewModelScope.launch {
            fileUseCase.toggleStar(fileName)
            loadFiles() // Reload files after toggling the star status
        }
    }

    suspend fun isStared(fileName: String): Boolean {
        val result = viewModelScope.async {
            fileUseCase.isStared(fileName)
        }
        return result.await()
    }

    fun cleanUpOrphanedMetadata() {
        viewModelScope.launch {
            fileUseCase.cleanUpOrphanedMetadata()
            loadFiles()
        }
    }
}
