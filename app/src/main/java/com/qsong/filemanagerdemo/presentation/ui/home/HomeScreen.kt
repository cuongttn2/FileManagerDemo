package com.qsong.filemanagerdemo.presentation.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.qsong.filemanagerdemo.presentation.ui.theme.Typography

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to File Manager",
            style = Typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This app allows you to create and manage text and image files.",
            style = Typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.navigate("file_manager") }) {
            Text(text = "Go to File Manager")
        }
    }
}