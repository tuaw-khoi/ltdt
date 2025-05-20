package com.example.doancoso.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.doancoso.R
import com.example.doancoso.data.repository.AuthService
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavHostController, authService: AuthService) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    val greenishBlue = Color(0xFF64C5B1)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.expense),
                contentDescription = "Financial Chart",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color(0xFFADE1B9))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Person Icon"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        isError = name.isBlank() && errorMessage != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenishBlue,
                            unfocusedBorderColor = greenishBlue,
                            cursorColor = greenishBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Email") },
                        placeholder = { Text("v@gmail.com") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        isError = !emailPattern.matcher(email).matches() && email.isNotBlank(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenishBlue,
                            unfocusedBorderColor = greenishBlue,
                            cursorColor = greenishBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Icon"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        isError = password.length < 6 && password.isNotBlank(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = greenishBlue,
                            unfocusedBorderColor = greenishBlue,
                            cursorColor = greenishBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f).padding(end = 8.dp), color = Color.Gray)
                        Text("Continue with", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        Divider(modifier = Modifier.weight(1f).padding(start = 8.dp), color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .border(1.dp, Color.Gray, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "Google Sign In",
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .border(1.dp, Color.Gray, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.fb),
                                contentDescription = "Facebook Sign In",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Gradient button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF6BC1C0), Color(0xFFA8E063))
                                )
                            )
                            .clickable(enabled = !isLoading) {
                                coroutineScope.launch {
                                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                                        errorMessage = "Please fill in all fields"
                                        return@launch
                                    }
                                    if (!emailPattern.matcher(email).matches()) {
                                        errorMessage = "Please enter a valid email address"
                                        return@launch
                                    }
                                    if (password.length < 6) {
                                        errorMessage = "Password must be at least 6 characters"
                                        return@launch
                                    }

                                    isLoading = true
                                    val success = authService.register(name, email, password)
                                    isLoading = false
                                    if (success) {
                                        navController.navigate("login") {
                                            popUpTo("signup") { inclusive = true }
                                        }
                                        Toast.makeText(
                                            navController.context,
                                            "Registration successful!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        errorMessage = "Registration failed. Please try again."
                                        snackbarHostState.showSnackbar("Registration failed. Please try again.")
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("ĐĂNG KÝ", color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { navController.navigate("login") }) {
                        Text("Bạn đã có tài khoản ? Đăng nhập", color = Color.Gray)
                    }
                }
            }
        }
    }
}
