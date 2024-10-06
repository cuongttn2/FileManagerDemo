package com.qsong.filemanagerdemo.presentation.viewmodel.file_manger

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.qsong.filemanagerdemo.di.ApplicationScopeIO
import com.qsong.filemanagerdemo.domain.usecase.FileUseCase
import com.qsong.filemanagerdemo.presentation.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileManagerViewModel @Inject constructor(
    private val fileUseCase: FileUseCase,
    @ApplicationScopeIO private val appScope: CoroutineScope,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(FileManagerState())
    val uiState: StateFlow<FileManagerState> = _uiState

    init {
        appScope.launch(Dispatchers.IO) {
            loadFiles()
        }
    }

    private fun loadFiles() {
        viewModelScope.launch(Dispatchers.Main) {
            val textFiles = async(Dispatchers.IO) { fileUseCase.getAllTextFiles() }
            val imageFiles = async(Dispatchers.IO) { fileUseCase.getAllImageFiles() }
            _uiState.update {
                it.copy(
                    textFiles = textFiles.await(),
                    imageFiles = imageFiles.await()
                )
            }
        }
    }

    fun createTextFile(name: String) {
        appScope.launch(Dispatchers.IO) {
            fileUseCase.createTextFile(name)
            loadFiles()
        }
    }

    fun createImageFile() {
        appScope.launch(Dispatchers.IO) {
            fileUseCase.createImageFile()
            loadFiles()
        }
    }

    fun toggleTag(fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fileUseCase.toggleTag(fileName)
            loadFiles()
        }
    }

    fun isStared(fileName: String): Boolean {
        Log.d("Metadata", "isStared-vm: ${fileUseCase.isStared(fileName)}")
        return fileUseCase.isStared(fileName)
    }
}