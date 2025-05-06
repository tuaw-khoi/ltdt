package com.example.doancoso.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso.data.repository.AuthService

@Composable
fun HomeScreen(navController: NavHostController, authService: AuthService) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF80DEEA), // Soft teal
                            Color(0xFFFFCCBC)  // Light coral
                        )
                    )
                )
                .padding(16.dp)
                .padding(bottom = 56.dp) // chừa chỗ cho BottomNavBar
        ) {
            Text(
                text = "Biểu đồ thu chi",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                PieChartComposable()
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Balance Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tổng tiền còn lại",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "0 VNĐ",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Danh mục thu chi",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            ExpenseCategory("Tiền điện", "150.000 VNĐ", Color(0xFFBB86FC))
            ExpenseCategory("Tiền học", "550.000 VNĐ", Color(0xFF03DAC5))
            ExpenseCategory("Tiền đi chợ", "500.000 VNĐ", Color(0xFFFFC107))
        }

        // Bottom Navigation ở dưới cùng
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            BottomNavBar(navController)
        }
    }
}

@Composable
fun PieChartComposable() {
    val entries = listOf(
        PieEntry(150000f, "Tiền điện"),
        PieEntry(550000f, "Tiền học"),
        PieEntry(500000f, "Tiền đi chợ")
    )

    val colors = listOf(
        Color(0xFFBB86FC),
        Color(0xFF03DAC5),
        Color(0xFFFFC107)
    )

    val total = entries.sumOf { it.value.toDouble() }.toFloat()
    val sweepAngles = entries.map { entry ->
        (entry.value / total) * 360f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = size.width / 2 * 0.8f
                val holeRadius = radius * 0.3f

                sweepAngles.forEachIndexed { index, sweepAngle ->
                    val color = colors[index % colors.size]
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2)
                    )
                    startAngle += sweepAngle
                }

                drawCircle(
                    color = Color.White,
                    radius = holeRadius,
                    center = Offset(centerX, centerY)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            entries.forEachIndexed { index, entry ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {

                    Spacer(modifier = Modifier.width(8.dp))

                }
            }
        }
    }
}

data class PieEntry(val value: Float, val label: String)

@Composable
fun ExpenseCategory(name: String, amount: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {

            Spacer(modifier = Modifier.width(8.dp))

        }

    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    NavigationBar(containerColor = Color(0xFF00796B)) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = Color.White
                )
            },
            selected = true,
            onClick = {
                navController.navigate(Screen.Home.route) // optional: quay về Home
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chat",
                    tint = Color.White
                )
            },
            selected = false,
            onClick = {
                // Tùy ý: thêm chức năng nếu muốn
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Yellow
                )
            },
            selected = false,
            onClick = {
                navController.navigate(Screen.AddItems.route)
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White
                )
            },
            selected = false,
            onClick = {
                // Tùy ý: thêm chức năng nếu muốn
            }
        )
    }
}
