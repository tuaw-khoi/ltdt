package com.example.doancoso.presentation.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso.data.models.ExpenseItem
import com.example.doancoso.data.repository.AuthService
import com.example.doancoso.data.repository.ExpenseItemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemsScreen(
    navController: NavHostController,
    authService: AuthService,
    expenseItemService: ExpenseItemService
) {
    val context = LocalContext.current

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("Thu nhập") }

    val backgroundColor = Color(0xFFF4F9F9)
    val primaryColor = Color(0xFF0288D1)
    val textColor = Color.Black

    val calendar = Calendar.getInstance()

    val showDatePicker = {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                date = String.format("%02d/%02d/%d", day, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Thêm Giao Dịch",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Amount
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' } },
            label = { Text("Số tiền") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            textStyle = TextStyle(color = textColor),
            singleLine = true
        )

        // Note
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Ghi chú") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            singleLine = true
        )

        // Date picker field
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text("Chọn ngày") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { showDatePicker() },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Chọn ngày",
                    tint = primaryColor
                )
            },
            singleLine = true
        )

        // Category
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Danh mục") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            singleLine = true
        )

        // Type: Dropdown
        var expanded by remember { mutableStateOf(false) }
        val types = listOf("Thu nhập", "Chi phí")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            OutlinedTextField(
                value = transactionType,
                onValueChange = {},
                label = { Text("Loại giao dịch") },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                types.forEach { selection ->
                    DropdownMenuItem(
                        text = { Text(selection) },
                        onClick = {
                            transactionType = selection
                            expanded = false
                        }
                    )
                }
            }
        }

        // Button Save
        Button(
            onClick = {
                if (amount.isBlank() || note.isBlank() || date.isBlank() || category.isBlank()) {
                    // Show snackbar / toast thông báo lỗi (tùy bạn tích hợp thêm)
                    return@Button
                }

                val amountValue = amount.toDoubleOrNull() ?: return@Button
                val finalAmount = if (transactionType == "Chi phí") -amountValue else amountValue

                val expense = ExpenseItem(
                    date = date,
                    timestamp = System.currentTimeMillis(),
                    category = category,
                    amount = finalAmount,
                    note = note,
                    type = transactionType
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val success = expenseItemService.addExpense(expense)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            navController.navigate(Screen.Home.route)
                        } else {
                            // Show error message
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue) // Changed to green
        ) {
            Text("Lưu", fontSize = 18.sp, color = Color.White)
        }
    }
}
