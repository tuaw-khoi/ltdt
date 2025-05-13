package com.example.doancoso.presentation.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.layout.ContentScale
import com.example.doancoso.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6BC1C0), Color(0xFFA8E063))
                )
            ),
        contentAlignment = Alignment.TopCenter
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Phần ảnh
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(400.dp)
                    .padding(top = 0.dp)
                    .padding(start = 0.dp, end = 0.dp),
                contentScale = ContentScale.Fit
            )

            // Phần "Free Transactions" và Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp) // Thêm padding phía trên phần text
            ) {
                Text(
                    text = "Giao dịch miễn phí",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2222)
                )

                val introductionText = remember {
                    "Dễ dàng theo dõi và quản lý thu chi cá nhân của bạn mọi lúc mọi nơi với ứng dụng di động tiện lợi này!"
                }
                var visibleText by remember { mutableStateOf("") }

                LaunchedEffect(key1 = true) {
                    while (true) {
                        introductionText.forEachIndexed { index, _ ->
                            delay(20) // Giảm thời gian delay để tăng tốc độ
                            visibleText = introductionText.substring(0, index + 1)
                        }
                        delay(1000) // Thời gian chờ trước khi lặp lại (để người dùng đọc)
                        visibleText = "" // Reset để bắt đầu lại
                    }
                }

                Text(
                    text = visibleText,
                    fontSize = 10.sp,
                    color = Color(0xFF2D2222),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(start = 55.dp, end = 50.dp),
                )

                Spacer(modifier = Modifier.height(48.dp)) // Thêm Spacer ở đây
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D7E9B)),
                    shape = RoundedCornerShape(40.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .padding(top = 80.dp)
                        .offset(x = (+30).dp), // Dịch nút qua phải
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp) // Khoảng cách giữa text và icon
                    ) {
                        Text(
                            text = "Tiếp tục ngay",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Tiếp tục",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(rememberNavController())
}
