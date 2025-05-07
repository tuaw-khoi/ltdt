package com.example.doancoso.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun AddItemsScreen(
    navController: NavHostController,
    authService: AuthService, // Bạn có thể không cần AuthService trực tiếp ở đây nữa
    expenseItemService: ExpenseItemService
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("") }

    val backgroundColor = Color(0xFF005A5A)
    val textColor = Color.White
    val inputBorderColor = Color(0xFFE0F2F1)
    val buttonColor = Color(0xFFFFEB3B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Thêm giao dịch",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { char -> char.isDigit() } },
            label = { Text("Số tiền", color = textColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textStyle = TextStyle(color = textColor),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = inputBorderColor,
                unfocusedBorderColor = inputBorderColor,
                cursorColor = textColor
            )
        )

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Ghi chú", color = textColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textStyle = TextStyle(color = textColor),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = inputBorderColor,
                unfocusedBorderColor = inputBorderColor,
                cursorColor = textColor
            )
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Ngày (vd: 06/05/2025)", color = textColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textStyle = TextStyle(color = textColor),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = inputBorderColor,
                unfocusedBorderColor = inputBorderColor,
                cursorColor = textColor
            )
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Danh mục", color = textColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textStyle = TextStyle(color = textColor),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = inputBorderColor,
                unfocusedBorderColor = inputBorderColor,
                cursorColor = textColor
            )
        )

        // Ô nhập liệu loại thu/chi
        OutlinedTextField(
            value = transactionType,
            onValueChange = { transactionType = it },
            label = { Text("Loại (Thu nhập/Chi phí)", color = textColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textStyle = TextStyle(color = textColor),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = inputBorderColor,
                unfocusedBorderColor = inputBorderColor,
                cursorColor = textColor
            )
        )

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                val finalAmount = if (transactionType.equals("Chi phí", ignoreCase = true)) -amountValue else amountValue

                val expense = ExpenseItem(
                    date = date,
                    category = category,
                    amount = finalAmount,
                    note = note,
                    type = transactionType
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val isSuccess = expenseItemService.addExpense(expense)
                    withContext(Dispatchers.Main) {
                        if (isSuccess) {
                            // Xử lý khi lưu thành công, ví dụ điều hướng về trang chủ
                            navController.navigate(Screen.Home.route)
                            // Bạn có thể thêm một thông báo nhỏ cho người dùng nếu muốn
                            // Toast.makeText(LocalContext.current, "Giao dịch đã được lưu", Toast.LENGTH_SHORT).show()
                        } else {
                            // Xử lý khi lưu thất bại, ví dụ hiển thị thông báo lỗi
                            // Snackbar.make(view, "Lỗi khi lưu giao dịch", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Lưu", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

    }
}