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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qsong.filemanagerdemo.presentation.ui.theme.Typography
import com.qsong.filemanagerdemo.presentation.viewmodel.file_manger.FileManagerViewModel

@Composable
fun FileManagerScreen(viewModel: FileManagerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var newTextFileName by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("File Manager", style = Typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = newTextFileName,
            onValueChange = { newTextFileName = it },
            label = { Text("Text File Name") })

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.createTextFile(newTextFileName.text) }) {
            Text("Create Text File")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.createImageFile() }) {
            Text("Capture Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Text Files:")
        LazyColumn {
            itemsIndexed(
                items = uiState.textFiles,
                key = { _, item -> item.id }) { _, txtFile ->
                val isStared = viewModel.isStared(txtFile.name)
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleTag(txtFile.name) }
                    .padding(8.dp)) {
                    Text(txtFile.name, modifier = Modifier.weight(1f))
                    Icon(
                        modifier = Modifier.clickable {
                            viewModel.toggleTag(txtFile.name)
                        },
                        imageVector = if (isStared) Icons.Default.Star else Icons.Outlined.Star,
                        contentDescription = if (isStared) "Not Star" else "Stared",
                        tint = if (isStared) Color.Red else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Image Files:")
        LazyColumn {
            itemsIndexed(items = uiState.imageFiles,
                key = { _, item -> item.id }) { _, imageFile ->
                val isStared = viewModel.isStared(imageFile.name)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(imageFile.name, modifier = Modifier.weight(1f))
                    Icon(
                        modifier = Modifier.clickable {
                            viewModel.toggleTag(imageFile.name)
                        },
                        imageVector = if (isStared) Icons.Default.Star else Icons.Outlined.Star,
                        contentDescription = if (isStared) "Not Star" else "Stared",
                        tint = if (isStared) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}
