package com.qsong.filemanagerdemo.presentation.ui.file_manager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qsong.filemanagerdemo.domain.model.FileItem
import com.qsong.filemanagerdemo.presentation.ui.theme.Typography
import com.qsong.filemanagerdemo.presentation.viewmodel.file_manger.FileManagerViewModel
import com.qsong.filemanagerdemo.utils.Utils.createPlaceholderBitmap

@Composable
fun FileManagerScreen(viewModel: FileManagerViewModel = hiltViewModel()) {
    val fileManagerState by viewModel.fileManagerState.collectAsState()
    var textFileName by remember { mutableStateOf("") }
    var showImageCaptureDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cleanUpOrphanedMetadata()
        viewModel.loadFiles()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("File Manager", style = Typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Text input and button to create a text file
        OutlinedTextField(
            value = textFileName,
            onValueChange = { textFileName = it },
            label = { Text("Enter text file name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (textFileName.isNotEmpty()) {
                    viewModel.createTextFile(textFileName)
                    textFileName = "" // Clear input
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Text File")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to capture and save an image
        Button(
            onClick = { showImageCaptureDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Capture Image File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Text and Image File Lists
        Text("Text Files:", style = Typography.bodyLarge)
        LazyColumn {
            itemsIndexed(
                items = fileManagerState.textFiles,
                key = { _, item -> item.id }) { _, textFile ->
                FileItemRow(
                    fileItem = textFile,
                    isStared = textFile.isStared,
                    toggleStar = viewModel::toggleStar
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Image Files:", style = Typography.bodyLarge)
        LazyColumn {
            itemsIndexed(
                items = fileManagerState.imageFiles,
                key = { _, item -> item.id }) { _, imageFile ->
                FileItemRow(
                    fileItem = imageFile,
                    isStared = imageFile.isStared,
                    toggleStar = viewModel::toggleStar
                )
            }
        }
    }

    // Dialog to simulate image capture and pass a bitmap
    if (showImageCaptureDialog) {
        // Simulating Bitmap creation for the example
        val bitmap = createPlaceholderBitmap()

        AlertDialog(
            onDismissRequest = { showImageCaptureDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.captureScreenAndSaveImageFile(bitmap) // Pass the bitmap
                    showImageCaptureDialog = false // Close dialog
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImageCaptureDialog = false }) {
                    Text("Dismiss")
                }
            },
            text = { Text("Image capture functionality goes here") }
        )
    }
}

@Composable
fun FileItemRow(fileItem: FileItem, isStared: Boolean, toggleStar: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { toggleStar.invoke(fileItem.name) }
            .padding(8.dp)
    ) {
        Text(
            text = fileItem.name,
            modifier = Modifier.weight(1f),
            style = Typography.bodyLarge
        )
        Icon(
            imageVector = if (isStared) Icons.Filled.Star else Icons.Outlined.Star,
            contentDescription = if (isStared) "Stared" else "Not Stared",
            tint = if (isStared) Color.Yellow else Color.Gray
        )
    }
}
