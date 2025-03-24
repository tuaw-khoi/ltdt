package com.example.doancoso.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.doancoso.R
import com.example.doancoso.data.repository.AuthService
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun SignupScreen(navController: NavHostController, authService: AuthService) {
//    val viewModel: AuthViewModel = viewModel()
//    val authState by viewModel.authState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Biểu thức chính quy để kiểm tra cú pháp email
    val emailPattern = Pattern.compile(
        "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    )

    // LaunchedEffect để xử lý điều hướng một lần
//    LaunchedEffect(authState) {
//        when (authState) {
//            is AuthState.Success -> {
//                Toast.makeText(navController.context, "Registration successful!", Toast.LENGTH_SHORT).show()
//                navController.navigate("login") {
//                    popUpTo("signup") { inclusive = true } // Xóa màn hình signup khỏi back stack
//                }
//                viewModel.resetAuthState() // Reset trạng thái sau khi điều hướng
//            }
//            else -> {}
//        }
//    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        //    sử dụng box để chồng các layer ( nen và form )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E2A44))
                .padding(paddingValues)
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Thêm hình ảnh biểu đồ tài chính
            Image(
                painter = painterResource(id = R.drawable.financial_chart),
                contentDescription = "Financial Chart",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.TopCenter) // Đặt hình ảnh ở trên cùng
            )
            //    form ky nằm giữa
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
//        when (authState) {
//            is AuthState.Loading -> CircularProgressIndicator()
//            is AuthState.Error -> {
//                val error = (authState as AuthState.Error).message
//                Text(text = error, color = MaterialTheme.colorScheme.error)
//            }
//            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Hiển thị thông báo lỗi nếu có
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Trường nhập tên
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
                        isError = errorMessage != null && name.isBlank()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Trường nhập email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null // Xóa lỗi khi người dùng bắt đầu nhập lại
                        },
                        label = { Text("Email") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        isError = errorMessage != null
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Trường nhập mật khẩu
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null // Xóa lỗi khi người dùng bắt đầu nhập lại
                        },
                        label = { Text("Password") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Icon"
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        isError = errorMessage != null && password.isBlank()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                // Kiểm tra các trường
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
                                    Toast.makeText(navController.context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                } else {
                                    errorMessage = "Registration failed. Please try again."
                                    snackbarHostState.showSnackbar("Registration failed. Please try again.")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Sign Up", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { navController.navigate("login") }) {
                        Text("Already have an account? Login", color = Color.Gray)
                    }
                }
            }
        }
    }
}
