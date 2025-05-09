package com.example.doancoso.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso.data.repository.AuthService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    navController: NavHostController,
    authService: AuthService
) {
    val backgroundDark = Color(0xFF1C1C1E)
    val cardColor = Color(0xFF3F51B5)
    val grayText = Color(0xFFB0B0B0)

    var amount by remember { mutableStateOf("") }
    var isRecurring by remember { mutableStateOf(false) }

    val groupList = listOf("Ăn uống", "Giải trí", "Mua sắm", "Học tập")
    var expanded by remember { mutableStateOf(false) }
    var selectedGroup by remember { mutableStateOf(groupList.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundDark)
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Huỷ", color = Color.White)
            Text("Thêm ngân sách", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dropdown
        Text("Nhóm ngân sách", color = Color.White, fontWeight = FontWeight.Medium)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedGroup,
                onValueChange = {},
                readOnly = true,
                label = { Text("Chọn nhóm", color = Color.Black) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                textStyle = TextStyle(color = Color.Black),
                colors = ExposedDropdownMenuDefaults.textFieldColors(

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                groupList.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group, color = Color.White) },
                        onClick = {
                            selectedGroup = group
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Số tiền
        SettingItem(
            icon = Icons.Default.AttachMoney,
            title = "Số tiền",
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("VND", color = grayText)
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        modifier = Modifier.width(120.dp)
                    )
                }
            }
        )

        // Ngày
        SettingItem(
            icon = Icons.Default.DateRange,
            title = "Tháng này (01/05 - 31/05)"
        )

        // Tổng cộng
        SettingItem(
            icon = Icons.Default.Public,
            title = "Tổng cộng"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Lặp lại ngân sách này", color = Color.White, fontWeight = FontWeight.Medium)
                Text(
                    "Ngân sách được tự động lặp lại ở kỳ hạn tiếp theo.",
                    color = grayText,
                    fontSize = 12.sp
                )
            }

            Switch(
                checked = isRecurring,
                onCheckedChange = { isRecurring = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Nút Lưu
        Button(
            onClick = { /* TODO: Save budget */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Lưu", color = Color.White)
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector? = null,
    title: String,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val cardColor = Color(0xFF2C2C2E)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
            .let { if (onClick != null) it.clickable { onClick() } else it },
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(it, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(title, color = Color.White, modifier = Modifier.weight(1f))
        trailingContent?.invoke()
    }

    Spacer(modifier = Modifier.height(12.dp))
}
