package com.judemaundu.swiftsway2.ui.theme.Data.UserId

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await



class UserRepository {
    private val usersCollection = Firebase.firestore.collection("users")

    fun getUsersLiveData() = callbackFlow<List<User>> {
        val listener = usersCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserRepository", "Error fetching users", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val users = snapshot?.toObjects(User::class.java) ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }.catch {
        Log.e("UserRepository", "Flow error", it)
        emit(emptyList())
    }

    suspend fun addUser(user: User) {
        try {
            val ref = usersCollection.add(user).await()
            Log.d("UserRepository", "User added: ${ref.id}")
        } catch (e: Exception) {
            Log.e("UserRepository", "Add error", e)
            throw e
        }
    }

    suspend fun updateUser(user: User) {
        try {
            require(user.id.isNotEmpty()) { "User ID is required to update" }
            usersCollection.document(user.id).set(user).await()
            Log.d("UserRepository", "User updated: ${user.id}")
        } catch (e: Exception) {
            Log.e("UserRepository", "Update error", e)
            throw e
        }
    }

    suspend fun deleteUser(userId: String) {
        try {
            usersCollection.document(userId).delete().await()
            Log.d("UserRepository", "User deleted: $userId")
        } catch (e: Exception) {
            Log.e("UserRepository", "Delete error", e)
            throw e
        }
    }
}

// --- ViewModel: Business logic and state management ---
class UserViewModel : ViewModel() {
    private val repository = UserRepository()
    private var userJob: Job? = null

    private val _users = mutableStateListOf<User>()
    val users: List<User> get() = _users

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> get() = _errorMessage

    init {
        loadUsers()
    }

    fun loadUsers() {
        _isLoading.value = true
        _errorMessage.value = null
        userJob?.cancel()
        userJob = viewModelScope.launch {
            repository.getUsersLiveData()
                .catch {
                    Log.e("UserViewModel", "Error loading users", it)
                    _errorMessage.value = "Failed to load users: ${it.message}"
                    _isLoading.value = false
                }
                .collect { fetchedUsers ->
                    _users.clear()
                    _users.addAll(fetchedUsers)
                    _isLoading.value = false
                }
        }
    }

    fun addUser(user: User) = viewModelScope.launch {
        try {
            _isLoading.value = true
            _errorMessage.value = null
            repository.addUser(user)
            loadUsers()
        } catch (e: Exception) {
            _errorMessage.value = "Add failed: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun updateUser(user: User) = viewModelScope.launch {
        try {
            _isLoading.value = true
            _errorMessage.value = null
            repository.updateUser(user)
            loadUsers()
        } catch (e: Exception) {
            _errorMessage.value = "Update failed: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun deleteUser(userId: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            _errorMessage.value = null
            repository.deleteUser(userId)
            loadUsers()
        } catch (e: Exception) {
            _errorMessage.value = "Delete failed: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        userJob?.cancel()
        super.onCleared()
    }
}

// --- UI: Glowing Dark User Card with Dark Theme Wrapper ---
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun SwiftSwayDarkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}

@Composable
fun GlowingUserCard(user: User) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${user.name}", color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Email: ${user.email}", color = Color.LightGray)
        }
    }
}

@Composable
fun UserListScreen(viewModel: UserViewModel = UserViewModel()) {
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val users = viewModel.users

    SwiftSwayDarkTheme {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            }

            errorMessage?.let {
                Text(text = it, color = Color.Red)
            }

            users.forEach { user ->
                GlowingUserCard(user)
            }
        }
    }
}
