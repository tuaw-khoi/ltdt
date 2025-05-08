package com.example.doancoso.presentation.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso.data.models.ExpenseItem
import com.example.doancoso.data.repository.AuthService
import com.example.doancoso.data.repository.ExpenseItemService
import kotlinx.coroutines.launch

@Composable
fun TransactionDetailScreen(navController: NavHostController, authService: AuthService) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Toàn bộ", "Tiền vào", "Tiền ra")

    val expenseItemService = remember { ExpenseItemService() }
    var userName by remember { mutableStateOf("Đang tải...") }
    var expenseItems by remember { mutableStateOf<List<ExpenseItem>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    // Lấy dữ liệu người dùng và chi tiêu
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                userName = expenseItemService.getUserName()
                Log.d("TransactionScreen", "User name: $userName")
            } catch (e: Exception) {
                Log.e("TransactionScreen", "Lỗi lấy tên người dùng: ${e.message}")
            }

            try {
                val items = expenseItemService.getAllExpenses()
                Log.d("TransactionScreen", "Số lượng giao dịch: ${items.size}")
                expenseItems = items
            } catch (e: Exception) {
                Log.e("TransactionScreen", "Lỗi lấy giao dịch: ${e.message}")
            }
        }
    }

    // Lọc giao dịch theo tab
    val filteredTransactions = remember(selectedTab, expenseItems) {
        when (selectedTab) {
            1 -> expenseItems.filter { it.amount > 0 }
            2 -> expenseItems.filter { it.amount < 0 }
            else -> expenseItems
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Sử dụng màu nền của theme
            .padding(top = 35.dp) // Giảm padding top
    ) {
        // Sử dụng TabRow
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface, // Màu nền của tab row
            contentColor = MaterialTheme.colorScheme.onSurface, // Màu chữ/icon trên tab row
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary // Màu indicator
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title.uppercase()) } // In hoa (tùy chọn)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng trắng

        // Sử dụng Card cho AccountHeader
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer // Màu nền card
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = userName,
                    color = MaterialTheme.colorScheme.onPrimaryContainer, // Màu chữ
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lịch sử giao dịch",
                style = MaterialTheme.typography.titleMedium, // Sử dụng kiểu chữ của theme
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { /* TODO: Xử lý tìm kiếm */ }) {
                Text("Tìm kiếm thêm", color = MaterialTheme.colorScheme.primary)
            }
        }

        TransactionList(transactions = filteredTransactions)
    }
}

@Composable
fun TransactionItem(expense: ExpenseItem) {
    val isIncoming = expense.amount > 0
    val amountColor = if (isIncoming) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    val formattedAmount = try {
        val amount = kotlin.math.abs(expense.amount)
        val formatted = "%,.0f".format(amount)
        "${if (isIncoming) "+" else "-"}$formatted VND"
    } catch (e: Exception) {
        Log.e("TransactionItem", "Lỗi format số: ${e.message}")
        "0 VND"
    }

    Card( // Sử dụng Card cho mỗi item
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = expense.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = expense.note ?: "(Không có ghi chú)",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formattedAmount,
                    color = amountColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun TransactionList(transactions: List<ExpenseItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(transactions) { expense ->
            TransactionItem(expense = expense)
        }
    }
}