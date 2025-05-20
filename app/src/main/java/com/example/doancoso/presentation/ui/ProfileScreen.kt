package com.example.doancoso.presentation.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso.R
import com.example.doancoso.data.repository.AuthService
import com.google.firebase.auth.FirebaseAuth
import com.example.doancoso.data.models.User

@Composable
fun ProfileScreen(navController: NavHostController, authService: AuthService) {
    val pastelBackground = Color(0xFFDCEEF2)
    val cardColor = Color.White
    val primaryColor = Color(0xFF6BC1C0)

    val userState = remember { mutableStateOf<User?>(null) }
    val refreshTrigger = remember { mutableStateOf(false) } // Dùng để trigger LaunchedEffect sau khi cập nhật

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "No user logged in"
    Log.d("ProfileScreen", "UID: $uid")

    // Load user khi uid thay đổi hoặc sau khi cập nhật hồ sơ
    LaunchedEffect(uid, refreshTrigger.value) {
        if (uid.isNotEmpty()) {
            val user = authService.fetchUser(uid)
            userState.value = user
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pastelBackground)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_avatar_placeholder),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userState.value?.name ?: "Đang tải...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userState.value?.email ?: "",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("editProfile/$uid") // Điều hướng tới màn chỉnh sửa
                        refreshTrigger.value = !refreshTrigger.value // Trigger reload sau khi quay lại
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chỉnh sửa hồ sơ", color = Color.White)
                }
            }
        }

        Text("Tài khoản", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))

        // Thêm các ô thông tin người dùng như số điện thoại, giới tính
        ProfileInfoOption(label = "Số điện thoại", value = userState.value?.phone ?: "Chưa có số điện thoại")
        ProfileInfoOption(label = "Giới tính", value = userState.value?.gender ?: "Chưa chọn giới tính")
        ProfileInfoOption(label = "Địa chỉ", value = userState.value?.address ?: "Chưa cập nhật")

        Spacer(modifier = Modifier.height(12.dp))

        // Các tùy chọn khác như Tài khoản, Cài đặt
        Text("Tùy chọn", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
        ProfileOption(icon = Icons.Default.Receipt, label = "Tôi") {}
        ProfileOption(icon = Icons.Default.Settings, label = "Cài đặt") {}

        Spacer(modifier = Modifier.height(12.dp))

        Text("Khác", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
        ProfileOption(icon = Icons.Default.ExitToApp, label = "Đăng xuất") {
            authService.logout()
            navController.navigate("login") {
                popUpTo("profile") { inclusive = true }
            }
        }
    }
}

@Composable
fun ProfileInfoOption(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(label, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.weight(1f))
            Text(value, fontSize = 16.sp, color = Color.Black)
        }
    }
}

@Composable
fun ProfileOption(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF6BC1C0)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, fontSize = 16.sp, color = Color.Black)
        }
    }
}
