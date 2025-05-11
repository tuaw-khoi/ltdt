package com.example.doancoso.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController,authService : AuthService ,signIn: () -> Unit) {
//    val viewModel: AuthViewModel = viewModel()
//    val authState by viewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    // LaunchedEffect để xử lý logic điều hướng một lần
//    LaunchedEffect(authState) {
//        when (authState) {
//            is AuthState.UserLoggedIn -> {
//                val user = (authState as AuthState.UserLoggedIn).user
//                Toast.makeText(navController.context, "Welcome, ${user?.name}!", Toast.LENGTH_SHORT).show()
//                navController.navigate("home") {
//                    popUpTo("login") { inclusive = true } // Xóa màn hình login khỏi back stack
//                }
//                viewModel.resetAuthState() // Reset trạng thái sau khi điều hướng
//            }
//            else -> {}
//        }
//    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E2A44))
                .padding(paddingValues)
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
//        when (authState) {
//            is AuthState.Loading -> CircularProgressIndicator()
//            is AuthState.Error -> {
//                val error = (authState as AuthState.Error).message
//                Text(text = error, color = MaterialTheme.colorScheme.error)
//            }
//            else -> {

            // Thêm hình ảnh biểu đồ tài chính
            Image(
                painter = painterResource(id = R.drawable.financial_chart),
                contentDescription = "Financial Chart",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.TopCenter) // Đặt hình ảnh ở trên cùng
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color.Black,
                            focusedBorderColor = Color.Black
                        ),
                        isError = errorMessage != null
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null // Xóa lỗi khi người dùng bắt đầu nhập lại
                        },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Icon"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color.Black,
                            focusedBorderColor = Color.Black
                        ),
                        isError = errorMessage != null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                // Kiểm tra cơ bản trước khi gọi API
                                if (email.isBlank() || password.isBlank()) {
                                    errorMessage = "Please fill in both email and password"
                                    return@launch
                                }

                                isLoading = true
                                val success = authService.login(email, password)
                                isLoading = false

                                if (success) {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = "Invalid email or password"
                                    snackbarHostState.showSnackbar("Invalid email or password")
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
                            Text("Đăng nhập", modifier = Modifier.padding(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { navController.navigate("signup") }) {
                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Bạn chưa có tài khoản? Đăng ký", color = Color.Gray)
                            Text("Đăng nhập bằng Google!", color = Color.Gray)
                        }
                    }

                    Button(
                        onClick = signIn,
                        modifier = Modifier
                            .size(76.dp)
                            .padding(top = 8.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gg),
                            contentDescription = "Google Sign In",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }
        }
    }
}
//    }
//}

// thêm vào để giả định
//@Preview (showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    // Tạo một NavHostController giả (mock) hoặc bỏ qua điều hướng trong Preview
//    val navController = rememberNavController() // Sử dụng rememberNavController() cho Preview
//    LoginScreen( navController,)
//}