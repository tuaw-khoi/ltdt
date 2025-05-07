package com.example.doancoso.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text("Ví của tôi", fontWeight = FontWeight.Bold)
            },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8FCD9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Họ và tên", fontWeight = FontWeight.SemiBold)
                    Switch(checked = true, onCheckedChange = {})
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("•••• •••• 2000", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("VND 1,200,873", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("6/24")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Tính năng", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(12.dp))

        val features = listOf(
            "Chuyển tiền" to Icons.Filled.Send,
            "QR Pay" to Icons.Filled.QrCode,
            "Tiết kiệm" to Icons.Filled.Savings,
            "Đầu tư" to Icons.Filled.TrendingUp,
            "Thanh toán hóa đơn" to Icons.Filled.Receipt,
            "Đặt lịch chuyển tiền" to Icons.Filled.Schedule,
            "Nạp tiền điện thoại" to Icons.Filled.Phone,
            "Tính năng khác" to Icons.Filled.MoreHoriz
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(features) { (label, icon) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(label, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Ưu đãi & khuyến mại", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalletScreenPreview() {
    WalletScreen()
}