package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID


// ViewModel

class ContentViewModel : ViewModel() {
    private val repository: ContentRepository = ContentRepositoryImpl() // Changed to concrete implementation
    private var contentJob: Job? = null

    private val _contents = mutableStateListOf<Content>()
    val contents: List<Content> get() = _contents

    init {
        loadContents()
    }

    fun loadContents() {
        contentJob?.cancel()
        contentJob = viewModelScope.launch {
            repository.getContentsFlow()
                .catch { e -> Log.e("ContentVM", "Error loading contents", e) }
                .collect { contentList ->
                    _contents.clear()
                    _contents.addAll(contentList)
                }
        }
    }

    fun addContent(content: Content) = viewModelScope.launch {
        repository.addContent(content)
    }

    fun updateContent(content: Content) = viewModelScope.launch {
        repository.updateContent(content)
    }

    fun deleteContent(contentId: String) = viewModelScope.launch {
        repository.deleteContent(contentId)
    }

    override fun onCleared() {
        super.onCleared()
        contentJob?.cancel()
    }
}

// Concrete Repository Implementation
class ContentRepositoryImpl : ContentRepository {
    // Temporary in-memory storage for demonstration
    private val tempStorage = mutableListOf<Content>()
    private val contentFlow = kotlinx.coroutines.flow.MutableStateFlow<List<Content>>(emptyList())

    override fun getContentsFlow(): Flow<List<Content>> {
        return contentFlow
    }

    override suspend fun addContent(content: Content) {
        tempStorage.add(content)
        contentFlow.value = tempStorage.toList()
    }

    override suspend fun updateContent(content: Content) {
        val index = tempStorage.indexOfFirst { it.id == content.id }
        if (index != -1) {
            tempStorage[index] = content
            contentFlow.value = tempStorage.toList()
        }
    }

    override suspend fun deleteContent(contentId: String) {
        tempStorage.removeAll { it.id == contentId }
        contentFlow.value = tempStorage.toList()
    }
}

// Repository Interface
interface ContentRepository {
    fun getContentsFlow(): Flow<List<Content>>
    suspend fun addContent(content: Content)
    suspend fun updateContent(content: Content)
    suspend fun deleteContent(contentId: String)
}

// Data Model
data class Content(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)

//    override fun onCleared() {
//        super.onCleared()
//        contentJob?.cancel()
//    }




@Composable
fun ContentManagementScreen(viewModel: ContentViewModel = viewModel(),navController: NavController) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Content Management",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add New Content")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Content List
                if (viewModel.contents.isEmpty()) {
                    Text(
                        "No content available",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    viewModel.contents.forEach { content ->
                        var showEditDialog by remember { mutableStateOf(false) }

                        ContentItem(
                            content = content,
                            onEdit = { showEditDialog = true },
                            onDelete = {
                                viewModel.deleteContent(content.id)
                                Toast.makeText(context, "Content deleted", Toast.LENGTH_SHORT).show()
                            }
                        )

                        if (showEditDialog) {
                            EditContentDialog(
                                content = content,
                                onDismiss = { showEditDialog = false },
                                onConfirm = { updatedContent ->
                                    viewModel.updateContent(updatedContent)
                                    Toast.makeText(context, "Content updated", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        Divider()
                    }
                }
            }
        }
    }

    // Add Content Dialog
    if (showAddDialog) {
        AddContentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newContent ->
                viewModel.addContent(newContent)
                Toast.makeText(context, "Content added", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun ContentItem(
    content: Content,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(content.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                content.description.take(50) + if (content.description.length > 50) "..." else "",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete")
            }
        }
    }
}

@Composable
fun AddContentDialog(
    onDismiss: () -> Unit,
    onConfirm: (Content) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Content") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Category:", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newContent = Content(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        category = category,
                        createdAt = System.currentTimeMillis()
                    )
                    onConfirm(newContent)
                },
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditContentDialog(
    content: Content,
    onDismiss: () -> Unit,
    onConfirm: (Content) -> Unit
) {
    var title by remember { mutableStateOf(content.title) }
    var description by remember { mutableStateOf(content.description) }
    var category by remember { mutableStateOf(content.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Content") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Category:", style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedContent = content.copy(
                        title = title,
                        description = description,
                        category = category
                    )
                    onConfirm(updatedContent)
                },
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
private fun contentprev() {
    ContentManagementScreen(navController = rememberNavController())


}