package com.example.doancoso.presentation.ui

import java.text.NumberFormat
import java.util.Locale
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Create // Ví dụ: Icon bút chì cho thêm mới
import androidx.compose.material.icons.filled.NoteAdd // Một ví dụ khác: Icon thêm ghi chú
import androidx.compose.material.icons.filled.PlaylistAdd // Thêm vào danh sách

fun generateColorForCategory(category: String): Color {
    val random = Random(category.hashCode().toLong())
    return Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 255)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    expenseItemService: ExpenseItemService = remember { ExpenseItemService() }
) {
    val selectedType = remember { mutableStateOf("Thu nhập") }
    val selectedTimeFrame = remember { mutableStateOf("Ngày") }
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
            painter = painterResource(id = R.drawable.home5),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.0f))
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 72.dp)
        )
        {
            // Add chatbot button at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("chatbot") },
                    containerColor = Color(0xFF6BC1C0),
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = "Chatbot Hỗ Trợ",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            SegmentedButton(
                options = listOf("Ngày", "Tuần", "Tháng"),
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
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
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
            PieChartComposable(chartData, title = "${selectedType.value.uppercase()} - ${selectedTimeFrame.value}")

            Spacer(modifier = Modifier.height(16.dp))

            // Lịch sử giao dịch
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🕓 Giao dịch gần đây",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF263238)
                )
                TextButton(onClick = { navController.navigate("history") }) {
                    Text("Xem thêm", color = Color(0xFF655E5E))
                }
            }

            recentTransactions.take(2).forEach { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = tx.category,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Text(
                                text = tx.note ?: "Không có ghi chú",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        val amountColor = if (tx.type == "Thu nhập") Color(0xFF4CAF50) else Color(0xFFE53935)
                        val sign = if (tx.type == "Thu nhập") "+" else ""

// Định dạng tiền tệ với dấu chấm ngăn cách hàng nghìn
                        val formattedAmount = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(tx.amount.toInt())

                        Text(
                            text = "$sign$formattedAmount VNĐ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = amountColor
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
fun SegmentedButton(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    labels: List<String> = options
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE0F2F1))
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color(0xFF6BC1C0) else Color.White)
                    .clickable { onSelect(option) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labels[index].uppercase(),
                    color = if (isSelected) Color.White else Color(0xFF6BC1C0),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
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
            .padding(12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.85f))
            .padding(16.dp)
            .wrapContentHeight()
    ) {
        Text(
            title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Pie chart
            Box(
                modifier = Modifier
                    .size(140.dp)
            ) {
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

            // Legend list
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (pieEntries.isNotEmpty()) {
                    pieEntries.forEach { entry ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 1.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(entry.color, shape = RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val formattedValue = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(entry.value.toInt())
                            val percentage = String.format("%.1f", entry.value / total * 100)

                            Text(
                                text = "${entry.label}: $formattedValue VNĐ ($percentage%)",
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    Text("Không có dữ liệu", color = Color.Gray)
                }
            }
        }
    }
}


data class PieEntry(val value: Float, val label: String, val color: Color)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val selectedItem = remember { mutableStateOf("home") } // Theo dõi item được chọn

    NavigationBar(
        containerColor = Color(0xFF6BC1C0),
        tonalElevation = 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selectedItem.value == "home") Color.White else Color.White.copy(alpha = 0.7f)
                )
            },
            selected = selectedItem.value == "home",
            onClick = {
                selectedItem.value = "home"
                navController.navigate("home")
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Chat,
                    contentDescription = "wallet",
                    tint = if (selectedItem.value == "wallet") Color.White else Color.White.copy(alpha = 0.7f)
                )
            },
            selected = selectedItem.value == "wallet",
            onClick = {
                selectedItem.value = "wallet"
                navController.navigate("wallet")
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.AddBox, // Hoặc Icons.Default.NoteAdd, hoặc Icons.Default.PlaylistAdd
                    contentDescription = "addItems",
                    tint = if (selectedItem.value == "addItems") Color.White else Color.White.copy(alpha = 0.7f)
                )
            },
            selected = selectedItem.value == "addItems",
            onClick = {
                selectedItem.value = "addItems"
                navController.navigate("addItems")
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (selectedItem.value == "profile") Color.White else Color.White.copy(alpha = 0.7f)
                )
            },
            selected = selectedItem.value == "profile",
            onClick = {
                selectedItem.value = "profile"
                navController.navigate("profile")
            }
        )
    }
}