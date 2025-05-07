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
import com.example.doancoso.data.models.ExpenseItem
import com.example.doancoso.data.models.ResultGetExpense
import com.example.doancoso.data.repository.AuthService
import com.example.doancoso.data.repository.ExpenseItemService
import kotlinx.coroutines.launch
import java.util.*

fun generateColorForCategory(category: String): Color {
    val random = Random(category.hashCode().toLong())
    return Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 255)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    authService: AuthService,
    expenseItemService: ExpenseItemService = remember { ExpenseItemService() }
) {
    val selectedType = remember { mutableStateOf("Thu nhập") }
    val selectedTimeFrame = remember { mutableStateOf("day") }
    val chartData = remember { mutableStateListOf<ResultGetExpense>() }
    val recentTransactions = remember { mutableStateListOf<ExpenseItem>() }
    val coroutineScope = rememberCoroutineScope()

    fun loadData() {
        coroutineScope.launch {
            val grouped = expenseItemService.getGroupedExpensesBy(selectedType.value, selectedTimeFrame.value)
            val all = expenseItemService.getAllExpenses()
                .filter { it.type == selectedType.value }
                .sortedByDescending { it.timestamp }
            chartData.clear()
            chartData.addAll(grouped)
            recentTransactions.clear()
            recentTransactions.addAll(all)
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.3f))
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .padding(bottom = 72.dp)
        ) {
            Text(
                text = "📊 Thống kê tài chính",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D40),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SegmentedButton(
                options = listOf("day", "week", "month"),
                labels = listOf("Ngày", "Tuần", "Tháng"),
                selected = selectedTimeFrame.value,
                onSelect = {
                    selectedTimeFrame.value = it
                    loadData()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            var expanded by remember { mutableStateOf(false) }
            val types = listOf("Thu nhập", "Chi phí")
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedType.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Loại giao dịch") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.uppercase()) },
                                onClick = {
                                    selectedType.value = type
                                    expanded = false
                                    loadData()
                                }
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                PieChartComposable(chartData, title = "${selectedType.value.uppercase()} - ${selectedTimeFrame.value}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lịch sử giao dịch
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🕓 Lịch sử giao dịch",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF004D40)
                )
                TextButton(onClick = { navController.navigate("history") }) {
                    Text("Xem thêm", color = Color(0xFF00796B))
                }
            }

            recentTransactions.take(2).forEach { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp), // Tăng padding giữa các thẻ
                    colors = CardDefaults.cardColors(
                        if (tx.type == "thu") Color(0xFFDFF0D8) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(16.dp) // Thêm bo góc mềm mại
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), // Thêm padding vào bên trong Row
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f) // Đảm bảo text có không gian
                        ) {
                            Text(
                                text = tx.category,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp, // Cỡ chữ lớn hơn
                                color = Color.Black
                            )
                            Text(
                                text = tx.note ?: "Không có ghi chú", // Hiển thị ghi chú nếu có
                                fontSize = 14.sp,
                                color = Color.Gray // Màu ghi chú xám nhạt
                            )
                        }
                        Text(
                            text = "${tx.amount.toInt()} VNĐ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, // Cỡ chữ số tiền lớn hơn
                            color = if (tx.type == "thu") Color(0xFF388E3C) else Color(0xFFC62828) // Màu cho Thu và Chi
                        )
                    }
                }
            }


        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            BottomNavBar(navController)
        }
    }
}

@Composable
fun SegmentedButton(options: List<String>, selected: String, onSelect: (String) -> Unit, labels: List<String> = options) {
    Row {
        options.forEachIndexed { index, option ->
            val isSelected = option == selected
            Button(
                onClick = { onSelect(option) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF00796B) else Color.White,
                    contentColor = if (isSelected) Color.White else Color.DarkGray
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(36.dp)
            ) {
                Text(labels[index].uppercase())
            }
        }
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
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))

        Box(modifier = Modifier.size(200.dp)) {
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
fun BottomNavBar(navController: NavHostController) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF00796B)) },
            selected = true,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat", tint = Color(0xFF00796B)) },
            selected = false,
            onClick = { navController.navigate("wallet") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "addItems", tint = Color(0xFFFFA000)) },
            selected = false,
            onClick = { navController.navigate("addItems") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF00796B)) },
            selected = false,
<<<<<<< HEAD
            onClick = {
                navController.navigate(Screen.Profile.route)
            }
=======
            onClick = { navController.navigate("profile") }
>>>>>>> cacffd14003f7f4a6ac4222089e4dc48d211d89f
        )
    }
}git add .