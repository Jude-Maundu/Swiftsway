package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Admin

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.judemaundu.swiftsway2.ui.theme.Data.UserId.User
import com.judemaundu.swiftsway2.ui.theme.Data.UserId.UserViewModel


@Composable
fun UserListScreen(viewModel: UserViewModel = viewModel(), navController: NavHostController) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("User List", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))

                if (viewModel.users.isEmpty()) {
                    Text("No users found", style = MaterialTheme.typography.bodyMedium)
                } else {
                    viewModel.users.forEach { user ->
                        var showDialog by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(user.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "${user.email} • ${user.name}", // Display UserType
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Row {
                                IconButton(onClick = { showDialog = true }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = {
                                    viewModel.deleteUser(user.id)
                                    Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }

                        if (showDialog) {
                            EditUserDialog(
                                user = user,
                                onDismiss = { showDialog = false },
                                onConfirm = { updatedUser ->
                                    viewModel.updateUser(updatedUser)
                                    showDialog = false
                                    Toast.makeText(context, "User updated", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: (User) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var selectedUserType by remember { mutableStateOf(user.name) } // Use UserType enum

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit User") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("User Type:", style = MaterialTheme.typography.labelMedium)
                UserType.values().forEach { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (name== selectedUserType),
                                onClick = { selectedUserType = name }
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = (name == selectedUserType),
                            onClick = { selectedUserType = name }
                        )
                        Text(
                            text = type.name,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        User(
                            id = user.id,
                            name = name,
                            email = email,


                        )
                    )
                }
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

enum class UserType {
    DRIVER,
    PASSENGER,
    BOTH
}

@Preview
@Composable
private fun UselistScreenPreview() {
    UserListScreen(navController = rememberNavController())
}
