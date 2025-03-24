import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doancoso.presentation.ui.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: AuthViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Chi tiêu", color = Color.White)
                        Text("Thu nhập", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    Text("0", color = Color.White, modifier = Modifier.padding(end = 16.dp))
                    Text("2M", color = Color.White)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Thêm giao dịch */ },
                containerColor = Color(0xFF4CAF50) // Màu xanh lá như trong ảnh
            ) {
                Text("+", fontSize = 24.sp, color = Color.White)
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Thống kê chi tiêu
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Thống kê", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExpenseStatItem("Chi tiêu không hóa đơn", "66%")
                    ExpenseStatItem("Bà già dùng", "27%")
                    ExpenseStatItem("An ủyng", "7%")
                }
            }

            // Danh sách giao dịch
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(getSampleTransactions()) { transaction ->
                        TransactionItem(transaction.date, transaction.amount)
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }

            // Nút hành động
            Column(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = { /* Xử lý kế hoạch ngân */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kê hoạch ngân", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun ExpenseStatItem(title: String, percentage: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Text(text = percentage, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun TransactionItem(date: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = date, style = MaterialTheme.typography.bodySmall)
        Text(text = amount, style = MaterialTheme.typography.bodyMedium)
    }
}

data class Transaction(val date: String, val amount: String)

fun getSampleTransactions(): List<Transaction> {
    return listOf(
        Transaction("Household appliances, 02/03/2023", "400,000.00 VNĐ"),
        Transaction("Food, 02/03/2023", "85,000.00 VNĐ"),
        Transaction("Food, 01/03/2023", "24,000.00 VNĐ")
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Tổng quan") },
            label = { Text("Tổng quan") },
            selected = true, // Tạm thời chọn mặc định
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Sổ giao dịch") },
            label = { Text("Sổ giao dich") },
            selected = false,
            onClick = { navController.navigate("transactions") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Lập kế hoạch") },
            label = { Text("Lập kế hoạch") },
            selected = false,
            onClick = { navController.navigate("plans") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Tài khoản") },
            label = { Text("Tài khoản") },
            selected = false,
            onClick = { navController.navigate("account") }
        )
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        // Thêm các màn hình khác khi cần
        composable("transactions") { Text("Transactions Screen") }
        composable("plans") { Text("Plans Screen") }
        composable("account") { Text("Account Screen") }
    }
}
