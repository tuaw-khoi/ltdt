package com.example.doancoso.presentation.ui

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.doancoso.R
import com.example.doancoso.data.models.User
import com.example.doancoso.data.repository.AuthService

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun EditProfile(navController: NavHostController, uid: String, authService: AuthService) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    val userState = remember { mutableStateOf<User?>(null) }
    val selectedImageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val file = File(context.cacheDir, "avatar.jpg") // Define a temporary file to save the bitmap

    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }

    LaunchedEffect(uid) {
        val user = authService.fetchUser(uid)
        user?.let {
            userName = it.name
            email = it.email
            phone = it.phone
            gender = it.gender
            address = it.address
            avatarUrl = it.avatarUrl ?: ""  // Set initial avatar URL
        }
    }

    val pastelBackground = Color(0xFFDCEEF2)
    val cardColor = Color.White
    val primaryColor = Color(0xFF1976D2)
    val coroutineScope = rememberCoroutineScope()  // Nhớ coroutine scope

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pastelBackground)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Chỉnh sửa hồ sơ",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = avatarUri?.let { rememberAsyncImagePainter(it) }
                    ?: if (avatarUrl.isNotEmpty()) rememberAsyncImagePainter(avatarUrl)
                    else painterResource(id = R.drawable.ic_avatar_placeholder),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-16).dp, y = 8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .shadow(6.dp, shape = CircleShape)
                    .clickable {
                        pickImageLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Change photo",
                    tint = primaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Tên") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Giới tính", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                gender = "Nam"
                            }
                            .padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = gender == "Nam",
                            onClick = { gender = "Nam" }
                        )
                        Text("Nam", modifier = Modifier.padding(start = 8.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                gender = "Nữ"
                            }
                    ) {
                        RadioButton(
                            selected = gender == "Nữ",
                            onClick = { gender = "Nữ" }
                        )
                        Text("Nữ", modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Địa chỉ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
//        val imgService = imgService() // Initialize the service

        Button(
            onClick = {
                if (selectedImageUri == null && avatarUrl.isEmpty()) {
                    Toast.makeText(context, "Vui lòng chọn một ảnh", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                coroutineScope.launch {
//                    try {
//                        val newAvatarUrl = if (selectedImageUri != null) {
//                            val file = imgService().getFileFromUri(context, selectedImageUri!!)
//                            if (file != null) {
//                                imgService().uploadAvatarToCloudinary(file) { url ->
//                                    url?.let {
//                                        // Update user with new avatar
//                                        val updateResult = authService.updateUser(
//                                            uid, userName, email, phone, gender, address, it
//                                        )
//                                        if (updateResult) {
//                                            navController.popBackStack()
//                                        } else {
//                                            Toast.makeText(context, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show()
//                                        }
//                                    } ?: run {
//                                        Toast.makeText(context, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                                return@launch
//                            } else {
//                                Toast.makeText(context, "Không thể xử lý ảnh", Toast.LENGTH_SHORT).show()
//                                return@launch
//                            }
//                        } else {
//                            avatarUrl // Keep existing avatar if no new one selected
//                        }
//
//                        // If no new image was selected, just update other fields
//                        val updateResult = authService.updateUser(
//                            uid, userName, email, phone, gender, address, newAvatarUrl
//                        )
//                        if (updateResult) {
//                            navController.popBackStack()
//                        } else {
//                            Toast.makeText(context, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show()
//                        }
//                    } catch (e: Exception) {
//                        Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
//                        Log.e("EditProfile", "Error updating profile", e)
//                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
            shape = RoundedCornerShape(14.dp),
            elevation = ButtonDefaults.buttonElevation(6.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Lưu thay đổi", color = Color.White)
        }
}
    }