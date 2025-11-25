package com.example.assignmentexample.screen.SignupScreen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.assignmentexample.data.UserViewModel

@Composable
fun CreateUsernameScreen(
    userViewModel: UserViewModel,
    email: String,
    onCreationSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Create Your Username",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Create a username and password to log in easily next time.")
        Spacer(modifier = Modifier.height(24.dp))

        //username Field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))


        //display error message
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        //submit Button
        Button(
            onClick = {
                if (username.isBlank()) {
                    errorMessage = "Username cannot be empty."
                } else {
                    userViewModel.createUsernameProfile(
                        username = username,
                        onSuccess = {
                            errorMessage = null
                            onCreationSuccess() // Navigate to login or home
                        },
                        onError = { msg ->
                            errorMessage = msg
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Complete Setup")
        }
    }
}