package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Admin

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DataManagementCard(viewModel: ContentViewModel = viewModel(),navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showExportDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }

    // File picker for export location
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { exportUri ->
            scope.launch {
                exportData(context, viewModel.contents, exportUri)
            }
        }
    }

    // File picker for backup location
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let { backupUri ->
            scope.launch {
                createBackup(context, viewModel.contents, backupUri)
            }
        }
    }

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Data Management", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Export Button
                Button(onClick = { showExportDialog = true }) {
                    Text("Export Data")
                }

                // Backup Button
                Button(onClick = { showBackupDialog = true }) {
                    Text("Backup System")
                }
            }
        }
    }

    // Export Confirmation Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Data") },
            text = { Text("Export all content data as JSON?") },
            confirmButton = {
                Button(onClick = {
                    showExportDialog = false
                    exportLauncher.launch("content_export_${getCurrentDateTime()}.json")
                }) {
                    Text("Export")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Backup Confirmation Dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Create Backup") },
            text = { Text("Create a complete system backup including all content?") },
            confirmButton = {
                Button(onClick = {
                    showBackupDialog = false
                    backupLauncher.launch("system_backup_${getCurrentDateTime()}.zip")
                }) {
                    Text("Backup")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Helper function to export data
private suspend fun exportData(context: Context, contents: List<Content>, uri: Uri) {
    try {
        val jsonString = contents.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ",\n"
        ) { content ->
            """
            {
                "id": "${content.id}",
                "title": "${content.title}",
                "description": "${content.description}",
                "category": "${content.category}",
                "createdAt": ${content.createdAt}
            }
            """.trimIndent()
        }

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(jsonString.toByteArray())
        }

        showToast(context, "Data exported successfully")
    } catch (e: Exception) {
        showToast(context, "Export failed: ${e.localizedMessage}")
    }
}

// Helper function to create backup (simplified version)
private suspend fun createBackup(context: Context, contents: List<Content>, uri: Uri) {
    try {
        // In a real app, this would create a proper ZIP archive
        val backupContent = """
            SYSTEM BACKUP
            Date: ${getCurrentDateTime()}
            Items: ${contents.size}
            
            ${contents.joinToString("\n\n") { content ->
            "ID: ${content.id}\nTitle: ${content.title}\nCategory: ${content.category}"
        }}
        """.trimIndent()

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(backupContent.toByteArray())
        }

        showToast(context, "Backup created successfully")
    } catch (e: Exception) {
        showToast(context, "Backup failed: ${e.localizedMessage}")
    }
}

// Helper function to show toast
private fun showToast(context: Context, message: String) {
    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
}

// Helper function to get current date/time
private fun getCurrentDateTime(): String {
    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    return sdf.format(Date())
}

@Preview
@Composable
private fun DataManagementCardPreview() {
    DataManagementCard(navController = rememberNavController())


}