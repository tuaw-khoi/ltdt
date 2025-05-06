package com.example.expensetracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

data class ChatMessage(
    val isSentByUser: Boolean,
    val content: String,
    val amount: String,
    val date: String,
    val details: String,
    val total: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val messages = listOf(
        ChatMessage(
            isSentByUser = false,
            content = "Đóng thẻ tập gym 1 tháng - Hàn\n3/4/2026 - 1.200.000 VND",
            amount = "1.200.000 VND",
            date = "28/02",
            details = "Phân loại: Sức khỏe & Dinh dưỡng - Tổng thu nhập tháng 2: 17.803.200 VND",
            total = "Tổng chi tiêu tháng 2: 17.803.200 VND - 7:05 PM",
            icon = Icons.Default.DirectionsRun
        ),
        ChatMessage(
            isSentByUser = true,
            content = "Cắt tóc - 80.000 VND",
            amount = "80.000 VND",
            date = "01/03",
            details = "Phân loại: Làm đẹp & Chăm sóc cá nhân - Tổng thu nhập tháng 3: 0 VND",
            total = "Tổng chi tiêu tháng 3: 80.000 VND - 4:25 PM",
            icon = Icons.Default.Spa
        ),
        ChatMessage(
            isSentByUser = true,
            content = "Mẹ đi chợ - 50.000 VND",
            amount = "50.000 VND",
            date = "01/03",
            details = "Phân loại: Đi lại - Tổng thu nhập tháng 3: 0 VND",
            total = "Tổng chi tiêu tháng 3: 130.000 VND - 4:25 PM",
            icon = Icons.Default.LocalTaxi
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sổ chi tiêu cá nhân",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFFB2DFDB)
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    placeholder = {
                        Text(
                            "Nhập tin nhắn...",
                            color = Color.Gray.copy(alpha = 0.6f)
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                        cursorColor = Color(0xFF4CAF50)
                    )
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE0F7FA),
                            Color(0xFFB2EBF2)
                        )
                    )
                ),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(message)
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val alignment = if (message.isSentByUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (message.isSentByUser) Color(0xFF4CAF50) else Color.White
    val textColor = if (message.isSentByUser) Color.White else Color.Black
    val timeColor = if (message.isSentByUser) Color.White.copy(alpha = 0.8f) else Color.Gray

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.isSentByUser) {
            Icon(
                imageVector = message.icon,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(24.dp)
            )
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Ngày: ${message.date}", fontSize = 12.sp, color = timeColor)
                Text(text = message.details, fontSize = 12.sp, color = timeColor)
                Text(
                    text = message.total,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = timeColor
                )
            }
        }

        if (message.isSentByUser) {
            Icon(
                imageVector = message.icon,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatScreenPreview() {
    MaterialTheme {
        ChatScreen()
    }
}