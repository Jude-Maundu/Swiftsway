import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Define the image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            // Handle the selected image URI
            uri?.let {
                // Upload the image to Firebase or use the URI
                println("Picked image URI: $uri")
                // You can upload the selected image to Firebase Storage here
            }
        }
    )

    // Load profile image
    LaunchedEffect(uid) {
        uid?.let {
            try {
                val imageRef = FirebaseStorage.getInstance().reference
                    .child("users/$uid/profile.jpg")
                profileImageUrl = imageRef.downloadUrl.await().toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Load name and email
    LaunchedEffect(uid) {
        uid?.let {
            val dbRef = FirebaseDatabase.getInstance().getReference("users/$uid")
            dbRef.get().addOnSuccessListener { snapshot ->
                name = snapshot.child("name").value?.toString() ?: ""
                email = snapshot.child("email").value?.toString() ?: ""
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Profile") })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (profileImageUrl != null) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Driver's Profile Information", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Name: $name")
            Text("Email: $email")

            Spacer(modifier = Modifier.height(24.dp))

            // 📸 Update Button
            Button(onClick = {
                // Launch the image picker when the button is clicked
                imagePickerLauncher.launch("image/*")
            }) {
                Text("Change Profile Picture")
            }
        }
    }
}

@Preview
@Composable
private fun ProfilesScreenPreview() {
    ProfilesScreen(rememberNavController())
}
