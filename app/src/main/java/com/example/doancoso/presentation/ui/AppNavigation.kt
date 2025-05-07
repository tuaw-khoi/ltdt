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
    data object Login : Screen("login")
    data object Signup : Screen("signup")
    data object Home : Screen("home")
    data object AddItems : Screen("addItems")
    data object Profile : Screen("profile")
    data object EditProfile : Screen("editProfile")
}

@Composable
fun AppNavigation(
    expenseItemService: ExpenseItemService,
    navController: NavHostController = rememberNavController(),
    authService: AuthService,
    googleSignInClient: GoogleSignInClient,
    signIn: () -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
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
            AddItemsScreen(navController, authService, expenseItemService)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, authService)
        }

        composable(
            "editProfile/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType },)
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            EditProfile(navController, uid, authService)
        }

    }
}
