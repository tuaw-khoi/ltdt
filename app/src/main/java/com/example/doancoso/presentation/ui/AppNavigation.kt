package com.example.doancoso.presentation.ui

import com.example.doancoso.data.repository.ExpenseItemService
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doancoso.data.repository.AuthService
import com.google.android.gms.auth.api.signin.GoogleSignInClient

//import com.google.android.play.core.integrity.au

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Signup : Screen("signup")
    data object Home : Screen("home")
    data object AddItems : Screen("addItems")
    data object Profile : Screen("profile")
    data object EditProfile : Screen("editProfile/{uid}") // Corrected route definition
    data object Wallet : Screen("wallet")
    data object History : Screen("history")
    data object Search : Screen("search")
    data object Chatbot : Screen("chatbot")
    data object AddBudget : Screen("add_budget")
    data object BudgetGroup : Screen("budget_group")
}

@Composable
fun AppNavigation(
    expenseItemService: ExpenseItemService,
    navController: NavHostController = rememberNavController(),
    authService: AuthService,
    googleSignInClient: GoogleSignInClient,
    signIn: () -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.Welcome.route) { // Đặt Welcome là màn hình bắt đầu
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController) // Truyền navController vào WelcomeScreen
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, authService=authService
                , signIn = signIn)
        }
        composable(Screen.Signup.route) {
            SignupScreen(navController, authService)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.AddItems.route) {
            AddItemsScreen(navController, authService, expenseItemService)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, authService)
        }
        composable(Screen.Chatbot.route) {
            ChatbotScreen(navController, authService)
        }
        composable(
            route = Screen.EditProfile.route, // Use the corrected route
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            EditProfile(navController, uid, authService)
        }

        composable(Screen.Wallet.route) {
            BudgetScreen(navController, authService)
        }
        composable(Screen.History.route) {
            TransactionDetailScreen(navController, authService)
        }
        composable(Screen.Search.route) {
            TransactionSearchScreen(navController, authService)
        }
        composable(Screen.AddBudget.route) {
            AddBudgetScreen(navController, authService)
        }

    }
}
