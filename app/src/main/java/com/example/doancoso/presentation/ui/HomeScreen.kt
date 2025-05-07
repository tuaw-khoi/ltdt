package com.example.doancoso.presentation.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso.data.models.ResultGetExpense
import com.example.doancoso.data.repository.AuthService
import com.example.doancoso.data.repository.ExpenseItemService
import kotlinx.coroutines.launch
import java.util.Random

fun generateColorForCategory(category: String): Color {
    val random = Random(
        category.hashCode().toLong()
    ) // Sử dụng hashCode của category làm seed để đảm bảo màu sắc nhất quán
    return Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 255)
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    authService: AuthService,
    expenseItemService: ExpenseItemService = remember { ExpenseItemService() }
) {
    val incomeData = remember { mutableStateListOf<ResultGetExpense>() }
    val expenseData = remember { mutableStateListOf<ResultGetExpense>() }
    val totalIncome = remember { mutableStateOf(0.0) }
    val totalExpense = remember { mutableStateOf(0.0) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            val incomes = expenseItemService.getExpensesByType("thu")
            Log.d("HomeScreen", "Incomes: $incomes")
            incomeData.clear()
            incomeData.addAll(incomes)
            totalIncome.value = incomes.firstOrNull()?.total ?: 0.0

            val expenses = expenseItemService.getExpensesByType("chi")
            Log.d("HomeScreen", "expenses: $expenses")
            expenseData.clear()
            expenseData.addAll(expenses)
            totalExpense.value = expenses.firstOrNull()?.total ?: 0.0
        }
    }

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
                .padding(bottom = 56.dp)
        ) {
            Text(
                text = "Tổng quan thu chi",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Biểu đồ thu
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    PieChartComposable(expenseData = incomeData, title = "Thu")
                }

                // Biểu đồ chi
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    PieChartComposable(expenseData = expenseData, title = "Chi")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tổng thu
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    TotalBalanceCard(title = "Tổng thu", amount = totalIncome.value)
                }

                // Tổng chi
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336).copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    TotalBalanceCard(title = "Tổng chi", amount = totalExpense.value)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Chi tiết giao dịch",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "Các khoản thu",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            incomeData.forEach { income ->
                ExpenseCategory(
                    name = income.category,
                    amount = "+${income.amount.toInt()} VNĐ",
                    color = generateColorForCategory(income.category)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Các khoản chi",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            expenseData.forEach { expense ->
                ExpenseCategory(
                    name = expense.category,
                    amount = "-${expense.amount.toInt()} VNĐ",
                    color = generateColorForCategory(expense.category)
                )
            }
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
fun TotalBalanceCard(title: String, amount: Double) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${amount.toInt()} VNĐ",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PieChartComposable(expenseData: List<ResultGetExpense>, title: String = "") {
    val total = expenseData.sumOf { it.amount }.toFloat()
    val pieEntries = expenseData.map {
        val color = generateColorForCategory(it.category)
        PieEntry(it.amount.toFloat(), it.category, color)
    }

    val sweepAngles = pieEntries.map { entry ->
        (entry.value / total) * 360f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(150.dp) // Giảm kích thước biểu đồ
                .clip(RoundedCornerShape(16.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = size.width / 2 * 0.8f
                val holeRadius = radius * 0.3f

                pieEntries.forEachIndexed { index, entry ->
                    val sweepAngle = (entry.value / total) * 360f
                    drawArc(
                        color = entry.color,
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

        Spacer(modifier = Modifier.height(8.dp))
        if (pieEntries.isNotEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                pieEntries.forEach { entry ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(entry.color, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${entry.label} (${entry.value.toInt()} VNĐ - ${
                                String.format(
                                    "%.1f",
                                    (entry.value / total) * 100
                                )
                            }%)", fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            Text("Không có dữ liệu", color = Color.Gray)
        }
    }
}

data class PieEntry(val value: Float, val label: String, val color: Color)

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
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = name, fontWeight = FontWeight.Medium)
        }
        Text(text = amount)
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
                navController.navigate(Screen.Profile.route)
            }
        )
    }
}