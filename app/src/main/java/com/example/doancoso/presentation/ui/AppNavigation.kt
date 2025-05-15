package com.example.doancoso.presentation.ui

import com.example.doancoso.data.repository.ExpenseItemService
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doancoso.data.models.ExpenseItem
import com.example.doancoso.data.repository.AuthService
import com.google.android.gms.auth.api.signin.GoogleSignInClient

data class NotificationItem(
    val message: String
)

@Composable
fun NotificationScreen(notifications: List<NotificationItem>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Thông Báo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Chưa có thông báo nào.")
            }
        } else {
            LazyColumn(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                items(notifications) { notification ->
                    Text(notification.message)
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Signup : Screen("signup")
    data object Home : Screen("home")
    data object AddItems : Screen("addItems")
    data object Profile : Screen("profile")
    data object EditProfile : Screen("editProfile/{uid}")
    data object Wallet : Screen("wallet")
    data object History : Screen("history")
    data object Search : Screen("search")
    data object Chatbot : Screen("chatbot")
    data object Notifications : Screen("notifications")

}

@Composable
fun AppNavigation(
    expenseItemService: ExpenseItemService,
    navController: NavHostController = rememberNavController(),
    authService: AuthService,
    googleSignInClient: GoogleSignInClient,
    signIn: () -> Unit
) {
    val transactionNotifications = remember { mutableStateListOf<NotificationItem>() }

    NavHost(navController = navController, startDestination = Screen.Welcome.route) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, authService, signIn = signIn)
        }
        composable(Screen.Signup.route) {
            SignupScreen(navController, authService)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, authService)
        }
        composable(Screen.AddItems.route) {
            AddItemsScreen(
                navController = navController,
                authService = authService,
                expenseItemService = expenseItemService,
                onTransactionAdded = { item ->
                    transactionNotifications.add(NotificationItem("Bạn vừa thêm giao dịch ${item.type.lowercase()} '${item.category.lowercase()}'"))
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, authService)
        }
        composable(Screen.Chatbot.route) {
            ChatbotScreen(navController, authService)
        }
        composable(
            route = Screen.EditProfile.route,
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            EditProfile(navController, uid, authService)
        }

        composable(Screen.History.route) {
            TransactionDetailScreen(navController, authService)
        }
        composable(Screen.Search.route) {
            TransactionSearchScreen(navController, authService)
        }

        composable(Screen.Notifications.route) {
            NotificationScreen(notifications = transactionNotifications)
        }
    }
}