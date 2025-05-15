package com.example.doancoso.presentation.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionDetailScreen(navController: NavHostController, authService: AuthService) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Toàn bộ", "Tiền vào", "Tiền ra")

    val expenseItemService = remember { ExpenseItemService() }
    var userName by remember { mutableStateOf("Đang tải...") }
    var expenseItems by remember { mutableStateOf<List<ExpenseItem>>(emptyList()) }
    var totalIncomeRaw by remember { mutableStateOf(0.0) }
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    val formattedTotalIncome: String by remember(totalIncomeRaw) {
        derivedStateOf { numberFormat.format(totalIncomeRaw) + " VND" }
    }
    var isTotalIncomeVisible by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val tealColor = Color(0xFF64C5B1)
    val lightTealColor = Color(0xFFC5D2D1)

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
                expenseItems = items.sortedByDescending { it.timestamp }
                totalIncomeRaw = items.filter { it.amount > 0 }.sumOf { it.amount.toDouble() }
            } catch (e: Exception) {
                Log.e("TransactionScreen", "Lỗi lấy giao dịch: ${e.message}")
            }
        }
    }

    val filteredTransactions = remember(selectedTab, expenseItems) {
        when (selectedTab) {
            1 -> expenseItems.filter { it.amount > 0 }
            2 -> expenseItems.filter { it.amount < 0 }
            else -> expenseItems
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ExpenseItem?>(null) }

    val onDeleteTransaction: (ExpenseItem) -> Unit = { item ->
        itemToDelete = item
        showDeleteDialog = true
    }

    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; itemToDelete = null },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa giao dịch này?") },
            confirmButton = {
                Button(onClick = {
                    itemToDelete?.let { expense ->
                        coroutineScope.launch {
                            val keyToDelete = expense.id
                            if (keyToDelete.isNotEmpty()) {
                                val isDeleted = expenseItemService.deleteExpenseByKey(keyToDelete)
                                if (isDeleted) {
                                    try {
                                        val updatedExpenses = expenseItemService.getAllExpenses()
                                        expenseItems = updatedExpenses.sortedByDescending { it.timestamp }
                                        totalIncomeRaw = updatedExpenses.filter { it.amount > 0 }.sumOf { it.amount.toDouble() }
                                    } catch (e: Exception) {
                                        Log.e(
                                            "TransactionScreen",
                                            "Lỗi làm mới giao dịch sau xóa: ${e.message}"
                                        )
                                    }
                                } else {
                                    Log.e("TransactionScreen", "Xóa giao dịch thất bại.")
                                }
                            } else {
                                Log.e("TransactionScreen", "Không tìm thấy ID của giao dịch để xóa.")
                            }
                        }
                    }
                    showDeleteDialog = false
                    itemToDelete = null
                }) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false; itemToDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
            }

            Text(
                "Chi tiết giao dịch",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title.uppercase()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = tealColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = userName,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isTotalIncomeVisible) "Số dư: **** VND" else "Số dư: $formattedTotalIncome",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 14.sp
                    )
                }
                IconButton(onClick = { isTotalIncomeVisible = !isTotalIncomeVisible }) {
                    Icon(
                        imageVector = if (isTotalIncomeVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (isTotalIncomeVisible) "Ẩn số dư" else "Hiện số dư",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { navController.navigate("search") }) {
                Text("Tìm kiếm thêm", color = MaterialTheme.colorScheme.primary)
            }
        }

        TransactionList(transactions = filteredTransactions, onDelete = onDeleteTransaction, itemBackgroundColor = lightTealColor)
    }
}

@Composable
fun TransactionItem(expense: ExpenseItem, onDelete: (ExpenseItem) -> Unit, backgroundColor: Color = MaterialTheme.colorScheme.surface) {
    val isIncoming = expense.amount > 0
    val amountColor = if (isIncoming) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    val formattedAmount = try {
        val amount = kotlin.math.abs(expense.amount)
        val formatted = numberFormat.format(amount)
        "${if (isIncoming) "+" else "-"}$formatted VND"
    } catch (e: Exception) {
        Log.e("TransactionItem", "Lỗi format số: ${e.message}")
        "0 VND"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = expense.note ?: "(Không có ghi chú)",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedAmount,
                        color = amountColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { onDelete(expense) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Xóa giao dịch", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun TransactionList(transactions: List<ExpenseItem>, onDelete: (ExpenseItem) -> Unit, itemBackgroundColor: Color) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(transactions) { expense ->
            TransactionItem(expense = expense, onDelete = onDelete, backgroundColor = itemBackgroundColor)
        }
    }
}