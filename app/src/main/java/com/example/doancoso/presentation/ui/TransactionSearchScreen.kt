package com.example.doancoso.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.doancoso.data.models.ExpenseItemHistory
import com.example.doancoso.data.repository.AuthService
import com.example.doancoso.data.repository.ExpenseItemService
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSearchScreen(navController: NavHostController, authService: AuthService) {
    var startDate by remember { mutableStateOf<LocalDate?>(LocalDate.now().minusMonths(1)) }
    var endDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val expenseItemService = remember { ExpenseItemService() }
    var allSearchResultsForDateRange by remember { mutableStateOf<List<ExpenseItemHistory>>(emptyList()) }
    var searchResults by remember { mutableStateOf<List<ExpenseItemHistory>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    var searchError by remember { mutableStateOf<String?>(null) }
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    var selectedTab by remember { mutableStateOf(0) } // State cho tab
    val tabs = listOf("Toàn bộ", "Tiền vào", "Tiền ra")

    // Lọc searchResults dựa trên selectedTab và allSearchResultsForDateRange
    LaunchedEffect(selectedTab, allSearchResultsForDateRange) {
        searchResults = when (selectedTab) {
            1 -> allSearchResultsForDateRange.filter { it.type == "Thu nhập" }
            2 -> allSearchResultsForDateRange.filter { it.type == "Chi phí" }
            else -> allSearchResultsForDateRange
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử giao dịch") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Filled.Home, contentDescription = "Trang chủ")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                // TabRow cho lọc nhanh
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = startDate?.format(dateFormatter) ?: "",
                        onValueChange = {},
                        label = { Text("Từ ngày") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = { showStartDatePicker = true }) {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = "Chọn từ ngày")
                            }
                        },
                        isError = searchError != null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = endDate?.format(dateFormatter) ?: "",
                        onValueChange = {},
                        label = { Text("Đến ngày") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = { showEndDatePicker = true }) {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = "Chọn đến ngày")
                            }
                        },
                        isError = searchError != null,
                    )
                }
                if (searchError != null) {
                    Text(
                        text = searchError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Quý khách có thể truy vấn xem lịch sử giao dịch trong vòng 1 năm với khoảng thời gian tối đa trong 1 lần tìm kiếm là 31 ngày",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        searchError = null
                        if (startDate == null || endDate == null) {
                            searchError = "Vui lòng chọn đầy đủ ngày."
                            return@Button
                        }
                        val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)
                        if (daysBetween > 31) {
                            searchError = "Khoảng thời gian tìm kiếm tối đa là 31 ngày."
                            return@Button
                        }
                        val oneYearAgo = LocalDate.now().minusYears(1)
                        if (startDate?.isBefore(oneYearAgo) == true || endDate?.isBefore(oneYearAgo) == true) {
                            searchError = "Chỉ có thể xem lịch sử giao dịch trong vòng 1 năm."
                            return@Button
                        }

                        coroutineScope.launch {
                            try {
                                val allResults = expenseItemService.getExpensesByDateRange(startDate, endDate, null) // Lấy tất cả kết quả theo ngày
                                allSearchResultsForDateRange = allResults
                                // Lọc ban đầu theo tab hiện tại
                                searchResults = when (selectedTab) {
                                    1 -> allSearchResultsForDateRange.filter { it.type == "Thu nhập" }
                                    2 -> allSearchResultsForDateRange.filter { it.type == "Chi phí" }
                                    else -> allSearchResultsForDateRange
                                }
                            } catch (e: Exception) {
                                Log.e("TransactionSearch", "Lỗi tìm kiếm: ${e.message}")
                                searchError = "Đã xảy ra lỗi khi tìm kiếm."
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !showStartDatePicker && !showEndDatePicker
                ) {
                    Text("Tìm kiếm", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (searchResults.isEmpty() && searchError == null) {
                    Text(
                        "Không có giao dịch nào trong khoảng thời gian này",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (searchResults.isNotEmpty()) {
                    Text("Kết quả tìm kiếm:")
                    LazyColumn {
                        items(searchResults) { transaction ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = transaction.date,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (!transaction.note.isNullOrBlank()) {
                                        Text(
                                            text = transaction.note,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Text(
                                    text = "${if (transaction.type == "Thu nhập") "+" else ""}${transaction.amount} VND",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (transaction.type == "Thu nhập") Color.Green else Color.Red
                                )
                            }
                            Divider()
                        }
                    }
                }

                // DatePickerDialog cho ngày bắt đầu
                if (showStartDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showStartDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                startDate = startDatePickerState.selectedDateMillis?.let {
                                    LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                                }
                                showStartDatePicker = false
                            }) {
                                Text("Xác nhận")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showStartDatePicker = false }) {
                                Text("Hủy")
                            }
                        }
                    ) {
                        DatePicker(state = startDatePickerState)
                    }
                }
                // DatePickerDialog cho ngày kết thúc
                if (showEndDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showEndDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                endDate = endDatePickerState.selectedDateMillis?.let {
                                    LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                                }
                                showEndDatePicker = false
                            }) {
                                Text("Xác nhận")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showEndDatePicker = false }) {
                                Text("Hủy")
                            }
                        }
                    ) {
                        DatePicker(state = endDatePickerState)
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TransactionSearchScreenPreview() {
    val navController = rememberNavController()
    val authService = remember { AuthService() } // Bạn có thể cần mock AuthService cho Preview
    TransactionSearchScreen(navController = navController, authService = authService)
}