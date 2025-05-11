package com.example.doancoso.presentation.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso.R
import com.example.doancoso.data.models.ExpenseItem
import com.example.doancoso.data.repository.AuthService
import com.example.doancoso.data.repository.ExpenseItemService
import com.example.doancoso.data.repository.TextRecognitionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemsScreen(
    navController: NavHostController,
    authService: AuthService,
    expenseItemService: ExpenseItemService
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var rawAmount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("Thu nhập") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val textRecognitionManager = TextRecognitionManager()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
//            coroutineScope.launch {
//                val recognizedText = textRecognitionManager.recognizeTextFromImage(context, it)
//
//                Log.d("AddItemsScreen", "Recognized text:\n$recognizedText")
//
//                // ✅ Regex tìm tất cả số tiền có dạng 10,000 hoặc 54000
//                val moneyRegex = Regex("""\d{1,3}(?:[.,]\d{3})+|\d{5,}""")
//
//                // ✅ Lấy tất cả các số tiền
//                val allAmounts = moneyRegex.findAll(recognizedText).map {
//                    it.value.replace("[.,]".toRegex(), "").toLongOrNull() ?: 0L
//                }.toList()
//
//                Log.d("AddItemsScreen", "All money values: $allAmounts")
//
//                // ✅ Lấy số cuối cùng (giả định là tổng), chia cho 1000
//                val totalAmount = if (allAmounts.isNotEmpty()) allAmounts.last() else 0L
//                rawAmount = (totalAmount / 1000).toString()
//
//                Log.d("AddItemsScreen", "Final total (divided by 1000): $rawAmount")
//
//                // ✅ Trích xuất ngày, giữ nguyên regex cũ để tìm "Ngày: 18/02/2019"
//                val dateRegex = Regex("""Ngày:\s*(\d{1,2}/\d{1,2}/\d{4})""")
//                date = dateRegex.find(recognizedText)?.groupValues?.get(1) ?: ""
//                Log.d("AddItemsScreen", "Date found: $date")
//
//                note = "Thông tin từ hóa đơn"
//            }

            coroutineScope.launch {
                val recognizedText = textRecognitionManager.recognizeTextFromImage(context, it)

                Log.d("AddItemsScreen", "Recognized text:\n$recognizedText")

                // ✅ Regex tìm tất cả số tiền có dạng 10,000 hoặc 54000
                val moneyRegex = Regex("""\d{1,3}(?:[.,]\d{3})+|\d{5,}""")

                // ✅ Lấy tất cả các số tiền
                val allAmounts = moneyRegex.findAll(recognizedText).map {
                    it.value.replace("[.,]".toRegex(), "").toLongOrNull() ?: 0L
                }.toList()

                Log.d("AddItemsScreen", "All money values: $allAmounts")

                // ✅ Lấy số cuối cùng (giả định là tổng), chia cho 1000
                val totalAmount = if (allAmounts.isNotEmpty()) allAmounts.last() else 0L
                rawAmount = (totalAmount / 1000).toString()

                Log.d("AddItemsScreen", "Final total (divided by 1000): $rawAmount")

                // ✅ Trích xuất ngày theo nhiều định dạng như "Ngày:", "ngày in:", hoặc chỉ có ngày
                val dateRegex = Regex("""(?i)(ngày\s*(in)?\s*[:\-]?\s*)?(\d{1,2}/\d{1,2}/\d{4})""")
                val dateMatch = dateRegex.find(recognizedText)
                date = dateMatch?.groups?.get(3)?.value ?: ""
                Log.d("AddItemsScreen", "Date found: $date")

                note = "Thông tin từ hóa đơn"
            }

        }
    }

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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.home1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(0.9f)
                .background(Color(0xCCFFFFFF), RoundedCornerShape(16.dp))
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

            val formattedDisplay = remember(rawAmount) {
                try {
                    val fullAmount = rawAmount.toLong() * 1000
                    NumberFormat.getNumberInstance(Locale("vi", "VN")).format(fullAmount)
                } catch (e: Exception) {
                    ""
                }
            }

            OutlinedTextField(
                value = rawAmount,
                onValueChange = { rawAmount = it.filter { ch -> ch.isDigit() } },
                label = { Text("Số tiền (nghìn VNĐ)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textStyle = TextStyle(color = textColor),
                singleLine = true,
                placeholder = { Text("VNĐ") },
                trailingIcon = {
                    if (formattedDisplay.isNotBlank()) {
                        Text(text = "${formattedDisplay} VNĐ", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )

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

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Danh mục") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )

            var expanded by remember { mutableStateOf(false) }
            val types = listOf("Thu nhập", "Chi phí")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                OutlinedTextField(
                    value = transactionType,
                    onValueChange = {},
                    label = { Text("Loại giao dịch") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
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

            // Hình ảnh hóa đơn (với icon ở cuối dòng)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Hình ảnh hóa đơn (nếu có)",
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFE0F7FA), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Chọn ảnh",
                        tint = primaryColor
                    )
                }
            }

            Button(
                onClick = {
                    if (rawAmount.isBlank() || note.isBlank() || date.isBlank() || category.isBlank()) {
                        return@Button
                    }

                    val amountValue = rawAmount.toDoubleOrNull()?.times(1000) ?: return@Button
                    val finalAmount = if (transactionType == "Chi phí") -amountValue else amountValue

                    val expense = ExpenseItem(
                        date = date,
                        timestamp = System.currentTimeMillis(),
                        category = category,
                        amount = finalAmount,
                        note = note,
                        type = transactionType,
                        imageUri = imageUri?.toString()
                    )

                    coroutineScope.launch {
                        val success = expenseItemService.addExpense(expense)
                        if (success) {
                            navController.navigate(Screen.Home.route)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Lưu", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}
