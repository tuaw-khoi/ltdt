package com.example.doancoso.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.navigation.NavHostController
import com.example.doancoso.data.repository.AuthService

@Composable
fun BudgetScreen(
    navController: NavHostController,
    authService: AuthService
) {
    val primaryBlue = Color(0xFFA3BBD7)
    val backgroundDark = Color(0xFF1C1C1E)
    val cardColor = Color(0xFF3F51B5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
            .padding(16.dp)
    ) {
        Text(
            text = "Ngân sách đang áp dụng",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tổng ngân sách
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Số tiền bạn có thể chi",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Text(
                    text = "5,010,000.00",
                    color = primaryBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("5.05 M", color = Color.White)
                    Text("40 K", color = Color.White)
                    Text("22 ngày", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        navController.navigate(Screen.AddBudget.route) // 👈 Điều hướng đến màn hình AddBudget
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Text("Tạo Ngân sách", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


    }
}
