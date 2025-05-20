package com.example.doancoso.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionNotification(
    category: String,
    amount: Double,
    type: String, // "Thu nhập" hoặc "Chi phí"
    note: String? = null,
    onDismiss: () -> Unit // Callback để đóng thông báo
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainer
    val textColor = MaterialTheme.colorScheme.onSurface
    val amountColor = if (type == "Thu nhập") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val icon = if (type == "Thu nhập") Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward
    val sign = if (type == "Thu nhập") "+" else "-"
    val formattedAmount = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(amount.toInt())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = type,
                    tint = amountColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                    if (!note.isNullOrBlank()) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            Text(
                text = "$sign$formattedAmount VNĐ",
                color = amountColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun PreviewTransactionNotification() {
    Column(modifier = Modifier.padding(16.dp)) {
        TransactionNotification(
            category = "Ăn uống",
            amount = 50000.0,
            type = "Chi phí",
            note = "Bữa trưa",
            onDismiss = {}
        )
        Spacer(modifier = Modifier.height(8.dp))
        TransactionNotification(
            category = "Lương",
            amount = 1000000.0,
            type = "Thu nhập",
            onDismiss = {}
        )
    }
}