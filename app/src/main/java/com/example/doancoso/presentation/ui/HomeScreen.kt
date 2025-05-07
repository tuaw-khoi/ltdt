package com.example.doancoso.presentation.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso.R
import com.example.doancoso.data.models.ResultGetExpense
import com.example.doancoso.data.repository.AuthService
import com.example.doancoso.data.repository.ExpenseItemService
import kotlinx.coroutines.launch
import java.util.Random

fun generateColorForCategory(category: String): Color {
    val random = Random(category.hashCode().toLong())
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

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val incomes = expenseItemService.getExpensesByType("thu")
            incomeData.clear()
            incomeData.addAll(incomes)
            totalIncome.value = incomes.firstOrNull()?.total ?: 0.0

            val expenses = expenseItemService.getExpensesByType("chi")
            expenseData.clear()
            expenseData.addAll(expenses)
            totalExpense.value = expenses.firstOrNull()?.total ?: 0.0
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // White transparent overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.3f))
        )

        // Scrollable content
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .padding(bottom = 72.dp)
        ) {
            Text(
                text = "📊 Tổng quan thu chi",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF004D40),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f).height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    PieChartComposable(incomeData, title = "Thu")
                }

                Card(
                    modifier = Modifier.weight(1f).height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    PieChartComposable(expenseData, title = "Chi")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(Color(0xFFB2DFDB)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    TotalBalanceCard("Tổng thu", totalIncome.value)
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(Color(0xFFFFCDD2)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    TotalBalanceCard("Tổng chi", totalExpense.value)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "💰 Chi tiết giao dịch",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text("Các khoản thu", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 4.dp))
            incomeData.forEach {
                ExpenseCategory(it.category, "+${it.amount.toInt()} VNĐ", generateColorForCategory(it.category))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Các khoản chi", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 4.dp))
            expenseData.forEach {
                ExpenseCategory(it.category, "-${it.amount.toInt()} VNĐ", generateColorForCategory(it.category))
            }
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier.fillMaxSize(),
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
        Text(title, fontSize = 18.sp, color = Color(0xFF004D40), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("${amount.toInt()} VNĐ", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PieChartComposable(expenseData: List<ResultGetExpense>, title: String = "") {
    val total = expenseData.sumOf { it.amount }.toFloat()
    val pieEntries = expenseData.map {
        PieEntry(it.amount.toFloat(), it.category, generateColorForCategory(it.category))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title.isNotEmpty()) {
            Text("Biểu đồ $title", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        }

        Box(modifier = Modifier.size(140.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = size.width / 2 * 0.8f
                val holeRadius = radius * 0.3f

                pieEntries.forEach { entry ->
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

                drawCircle(Color.White, holeRadius, Offset(centerX, centerY))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (pieEntries.isNotEmpty()) {
            Column(horizontalAlignment = Alignment.Start) {
                pieEntries.forEach { entry ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Box(modifier = Modifier.size(10.dp).background(entry.color, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${entry.label} (${entry.value.toInt()} VNĐ - ${"%.1f".format((entry.value / total) * 100)}%)", fontSize = 12.sp)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            Text(amount, color = Color.DarkGray)
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF00796B)) },
            selected = true,
            onClick = { navController.navigate(Screen.Home.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat", tint = Color(0xFF00796B)) },
            selected = false,
            onClick = { navController.navigate(Screen.Wallet.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFFFFA000)) },
            selected = false,
            onClick = { navController.navigate(Screen.AddItems.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF00796B)) },
            selected = false,
            onClick = { /* Mở Profile nếu cần */ }
        )
    }
}
